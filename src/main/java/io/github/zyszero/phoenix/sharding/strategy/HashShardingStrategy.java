package io.github.zyszero.phoenix.sharding.strategy;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * hash sharding strategy.
 *
 * @Author: zyszero
 * @Date: 2024/8/26 21:29
 */
public class HashShardingStrategy implements ShardingStrategy {


    private final String shardingColumn;

    private final String algorithmExpression;

    public HashShardingStrategy(Properties properties) {
        this.shardingColumn = properties.getProperty("sharding-column");
        this.algorithmExpression = properties.getProperty("algorithm-expression");
    }

//    @Override
//    public List<String> getShardingColumns() {
//        return List.of(this.shardingColumn);
//    }

    @Override
    public String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> ShardingParams) {
        String expression = InlineExpressionParser.handlePlaceHolder(algorithmExpression);
        InlineExpressionParser parser = new InlineExpressionParser(expression);
        Closure<?> closure = parser.evaluateClosure();
        closure.setProperty(this.shardingColumn, ShardingParams.get(this.shardingColumn));
        return closure.call().toString();
    }
}
