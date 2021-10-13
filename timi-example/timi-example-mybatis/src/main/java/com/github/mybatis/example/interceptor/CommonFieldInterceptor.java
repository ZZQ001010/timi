//package com.github.mybatis.example.interceptor;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.executor.statement.StatementHandler;
//import org.apache.ibatis.mapping.BoundSql;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.plugin.Intercepts;
//import org.apache.ibatis.plugin.Invocation;
//import org.apache.ibatis.plugin.Signature;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Field;
//import java.sql.Connection;
//
///**
// * @author zzq
// * @date 2021/8/24
// */
//@Component
//@Slf4j
//@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
//public class CommonFieldInterceptor implements Interceptor {
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
//        BoundSql boundSql = statementHandler.getBoundSql();
//        String sql = boundSql.getSql();
//        log.info("源SQL：{}", sql);
//        sql = sql.replaceAll("`", "");
//        log.info("增强后的SQL：{}", sql);
//        Field field = boundSql.getClass().getDeclaredField("sql");
//        field.setAccessible(true);
//        field.set(boundSql, sql);
//        return invocation.proceed();
//    }
//}
