package io.github.zyszero.phoenix.sharding.mybatis;

import io.github.zyszero.phoenix.sharding.engine.ShardingContext;
import io.github.zyszero.phoenix.sharding.engine.ShardingResult;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import org.springframework.stereotype.Component;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * interceptor sql.
 *
 * @Author: zyszero
 * @Date: 2024/8/14 0:39
 */
@Intercepts(
        @org.apache.ibatis.plugin.Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {java.sql.Connection.class, Integer.class}
        )
)
public class SqlStatementInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ShardingResult result = ShardingContext.get();
        if (result != null) {
            StatementHandler handler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = handler.getBoundSql();
            String sql = boundSql.getSql();
            System.out.println(" ===> SqlStatementInterceptor: " + sql);
            String targetSql = result.getTargetSqlStatement();
            if (!sql.equalsIgnoreCase(targetSql)) {
                replaceSql(boundSql, targetSql);
                System.out.println(" ===> target sql: " + targetSql);
            }
        }
        return invocation.proceed();
    }

    private static void replaceSql(BoundSql boundSql, String targetSql) throws NoSuchFieldException {
        Field field = boundSql.getClass().getDeclaredField("sql");
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        long fieldOffset = unsafe.objectFieldOffset(field);
        unsafe.putObject(boundSql, fieldOffset, targetSql);
    }
}
