package com.github.timi.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * ClassName：CodeGenerator <br/>
 * Description：代码文件生成器 <br/>
 * Date： 2016年12月2日 上午10:29:46 <br/>
 * 
 * @author yanghm <br/>
 * @update [修改日期:yyyy年MM月dd日 修改内容:? 修改人：?]
 */
public class CodeGenerator {

	private Configuration cfg;

	private String ftlPath = "/ftl";

	private String charset = "UTF-8";

	private static CodeGenerator gen;

	private CodeGenerator(String ftlPath) {
		cfg = new Configuration();
		if (StringUtils.isNotBlank(ftlPath)) {
			this.ftlPath = ftlPath;
		}
		cfg.setDefaultEncoding(charset);// 设置默认读取模板编码为utf-8
		cfg.setNumberFormat("#");// 设置数字格式1000=》"1000"，默认为1,000
		cfg.setClassForTemplateLoading(this.getClass(), this.ftlPath);
	}

	public static CodeGenerator init(String ftlPath) {
		if (gen == null) {
			gen = new CodeGenerator(ftlPath);
		}
		return gen;
	}

	public File getTemplateFiles() {
		File file = new File(this.ftlPath);
		return file;
	}

	/**
	 * getTemplate:获取模板<br/>
	 * date：2016年12月2日上午10:22:56<br/>
	 * 
	 * @author yanghm<br/>
	 *         <br/>
	 * @param name 模板名
	 * @return
	 */
	public Template getTemplate(String name) {
		try {
			Template template = cfg.getTemplate(name, this.charset);
			return template;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * printFile:控制台打印文件内容<br/>
	 * date：2016年12月2日上午10:04:21<br/>
	 * 
	 * @author yanghm<br/>
	 *         <br/>
	 * @param templateName
	 * @param data
	 */
	public void printFile(String templateName, Object data) {
		try {
			Template template = this.getTemplate(templateName);
			template.process(data, new PrintWriter(System.out));
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * generateFile:生成代码文件,默认编码utf-8<br/>
	 * date：2016年12月2日上午10:03:28<br/>
	 * 
	 * @author yanghm<br/>
	 *         <br/>
	 * @param templateName 模板名
	 * @param data 模板数据
	 * @param outFilePath 文件路径
	 */
	public void generateFile(String templateName, Object data, String outFilePath) {
		this.generateFile(templateName, data, outFilePath, null);
	}

	/**
	 * 
	 * generateFile:生成代码文件<br/>
	 * date：2016年12月2日上午10:03:28<br/>
	 * 
	 * @author yanghm<br/>
	 *         <br/>
	 * @param templateName 模板名
	 * @param data 模板数据
	 * @param outFilePath 文件路径
	 * @@param charset 文件编码
	 */
	public void generateFile(String templateName, Object data, String outFilePath, String charset) {
		OutputStreamWriter out = null;
		try {
			if (charset != null) {
				this.charset = charset;
			}
			File file = new File(outFilePath);
			if (!file.getParentFile().exists()) {// 上级文件夹不存在,创建上级文件夹
				file.getParentFile().mkdirs();
			}
			out = new OutputStreamWriter(new FileOutputStream(file), this.charset);
			Template temp = this.getTemplate(templateName);
			temp.process(data, out);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
