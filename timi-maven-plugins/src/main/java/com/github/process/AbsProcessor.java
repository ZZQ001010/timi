package com.github.process;

import com.github.api.assembler.erm.ERMAssembler;
import com.github.entity.Configuration;
import com.github.meta.Database;
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
 * @date 2021/8/5
 */
public abstract class AbsProcessor implements Processor {
    
    MavenProject project;
    
    protected void buildProejct(Configuration config) {
        project.addCompileSourceRoot(config.getOutputDirectory());
        Resource r = new Resource();
        ArrayList<String> in = new ArrayList<String>();
        in.add("**/client/**");
        in.add("**/shared/**");
        r.setDirectory(config.getOutputDirectory());
        r.setIncludes(in);
        project.addResource(r);
    }
    
    protected List<Database> assembler(Configuration config, Log log) throws MojoExecutionException, DocumentException {
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
}
