package io.github.zyszero.phoenix.sharding.strategy;

import java.util.List;
import java.util.Map;

/**
 * @Author: zyszero
 * @Date: 2024/8/26 21:25
 */
public interface ShardingStrategy {

    List<String> getShardingColumns();

    String doSharding(List<String> availableTargetNames,
                      String logicTableName,
                      Map<String, Object> ShardingParams);
}
