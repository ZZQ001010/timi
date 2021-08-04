package com.github.main;

import com.github.assembler.erm.ERMAssembler;
import com.github.meta.Database;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentException;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zzq
 * @date 2021/8/4
 *
 *
 * <plugins>
 *             <plugin>
 *                 <groupId>cn.sunline.kite.plugins</groupId>
 *                 <artifactId>kite-plugins-maven</artifactId>
 *                 <version>${kite-plugins.version}</version>
 *                 <executions>
 *                     <execution>
 *                         <goals>
 *                             <goal>entity</goal>
 *                         </goals>
 *                         <phase>generate-sources</phase>
 *                     </execution>
 *                 </executions>
 *                 <configuration>
 *                     <sources>
 *                         <source>../ccs-infrastructure/ccs.erm</source>
 *                     </sources>
 *                     <generateOption>OnlyBO</generateOption>
 *                     <parameterPackages>
 *                         <parameterPackage>cn.sunline.ccs.definition.enums</parameterPackage>
 *                         <parameterPackage>cn.sunline.ppy.dictionary.enums</parameterPackage>
 *                     </parameterPackages>
 *                     <basePackage>cn.sunline.ccs.infrastructure</basePackage>
 *                     <outputDirectory>${basedir}/target/generated-sources</outputDirectory>
 *                     <useAutoTrimType>false</useAutoTrimType>
 *                 </configuration>
 *                 <dependencies>
 *
 *                 </dependencies>
 *             </plugin>
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
    private File sources[];
    
    /**
     * @parameter default-value=false
     */
    private boolean trimStrings;
    
    /**
     * @parameter default-value=false
     */
    private boolean useAutoTrimType;
    
    /**
     * <i>Maven Internal</i>: Project to interact with.
     *
     * @parameter property="project"
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private MavenProject project;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
    
    }
}
