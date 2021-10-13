package com.github.mybatis.example;

import cn.org.atool.fluent.mybatis.base.model.SqlOp;
import cn.org.atool.fluent.mybatis.base.splice.FreeQuery;
import cn.org.atool.fluent.mybatis.segment.model.Parameters;
import com.github.infrastructure.wrapper.HelloWorldQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author zzq
 * @date 2021/8/17
 */
@Component
public class RunApp implements ApplicationRunner {
    
    @Autowired
    com.github.infrastructure.mapper.HelloWorldMapper mapper;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        HelloWorldQuery query = mapper.query().select.yourName().gmtCreated().end()
                .where.pid().lt(10)
                .and.pid().ge(100)
                .and.yourName().eq("abc")
                .end();
        String customizedSql = query.getWrapperData().getQuerySql();
        Parameters parameters = query.getWrapperData().getParameters();
        System.out.println(query.getWrapperData().getSqlSelect());
        System.out.println(query.getWrapperData().getCustomizedSql());
        System.out.println(query.getWrapperData().getWhereSql());
        System.out.println(query.getWrapperData().getLastSql());
        System.out.println(parameters);
        System.out.println(customizedSql);
    
        FreeQuery query11 = new FreeQuery(null).customizedByQuestion("" +
                        "select * from student " +
                        "where user_name like ? " +
                        "and age > ?",
                "xyz%", 20);
        System.out.println(query11.getWrapperData().getCustomizedSql());
    }
    
    public static void main(String[] args) {
        HashSet<Object> list = new HashSet<>();
        list.add("variable_1_2");
        list.add("variable_1_1");
        list.add("variable_1_3");
        List<Object> l = Arrays.asList(list.toArray());
        System.out.println(l);
        Collections.sort(l, null);
        System.out.println(l);
        String a = "SELECT your_name, gmt_created FROM hello_world WHERE pid < #{ew.wrapperData.parameters.variable_1_1}  AND  pid >= #{ew.wrapperData.parameters.variable_1_2}  AND  your_name = #{ew.wrapperData.parameters.variable_1_3}";
        String s = a.replaceAll("#\\{[^}]+\\}", "?");
        System.out.println(s);
    }
}
