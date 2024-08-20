package io.github.zyszero.phoenix.sharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Entity.
 *
 * @Author: zyszero
 * @Date: 2024/8/13 23:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String name;
    private int age;
}
