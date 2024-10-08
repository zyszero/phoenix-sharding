package io.github.zyszero.phoenix.sharding.config;

import io.github.zyszero.phoenix.sharding.datasource.ShardingDataSource;
import io.github.zyszero.phoenix.sharding.engine.ShardingEngine;
import io.github.zyszero.phoenix.sharding.engine.StandardShardingEngine;
import io.github.zyszero.phoenix.sharding.mybatis.SqlStatementInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sharding auto configuration.
 *
 * @Author: zyszero
 * @Date: 2024/8/14 0:23
 */
@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingAutoConfiguration {

    @Bean
    public ShardingDataSource shardingDataSource(ShardingProperties properties) {
        return new ShardingDataSource(properties);
    }

    @Bean
    public ShardingEngine shardingEngine(ShardingProperties properties) {
        return new StandardShardingEngine(properties);
    }

    @Bean
    public SqlStatementInterceptor sqlStatementInterceptor() {
        return new SqlStatementInterceptor();
    }
}
