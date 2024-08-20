package io.github.zyszero.phoenix.sharding.engine;

/**
 * Sharding context
 *
 * @Author: zyszero
 * @Date: 2024/8/14 0:17
 */
public class ShardingContext {
    private static final ThreadLocal<ShardingResult> LOCAL = new ThreadLocal<>();

    public static ShardingResult get() {
        return LOCAL.get();
    }

    public static void set(ShardingResult shardingResult) {
        LOCAL.set(shardingResult);
    }
}
