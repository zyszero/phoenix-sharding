package io.github.zyszero.phoenix.sharding.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import io.github.zyszero.phoenix.sharding.config.ShardingProperties;
import io.github.zyszero.phoenix.sharding.demo.model.User;
import io.github.zyszero.phoenix.sharding.strategy.HashShardingStrategy;
import io.github.zyszero.phoenix.sharding.strategy.ShardingStrategy;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            String table = sqlInsertStatement.getTableName().getSimpleName();
            Map<String, Object> shardingColumnsMap = new HashMap<>();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLExpr column = columns.get(i);
                SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) column;
                String columnName = columnExpr.getSimpleName();
                shardingColumnsMap.put(columnName, args[i]);
            }


            ShardingStrategy databaseStrategy = databaseStrategies.get(table);
            String targetDatabase = databaseStrategy.doSharding(actualDatabaseNames.get(table), table, shardingColumnsMap);

            ShardingStrategy tableStrategy = tableStrategies.get(table);
            String targetTable = tableStrategy.doSharding(actualDatabaseNames.get(table), table, shardingColumnsMap);
            System.out.println(" ===>>> ");
            System.out.println(" ===>>> target db.table = " + targetDatabase + "." + targetTable);
            System.out.println(" ===>>> ");
        } else {
            // TODO SELECT/UPDATE/DELETE
        }

        Object parameterObject = args[0];
        System.out.println(" ===> getObject sql statements: " + sql);
        int id = 0;
        if (parameterObject instanceof User user) {
            id = user.getId();
        } else if (parameterObject instanceof Integer userId) {
            id = userId;
        }
        return new ShardingResult(id % 2 == 0 ? "phoenix-sharding0" : "phoenix-sharding1", sql);
    }
}
