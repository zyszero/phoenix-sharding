package io.github.zyszero.phoenix.sharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order Entity
 *
 * @Author: zyszero
 * @Date: 2024/8/28 22:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private int id;
    private int uid;
    private double price;
}
