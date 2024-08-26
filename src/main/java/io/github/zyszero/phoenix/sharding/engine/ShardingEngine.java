package io.github.zyszero.phoenix.sharding.engine;

/**
 * Core sharding engine.
 *
 * @Author: zyszero
 * @Date: 2024/8/26 20:55
 */
public interface ShardingEngine {

    ShardingResult sharding(String sql, Object[] args);
}
