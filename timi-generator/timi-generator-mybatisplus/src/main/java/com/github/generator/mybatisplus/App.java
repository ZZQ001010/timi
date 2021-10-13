package com.github.generator.mybatisplus;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @author zzq
 * @date 2021/10/13
 */
public class App
{

    
    public static void main(String[] args)
    {
    
        String username = "root";
    
        String password = "root";
    
        String url = "jdbc:mysql://127.0.0.1:3306/fluent_mybatis";
        
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("ZZQ")
                            .enableSwagger().fileOverride().outputDir("./swagger");
                })
                .packageConfig(builder -> {
                    builder.parent("com.github.generator.mybatisplus")
                            .moduleName("system")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "./mapperxml"));
                }).templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
    
}
