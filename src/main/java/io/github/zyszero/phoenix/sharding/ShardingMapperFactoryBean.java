package io.github.zyszero.phoenix.sharding;

import io.github.zyszero.phoenix.sharding.demo.User;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Proxy;

/**
 * Factory bean for mapper.
 *
 * @Author: zyszero
 * @Date: 2024/8/14 0:53
 */
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    public ShardingMapperFactoryBean() {
    }

    public ShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }


    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        Object proxy = super.getObject();
        SqlSession session = getSqlSession();
        Configuration configuration = session.getConfiguration();
        Class<T> clazz = getMapperInterface();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                (p, method, args) -> {
                    String mapperId = clazz.getName() + "." + method.getName();
                    MappedStatement statement = configuration.getMappedStatement(mapperId);
                    BoundSql boundSql = statement.getBoundSql(args);
                    System.out.println(" ===> getObject sql statements: " + boundSql.getSql());
                    Object parameterObject = args[0];
                    if (parameterObject instanceof User user) {
                        ShardingContext.set(new ShardingResult(user.getId() % 2 == 0 ? "phoenix-sharding0" : "phoenix-sharding1"));
                    } else if (parameterObject instanceof Integer id) {
                        ShardingContext.set(new ShardingResult(id % 2 == 0 ? "phoenix-sharding0" : "phoenix-sharding1"));
                    }
                    System.out.println(" ===> getObject sql parameters: " + boundSql.getParameterObject());

                    return method.invoke(proxy, args);
                });
    }
}
