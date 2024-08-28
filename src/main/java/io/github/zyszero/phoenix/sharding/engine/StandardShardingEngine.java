package io.github.zyszero.phoenix.sharding.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import io.github.zyszero.phoenix.sharding.config.ShardingProperties;
import io.github.zyszero.phoenix.sharding.strategy.HashShardingStrategy;
import io.github.zyszero.phoenix.sharding.strategy.ShardingStrategy;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Standard sharding engine.
 *
 * @Author: zyszero
 * @Date: 2024/8/26 21:01
 */
public class StandardShardingEngine implements ShardingEngine {

    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();

    private final MultiValueMap<String, String> actualTableNames = new LinkedMultiValueMap<>();

    private final Map<String, ShardingStrategy> databaseStrategies = new HashMap<>();

    private final Map<String, ShardingStrategy> tableStrategies = new HashMap<>();


    public StandardShardingEngine(ShardingProperties properties) {

        properties.getTables().forEach((table, tableProperties) -> {
            tableProperties.getActualDataNodes().forEach(actualDataNode -> {
                String[] split = actualDataNode.split("\\.");
                String databaseName = split[0];
                String tableName = split[1];

                actualDatabaseNames.add(databaseName, tableName);
                actualTableNames.add(tableName, databaseName);
            });
            databaseStrategies.put(table, new HashShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrategies.put(table, new HashShardingStrategy(tableProperties.getTableStrategy()));
        });
    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        String table;
        Map<String, Object> shardingColumnsMap;
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            table = sqlInsertStatement.getTableName().getSimpleName();
            shardingColumnsMap = new HashMap<>();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLExpr column = columns.get(i);
                SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) column;
                String columnName = columnExpr.getSimpleName();
                shardingColumnsMap.put(columnName, args[i]);
            }
        } else {

            // SELECT/UPDATE/DELETE
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            visitor.setParameters(List.of(args));
            sqlStatement.accept(visitor);

            LinkedHashSet<SQLName> sqlNames = new LinkedHashSet<>(visitor.getOriginalTables());
            if (sqlNames.size() > 1) {
                throw new RuntimeException("no support multi tables sharding: " + sqlNames);
            }
            table = sqlNames.iterator().next().getSimpleName();
            System.out.println(" ===>>> visitor.getOriginalTables = " + table);

            shardingColumnsMap =
                    visitor.getConditions()
                            .stream()
                            .collect(
                                    Collectors.toMap(
                                            c -> c.getColumn().getName(), c -> c.getValues().get(0)
                                    ));
            System.out.println(" ===>>> visitor.getConditions = " + shardingColumnsMap);
        }

        ShardingStrategy databaseStrategy = databaseStrategies.get(table);
        String targetDatabase = databaseStrategy.doSharding(actualDatabaseNames.get(table), table, shardingColumnsMap);
        ShardingStrategy tableStrategy = tableStrategies.get(table);
        String targetTable = tableStrategy.doSharding(actualDatabaseNames.get(table), table, shardingColumnsMap);
        System.out.println(" ===>>> ");
        System.out.println(" ===>>> target db.table = " + targetDatabase + "." + targetTable);
        System.out.println(" ===>>> ");

        return new ShardingResult(targetDatabase, sql.replace(table, targetTable));
    }
}
