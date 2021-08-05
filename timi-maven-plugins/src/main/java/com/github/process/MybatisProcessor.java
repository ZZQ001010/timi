package com.github.process;

import com.github.entity.Configuration;
import com.github.meta.Database;
import com.github.mybatis.FluentMybatisGenerator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentException;

import java.util.List;

/**
 * @author zzq
 * @date 2021/8/4
 */
public class MybatisProcessor extends AbsProcessor implements Processor{
    
    @Override
    public void process(Configuration config, MavenProject project, Log log)
            throws DocumentException, MojoExecutionException {
        log.info(">>>>>>>>>>>>>>>>>>> MybatisProcessor Will start execution.");
        this.project = project;
        buildProejct(config);
        log.info(">>>>>>>>>>>>>>>>>>> MybatisProcessor assembler execution.");
        List<Database> result = assembler(config, log);
        log.info(">>>>>>>>>>>>>>>>>>> MybatisProcessor generate execution.");
        new FluentMybatisGenerator().generateFiles(config, result, log);
    }
    
    
 
}
