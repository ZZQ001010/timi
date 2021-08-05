package com.github.api;


import com.github.meta.Database;
import org.dom4j.DocumentException;

import java.io.File;

/**
 * ClassName：IAssembler <br/>
 * Description：数据解析组装接口 <br/>
 * Date： 2016年12月27日 上午9:49:48 <br/>
 * 
 * @author yanghm <br/>
 * @update [修改日期:yyyy年MM月dd日 修改内容:? 修改人：?]
 */
public interface IAssembler {

	public Database assemble(File sources) throws DocumentException;
}
