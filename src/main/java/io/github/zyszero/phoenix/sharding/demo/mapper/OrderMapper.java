package io.github.zyszero.phoenix.sharding.demo.mapper;

import io.github.zyszero.phoenix.sharding.demo.model.Order;
import io.github.zyszero.phoenix.sharding.demo.model.User;
import org.apache.ibatis.annotations.*;

/**
 * Mapper for order.
 *
 * @Author: zyszero
 * @Date: 2024/8/13 23:14
 */
@Mapper
public interface OrderMapper {
    @Insert("insert into t_order(id,uid,price) values(#{id},#{uid},#{price})")
    int insert(Order order);


    @Select("select * from t_order where id = #{id} and uid = #{uid}")
    Order findById(int id, int uid);


    @Update("update t_order set price = #{price} where id = #{id} and uid = #{uid}")
    int update(Order order);


    @Delete("delete from t_order where id = #{id} and uid = #{uid}")
    int delete(int id, int uid);
}
