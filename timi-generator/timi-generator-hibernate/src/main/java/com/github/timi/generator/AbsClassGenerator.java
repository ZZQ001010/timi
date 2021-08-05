package com.github.timi.generator;

import com.github.api.IGenerator;
import com.github.constants.StringConst;
import org.apache.maven.plugin.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


/** 
 */
public abstract class AbsClassGenerator implements IGenerator {
    
    protected Log logger;
    
    protected String fileSuffix;
    
    protected String fileName;
    
    protected String filePath;
    
    protected Map<String,Object> data;
    
    protected String template;

    /**
     * generateFile:生成文件<br/>
     * date：2016年12月27日下午3:01:41<br/>
     * @author yanghm<br/>
     * <br/>
     */
    protected void generateFile(){
        fileName = initFileName();
        fileSuffix = initFileSuffix();
        filePath = initFilePath();
        template = initTemplate();
        data = initData();
        //排序import并放入data中
        List<String> imports = new ArrayList<String>(initImports());
        Collections.sort(imports);
        data.put("imports", imports);
        
        String path = filePath + fileName + fileSuffix;
        CodeGenerator generator = CodeGenerator.init(null);
        generator.generateFile(template, data, path);
        if(logger.isDebugEnabled()){
            logger.debug("生成JAVA文件：" + path);
        }
    };

    /**
     * initFileSuffix:生成文件的后缀名<br/>
     * date：2016年12月27日下午2:24:16<br/>
     * @author yanghm<br/>
     * <br/>
     * @return
     */
    protected String initFileSuffix(){
        return StringConst.CLASS_FILE_SUFFIX;
    };
    
    /**
     * initFileName:生成的文件名<br/>
     * date：2016年12月27日下午2:14:04<br/>
     * @author yanghm<br/>
     * <br/>
     * @return
     */
    protected abstract String initFileName();
    
    /**
     * initFilePath:生成的文件路径<br/>
     * date：2016年12月27日下午2:14:28<br/>
     * @author yanghm<br/>
     * <br/>
     * @return
     */
    protected abstract String initFilePath();
    
    /**
     * initTemplate:生成文件的模板<br/>
     * date：2016年12月27日下午2:15:01<br/>
     * @author yanghm<br/>
     * <br/>
     * @return
     */
    protected abstract String initTemplate();
    
    /**
     * initData:生成文件的数据<br/>
     * date：2016年12月27日下午2:15:25<br/>
     * @author yanghm<br/>
     * <br/>
     * @return
     */
    protected abstract Map<String, Object> initData();
    
    /**
     * initImports:收集生成的java文件需要import的类，去重，排序在后面逻辑中自动处理<br/>
     * date：2016年12月29日下午4:44:01<br/>
     * @author yanghm<br/>
     * <br/>
     * @return
     */
    protected abstract Set<String> initImports();
}
