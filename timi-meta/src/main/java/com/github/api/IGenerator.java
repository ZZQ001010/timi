package com.github.api;


import com.github.entity.Configuration;
import com.github.meta.Database;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

/**
 * ClassName：IGenerator <br/>
 * Description：文件生成接口 <br/>
 * Date：   2016年12月27日 上午11:13:31 <br/>
 * @author yanghm <br/>
 * @update [修改日期:yyyy年MM月dd日 修改内容:? 修改人：?]
 */
public interface IGenerator {
    
    /**
     * generateFiles:执行方法生成文件<br/>
     * date：2016年12月27日上午11:16:13<br/>
     * @author yanghm<br/>
     * <br/>
     */
    void generateFiles(Configuration config, List<Database> source, Log log);
    
}
