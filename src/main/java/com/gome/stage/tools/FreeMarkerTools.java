package com.gome.stage.tools;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.gome.frontSe.base.FreeMarkerFillBase;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerTools extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static Configuration cfg;
	@Override
	public void init(ServletConfig config) throws ServletException {
		cfg = new Configuration();
		try {
			cfg.setDirectoryForTemplateLoading(	new File(config.getServletContext().getRealPath("WEB-INF/template")));
			cfg.setObjectWrapper(new DefaultObjectWrapper());
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.init(config);
	}
	/**
	 * 获得配置工厂类对象
	 * */
	public static Configuration getConfiguration(){
		return cfg;
	}
	/**
	 * 获得相应的模板对象
	 * */
	public static Template getTemplate(String path) throws IOException{
		return getConfiguration().getTemplate(path,"UTF-8");
	}
	
	/**
	 * 得到相应模板的填充数据后的HTML
	 * @author zhangjianwei
	 * @param tepPath 模板的相对目录 
	 * @param data 需要填充的数据对象
	 * @throws IOException 
	 * @throws TemplateException 
	 * */
	public static String getTemplateHtml(String tepPath,FreeMarkerFillBase data) throws IOException, TemplateException{
		Template tem = getTemplate(tepPath);
		StringWriter html =  new StringWriter();
		tem.process(data, html);
		return html.toString();
	}
	
	/**
	 * 将相应模板填充数据后的结果写入相应的流对象
	 * @author zhangjianwei
	 * @param tepPath 模板的相对目录 
	 * @param data 需要填充的数据对象
	 * @param wt 写入流对象
	 * @throws IOException 
	 * @throws TemplateException 
	 * */
	public static void getTemplateWrite(String tepPath,Object data,Writer wt) throws IOException, TemplateException{
		Template tem = getTemplate(tepPath);
		tem.process(data, wt);
	}
	
	
}