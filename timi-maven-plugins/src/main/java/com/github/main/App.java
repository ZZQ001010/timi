package com.github.main;

import com.github.constants.Mode;
import com.github.entity.Configuration;
import com.github.process.HibernateProcessor;
import com.github.process.MybatisProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description：maven代码生成插件入口类 <br/>
 * Date： 2016年12月27日 上午10:23:11 <br/>
 *
 * @author zzq <br/>
 *
 * @goal entity
 *
 * @phase generate-sources
 */
public class App extends AbstractMojo {
    
    /**
     * @parameter
     * @required
     */
    private String basePackage;
    
    /**
     * @parameter default-value="target/ark-generated"
     * @required
     */
    private String outputDirectory;
    
    /**
     * @parameter
     * @required
     */
    private File[] sources;
    
    /**
     * @parameter default-value=false
     */
    private boolean trimStrings;
    
    /**
     * @parameter default-value=false
     */
    private boolean useAutoTrimType;
    
    /**
     * @parameter mode
     * @required
     */
    private Mode mode;
    
    /**
     * <i>Maven Internal</i>: Project to interact with.
     *
     * @parameter property="project"
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private MavenProject project;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        checkMode(mode);
        Configuration config = configuration();
        switch (mode) {
            case MYBATIS:
                try {
                    new MybatisProcessor().process(config, project, getLog());
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                break;
            case HIBERNATE:
                try {
                    new HibernateProcessor().process(config, project, getLog());
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    
    private void checkMode(Mode modeStr) {
        if ( !Arrays.asList(Mode.values()).contains(modeStr)) {
            throw new IllegalArgumentException("仅仅支持HIBERNATE,MYBATIS两种模式");
        }
    }

    
    private Configuration configuration() {
        Configuration configuration = new Configuration();
        configuration.setBasePackage(basePackage);
        configuration.setOutputDirectory(outputDirectory);
        configuration.setSources(sources);
        configuration.setTrimStrings(trimStrings);
        configuration.setUseAutoTrimType(useAutoTrimType);
        return configuration;
    }
    
    
    public File[] getSources() {
        return sources;
    }
    
    public void setSources(File[] sources) {
        this.sources = sources;
    }
    
    public String getBasePackage() {
        return basePackage;
    }
    
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
    
    public String getOutputDirectory() {
        return outputDirectory;
    }
    
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
 
    
    public boolean isTrimStrings() {
        return trimStrings;
    }
    
    public void setTrimStrings(boolean trimStrings) {
        this.trimStrings = trimStrings;
    }
    
    public boolean isUseAutoTrimType() {
        return useAutoTrimType;
    }
    
    public void setUseAutoTrimType(boolean useAutoTrimType) {
        this.useAutoTrimType = useAutoTrimType;
    }
    
    public Mode getMode() {
        return mode;
    }
    
    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
