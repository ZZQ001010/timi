package com.github.api.assembler.utils;


/** 
 * <p>
 * TODO 完善类描述
 * </p>
 * @version 1.0 2017年10月26日 linxc 修改内容:初版
 */
public class DomanUtil {

	public static String[] getValues(String[] kv) {
		String[] values = new String[kv.length-1];
		for (int i = 1; i < kv.length; i ++) {
			values[i-1] = kv[i];
		}
		return values;
	}
}
