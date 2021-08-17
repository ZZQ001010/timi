/**
 * Project Name:ark-maven-plugin <br/>
 * File Name:StringConstant.java <br/>
 * Package Name:.ark.maven.freemaker.maker <br/>
 * Date:2016年12月19日下午2:23:00 <br/>
 * Copyright (c) 2016, Sunline All Rights Reserved.
 * 
 */
package com.github.constants;

import java.util.Arrays;
import java.util.List;

/**
 * ClassName：StringConstant <br/>
 * Description：freemaker代码生成中用到的一些字符常量 <br/>
 * Date： 2016年12月19日 下午2:23:00 <br/>
 * 
 * @author yanghm <br/>
 * @update [修改日期:yyyy年MM月dd日 修改内容:? 修改人：?]
 */
public final class StringConst {

	/**
	 * 符号：点.
	 */
	public final static String SPOT = ".";

	/**
	 * 符号：斜杠/
	 */
	public final static String SLASH = "/";

	/**
	 * class文件名后缀
	 */
	public final static String CLASS_FILE_SUFFIX = ".java";

	/**
	 * properties文件名后缀
	 */
	public final static String PROPERTIES_FILE_SUFFIX = ".properties";
	
	/**
	 * xml文件名后缀
	 */
	public final static String XML_FILE_SUFFIX = ".xml";

	/**
	 * 国际化文件的附加包路径
	 */
	public final static String I18N = ".i18n";

	/**
	 * Hint
	 */
	public final static String HINT = "Hint";

	/**
	 * 接口的通用修饰符
	 */
	public final static List<String> INTERFACE_MODIFIERS = Arrays.asList("public", "interface");

	/**
	 * i18n国际化类文件的公用后缀名
	 */
	public final static String CONSTANTS = "Constants";
	
	/** 
	 * spring国际化中文文件后缀
	 */  
	public final static String ZH_CN = "_zh_CN";
	
	/** 
	 * spring国际化英文文件后缀
	 */  
	public final static String EN_US = "_en_US";
	
	/** 
	 * spring国际化配置文件文件名
	 */  
	public final static String I18N_CFG = "i18n-context";

	/**
	 * DOMAINCLIENT类名公共部分
	 */
	public final static String DOMAINCLIENT = "DomainClient";

	/**
	 * 国际化类继承接口的全路径
	 */
	public final static String CONSTANTS_PATH = "com.google.gwt.i18n.client.Constants";

	/**
	 * public关键字字符串
	 */
	public final static String PUBLIC = "public";

	/**
	 * string关键字字符串
	 */
	public final static String SRING = "string";
}
