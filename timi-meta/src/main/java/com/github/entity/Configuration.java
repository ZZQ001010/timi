package com.github.entity;

import java.io.File;
import java.util.List;

/**
 * @author zzq
 * @date 2021/8/4
 */
public class Configuration {
    
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
    
    private List<String> supportFileTypes;
    
    public List<String> getSupportFileTypes() {
        return supportFileTypes;
    }
    
    public void setSupportFileTypes(List<String> supportFileTypes) {
        this.supportFileTypes = supportFileTypes;
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
    
    public File[] getSources() {
        return sources;
    }
    
    public void setSources(File[] sources) {
        this.sources = sources;
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
    
    @Override
    public String toString() {
        return "Configuration{" + "basePackage='" + basePackage + '\'' + ", outputDirectory='" + outputDirectory + '\''
                + ", sources=" + sources + ", trimStrings=" + trimStrings + ", useAutoTrimType=" + useAutoTrimType
                + '}';
    }
}
