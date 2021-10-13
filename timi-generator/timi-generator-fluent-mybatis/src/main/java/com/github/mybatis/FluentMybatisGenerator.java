package com.github.mybatis;

import com.github.api.IGenerator;
import com.github.entity.Configuration;
import com.github.meta.Database;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

/**
 * @author zzq
 * @date 2021/8/5
 */
public class FluentMybatisGenerator implements IGenerator {
    
    @Override
    public void generateFiles(Configuration config, List<Database> source, Log log) {
        BOGenerator boGenerator = new BOGenerator();
        boGenerator.generateFiles(config, source, log);
    }
}
