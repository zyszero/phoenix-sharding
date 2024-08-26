package io.github.zyszero.phoenix.sharding.mybatis;

import io.github.zyszero.phoenix.sharding.engine.ShardingContext;
import io.github.zyszero.phoenix.sharding.engine.ShardingEngine;
import io.github.zyszero.phoenix.sharding.engine.ShardingResult;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

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


    @Setter
    private ShardingEngine engine;


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

                    Object[] params = getParams(boundSql, args);

                    ShardingResult result = engine.sharding(boundSql.getSql(), params);
                    ShardingContext.set(result);

                    return method.invoke(proxy, args);
                });
    }

    @SneakyThrows
    private Object[] getParams(BoundSql boundSql, Object[] args) {
        Object[] params = args;
        if (args.length == 1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())) {
            Object arg = args[0];
            List<String> cols = boundSql.getParameterMappings().stream()
                    .map(ParameterMapping::getProperty)
                    .toList();
            Object[] newParams = new Object[cols.size()];
            for (int i = 0; i < cols.size(); i++) {
                newParams[i] = getFieldValue(arg, cols.get(i));
            }
            params = newParams;
        }

        return params;
    }

    private static Object getFieldValue(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
}
