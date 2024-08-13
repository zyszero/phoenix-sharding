package io.github.zyszero.phoenix.sharding;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration for sharding.
 *
 * @Author: zyszero
 * @Date: 2024/8/13 23:59
 */
@Data
@ConfigurationProperties(prefix = "spring.sharding")
public class ShardingProperties {
    private Map<String, Properties> databases = new LinkedHashMap<>();

}
