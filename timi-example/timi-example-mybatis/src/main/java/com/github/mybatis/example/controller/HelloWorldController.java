package com.github.mybatis.example.controller;

import com.github.infrastructure.entity.HelloWorldEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zzq
 * @date 2021/8/4
 */
@RestController
public class HelloWorldController {

    @Autowired
    com.github.infrastructure.mapper.HelloWorldMapper mapper;

    @GetMapping("insert")
    public void insert(int start, int end) {
        ArrayList<HelloWorldEntity> helloWorldEntities = new ArrayList<>();
        for (int item = start; item < end; item++) {
            HelloWorldEntity helloWorldEntity = new HelloWorldEntity();
            helloWorldEntity.setSayHello("hello" + item);
            helloWorldEntity.setYourName("zzq" + item);
            helloWorldEntity.setGmtCreated(new Date());
            helloWorldEntity.setGmtModified(new Date());
            helloWorldEntities.add(helloWorldEntity);
        } mapper.insertBatch(helloWorldEntities);
    }

    @GetMapping("select")
    public List<HelloWorldEntity> select() {
        return mapper.listPoJos(HelloWorldEntity.class,
                mapper.query().selectAll().where().pid().lt(10).end());
    }

}
