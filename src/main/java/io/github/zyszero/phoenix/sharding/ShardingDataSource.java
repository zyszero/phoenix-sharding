package io.github.zyszero.phoenix.sharding;


import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * sharding datasource.
 *
 * @Author: zyszero
 * @Date: 2024/8/14 0:01
 */
public class ShardingDataSource extends AbstractRoutingDataSource {


    public ShardingDataSource(ShardingProperties properties) {
        Map<Object, Object> datasourceMap = new LinkedHashMap<>();
        properties.getDatabases().forEach((k, v) -> {
            try {
                datasourceMap.put(k, DruidDataSourceFactory.createDataSource(v));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        setTargetDataSources(datasourceMap);
        setDefaultTargetDataSource(datasourceMap.values().iterator().next());
    }

    @Override
    protected Object determineCurrentLookupKey() {
        ShardingResult shardingResult = ShardingContext.get();
        Object key = shardingResult == null ? null : shardingResult.getTargetDataSourceName();
        System.out.println(" ===> determineCurrentLookupKey = " + key);
        return key;
    }
}
