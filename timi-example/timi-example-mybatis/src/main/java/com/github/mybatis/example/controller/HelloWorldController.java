package com.github.mybatis.example.controller;

import com.github.infrastructure.entity.HelloWorldEntity;
import com.github.mybatis.example.entity.User;
import com.github.mybatis.example.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    
    @Autowired
    UserMapper userMapper;

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
                mapper.query().select.yourName().end()
                        .where().pid().lt(10).end());
    }
    
    /**
     *
     *   使用原生的mapper
     */
    @GetMapping("selectAllUser")
    public User selectAllUser() {
        return userMapper.selectAll();
    }
    
    /**
     *
     *   mybatis plus
     *
     */
    @GetMapping("selectAllUser2")
    public User selectAllUser2() {
        return userMapper.selectAll();
    }
    
    /**
     *
     *   mybatis plus
     *
     */
    @GetMapping("selectPageFluMybatis")
    public PageInfo selectAllUser3(int page, int offset) {
        PageHelper.startPage(page, offset);
        List<HelloWorldEntity> helloWorldEntities = mapper.listEntity(mapper.query().selectAll());
        PageInfo pageInfo = new PageInfo(helloWorldEntities);
        return pageInfo;
    }
    
    
    /**
     *
     *   mybatis plus
     *
     */
    @GetMapping("insertPk")
    public void insertPk() {
        
    }
    
}
