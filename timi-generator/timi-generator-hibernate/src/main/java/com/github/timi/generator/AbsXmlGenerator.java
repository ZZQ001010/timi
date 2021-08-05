package com.github.timi.generator;

import com.github.api.IGenerator;
import com.github.constants.StringConst;
import org.apache.maven.plugin.logging.Log;

import java.util.Map;


/** 
 */
public abstract class AbsXmlGenerator implements IGenerator {
    
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
        
        String path = filePath + fileName + fileSuffix;
        CodeGenerator generator = CodeGenerator.init(null);
        generator.generateFile(template, data, path);
        if(logger.isDebugEnabled()){
            logger.debug("生成XML文件：" + path);
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
        return StringConst.XML_FILE_SUFFIX;
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
    
}
