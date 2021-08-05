package com.github.process;

import com.github.api.IGenerator;
import com.github.api.assembler.erm.ERMAssembler;
import com.github.entity.Configuration;
import com.github.meta.Database;
import com.github.timi.generator.entity.BEntityGen;
import com.github.timi.generator.entity.EntityDefGen;
import com.github.timi.generator.entity.EntityKeyGen;
import com.github.timi.generator.entity.REntityGen;
import com.github.timi.generator.entity.TEntityGen;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zzq
 * @date 2021/8/4
 */
public class HibernateProcessor implements Processor {
    
    MavenProject project;
    
    @Override
    public void process(Configuration config, MavenProject project,  Log log) throws MojoExecutionException, DocumentException {
        this.project = project;
        buildProejct(config);
        List<Database> result = assembler(config, log);
        generate(config, result, log);
    }
    
    private void generate(Configuration config, List<Database> result, Log log) {
        IGenerator generator;
        String basePackage = config.getBasePackage();
        String outputDirectory = config.getOutputDirectory();
        for (Database database : result) {
            log.info("生成枚举类！");
            generator = new EntityDefGen(log, database, basePackage, outputDirectory);
            generator.generateFiles();
            log.info("生成BO代码！");
            generator = new BEntityGen(log, database, basePackage, outputDirectory);
            generator.generateFiles();
            log.info("生成表R对象");
            generator = new REntityGen(log, database, basePackage, outputDirectory);
            generator.generateFiles();
            log.info("生成表主键(多主键)");
            generator = new EntityKeyGen(log, database, basePackage, outputDirectory, false);
            generator.generateFiles();
            log.info("生成Q对象");
            generator = new TEntityGen(log, database, basePackage, outputDirectory, false, null);
            generator.generateFiles();
        }
    }
    
    private List<Database> assembler(Configuration config, Log log) throws MojoExecutionException, DocumentException {
        List<Database> databases = new ArrayList<Database>();
        ERMAssembler assembler = new ERMAssembler(log);
        assembler.setBasePackage(config.getBasePackage());
        for (File source : config.getSources()) {
            String ext = FilenameUtils.getExtension(source.getName());
            if ("pdm".equals(ext)) {
                // databases.add(pdmImporter.doImport(source, tablePattern));
            } else if ("erm".equals(ext)) {
                databases.add(assembler.assemble(source));
            } else {
                log.error("不支持的文件扩展名[" + ext + "]");
                throw new MojoExecutionException("不支持的文件扩展名[" + ext + "]");
            }
        }
        return databases;
    }
    
    private void buildProejct(Configuration config) {
        project.addCompileSourceRoot(config.getOutputDirectory());
        Resource r = new Resource();
        ArrayList<String> in = new ArrayList<String>();
        in.add("**/client/**");
        in.add("**/shared/**");
        r.setDirectory(config.getOutputDirectory());
        r.setIncludes(in);
        project.addResource(r);
    }
}
