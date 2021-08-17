package com.github.mybatis.example;

import cn.org.atool.fluent.mybatis.segment.model.Parameters;
import com.github.infrastructure.wrapper.HelloWorldQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
        HelloWorldQuery query = mapper.query().select.yourName().gmtCreated().end().where.pid().lt(10).end();
        String customizedSql = query.getWrapperData().getQuerySql();
        Parameters parameters = query.getWrapperData().getParameters();
        System.out.println(parameters);
        System.out.println(customizedSql);
        List<Object> objects = mapper.listObjs(query);
    }
}
