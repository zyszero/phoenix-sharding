package io.github.zyszero.phoenix.sharding.demo.mapper;

import io.github.zyszero.phoenix.sharding.demo.model.User;
import org.apache.ibatis.annotations.*;

/**
 * Mapper for user.
 * @Author: zyszero
 * @Date: 2024/8/13 23:14
 */
@Mapper
public interface UserMapper {
    @Insert("insert into user(id,name,age) values(#{id},#{name},#{age})")
    int insert(User user);


    @Select("select * from user where id = #{id}")
    User findById(int id);


    @Update("update user set name = #{name},age = #{age} where id = #{id}")
    int update(User user);


    @Delete("delete from user where id = #{id}")
    int delete(int id);
}
