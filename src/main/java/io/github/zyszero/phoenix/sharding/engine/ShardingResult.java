package io.github.zyszero.phoenix.sharding.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * sharding result.
 *
 * @Author: zyszero
 * @Date: 2024/8/14 0:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShardingResult {

    private String targetDataSourceName;

    private String targetSqlStatement;
}
