package com.github.process;

import com.github.entity.Configuration;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentException;

/**
 * @author zzq
 * @date 2021/8/4
 */
public interface Processor {
    
    void process(Configuration config, MavenProject project, Log log) throws MojoExecutionException, DocumentException;
    
}
