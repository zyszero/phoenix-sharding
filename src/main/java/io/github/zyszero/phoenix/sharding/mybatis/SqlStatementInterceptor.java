package io.github.zyszero.phoenix.sharding.mybatis;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.stereotype.Component;

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
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        System.out.println(" ===> SqlStatementInterceptor: " + boundSql.getSql());
        Object parameterObject = boundSql.getParameterObject();
        System.out.println(" ===> SqlStatementInterceptor: " + parameterObject);
        // todo 修改 sql，比如 user -> user1
        return invocation.proceed();
    }
}
