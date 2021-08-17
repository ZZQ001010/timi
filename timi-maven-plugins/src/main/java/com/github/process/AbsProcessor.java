package com.github.process;

import com.github.api.IAssembler;
import com.github.entity.Configuration;
import com.github.meta.Database;
import com.github.spi.ComponentLoader;
import com.github.utils.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zzq
 * @date 2021/8/5
 */
public abstract class AbsProcessor implements Processor {
    
    MavenProject project;
    
    private ComponentLoader<IAssembler> assemblerLoader = new ComponentLoader();
    
    private Log log ;
    
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
    
    /**
     * @author zzq
     * @date 2021/8/6 下午5:29
     *
     */
    protected List<Database> assembler(Configuration config, Log log) throws MojoExecutionException, DocumentException {
        this.log = log;
        List<Database> databases = new ArrayList<>();
        List<String> supportFileTypes = config.getSupportFileTypes();
        for (File source : config.getSources()) {
            String ext = FilenameUtils.getExtension(source.getName());
            if (!supportFileTypes.contains(ext)) {
                log.error("不支持的文件扩展名[" + ext + "]");
                throw new MojoExecutionException("不支持的文件扩展名[" + ext + "]");
            }
            databases.add(loadAssembler(ext).assemble(source, log));
        }
        return databases;
    }
    
    /**
     * 根据文件格式（文件后缀）加载对应的类加载器
     * @return
     * @author zzq
     * @date 2021/8/17 下午12:27
     */
    private IAssembler loadAssembler(String type) throws MojoExecutionException {
        List<IAssembler> assemblers = assemblerLoader.load(IAssembler.class).stream()
                .filter(assembler -> assembler.type(type)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(assemblers) || assemblers.size() != 1) {
            String msg = "文件格式["+ type +"] 预期发现[1]个Assembler但匹配["+ assemblers.size() +"]个，请检查["+ assemblers.toString() +"]的type方法";
            log.error(msg);
            throw new MojoExecutionException(msg);
        }
        return assemblers.get(0);
    }
    
}
