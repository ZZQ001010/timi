package com.github.mybatis;

import com.github.api.IGenerator;
import com.github.entity.Configuration;
import com.github.meta.Database;
import com.github.meta.Table;
import com.github.mybatis.database.DbTypeOfGenerator;
import com.github.mybatis.database.config.impl.GlobalConfig;
import com.github.mybatis.database.config.impl.TableConfigSet;
import com.github.mybatis.database.model.DataSourceSetter;
import com.github.mybatis.database.model.TableSetter;
import com.github.mybatis.javafile.template.EntityFile;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.List;

/**
 * @author zzq
 * @date 2021/8/5
 */
public class BOGenerator implements IGenerator {
    
    @Override
    public void generateFiles(Configuration config, List<Database> source, Log log) {
        source.forEach(db ->
            db.getTables().forEach(tb -> {
                genclass2(tb, config);
            })
        );
    }
    
    private void genclass2(Table tb, Configuration config) {
        try {
            // TODO 支持更多的数据库
            GlobalConfig globalConfig = new GlobalConfig();
            globalConfig.setDataSourceSetter(new DataSourceSetter(DbTypeOfGenerator.MYSQL, "", "", "", ""));
            globalConfig.setOutputDir(config.getOutputDirectory());
            globalConfig.setBasePackage(config.getBasePackage());
    
            TableConfigSet tableConfigSet = new TableConfigSet(globalConfig);
            TableSetter table = new TableSetter(tb.getTextName(), globalConfig, tableConfigSet);
            table.setEntityPrefix("");
            table.setTableName(tb.getTextName());
            table.initTable(tb.getColumns());
            EntityFile entityFile = new EntityFile(table);
            entityFile.javaFile(new File(config.getOutputDirectory()), true);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
