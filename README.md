# timi
一个自动生成dao 枚举，bo的maven插件，便捷开发生成 querydsl或fluentMybatis 相关的代码（支持两种orm框架）

# 快速开始

#### 1. 创建springboot 工程引入依赖

```xml

    <dependencies>
        <!-- https://mvnrepository.com/artifact/org.mybatis.spring.boot/mybatis-spring-boot-starter -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.2.0</version>
        </dependency>
    
    
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    
        <!-- 引入fluent-mybatis 运行依赖包, scope为compile -->
        <dependency>
            <groupId>com.github.atool</groupId>
            <artifactId>fluent-mybatis</artifactId>
            <version>${fluent-mybatis.version}</version>
            <exclusions>
                <exclusion>
                    <artifactId>mybatis</artifactId>
                    <groupId>org.mybatis</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <!-- 引入fluent-mybatis-processor, scope设置为provider 编译需要，运行时不需要 -->
        <dependency>
            <groupId>com.github.atool</groupId>
            <artifactId>fluent-mybatis-processor</artifactId>
            <scope>provided</scope>
            <version>${fluent-mybatis.version}</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
    
    
    <build>
        <plugins>
            <plugin>
                <groupId>com.github</groupId>
                <artifactId>timi-maven-plugins</artifactId>
                <version>${project.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>entity</goal>
                        </goals>
                        <phase>generate-sources</phase>
                    </execution>
                </executions>
                <configuration>
                    <sources>
                        <source>../ccs.erm</source>
                    </sources>
                    <basePackage>com.github.infrastructure</basePackage>
                    <outputDirectory>${basedir}/target/generated-sources</outputDirectory>
                    <useAutoTrimType>false</useAutoTrimType>
                    <mode>MYBATIS</mode>
                </configuration>
                <dependencies>
                
                </dependencies>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <dependencies>
                
                </dependencies>
            </plugin>
            <plugin>
                <groupId>com.mysema.maven</groupId>
                <artifactId>apt-maven-plugin</artifactId>
                <version>1.1.3</version>
                <dependencies>
                </dependencies>
                <executions>
                    <execution>
                        <goals>
                            <goal>process</goal>
                        </goals>
                        <phase>generate-sources</phase>
                    </execution>
                </executions>
                <configuration>
                    <outputDirectory>${basedir}/target/generated-sources-querydsl</outputDirectory>
                    <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

#### 2. 新建数据模型
![avater](https://tva1.sinaimg.cn/large/008i3skNly1gt5yz06umtj30zy0qg7a0.jpg)

#### 3. 编译生成Java代码

![avater](https://tva1.sinaimg.cn/large/008i3skNly1gt5z0ii8l9j30li0pgwgc.jpg)

#### 4. 开发业务代码

`com.github.mybatis.example.controller.HelloWorldController`

```java
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

```

`com.github.mybatis.example.App`

```java
package com.github.mybatis.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({"com.github.infrastructure.mapper"})
@SpringBootApplication
public class App {
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

```

5. 验证效果

![avater](https://tva1.sinaimg.cn/large/008i3skNly1gt5z5d81tfj31cf0u0gon.jpg)

