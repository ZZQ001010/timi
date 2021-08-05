/**
 * Project Name:ark-maven-plugin <br/>
 * File Name:Constants.java <br/>
 * Package Name:com.sunline.ark.maven.freemaker <br/>
 * Date:2016年12月26日下午12:17:15 <br/>
 * Copyright (c) 2016, Sunline All Rights Reserved.
 * 
 */
package com.github.constants;

import java.util.HashSet;

/**
 * ClassName：Constants <br/>
 * Description：枚举类（单例，线程安全） <br/>
 * Date： 2016年12月26日 下午12:17:15 <br/>
 * 
 * @author yanghm <br/>
 * @update [修改日期:yyyy年MM月dd日 修改内容:? 修改人：?]
 */
public enum Constants {
	CONST;

	/**
	 * 用来避免重复
	 */
	public HashSet<Integer> numbers = new HashSet<Integer>();
}
