package com.github.mybatis.example;

import cn.org.atool.fluent.mybatis.metadata.DbType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({"com.github.infrastructure.mapper", "com.github.mybatis.example.mapper"})
@SpringBootApplication
public class App {
    
    public static void main(String[] args) {
        // 设置数据库字段的反义处理
        DbType.MYSQL.setEscapeExpress("?");
        SpringApplication.run(App.class, args);
    }
}
