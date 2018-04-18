package com.atguigu.scw.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContextInitializer;

/**
 * 监听项目的启动停止
 * @author lfy
 *
 */
public class MyAppListener implements ServletContextListener {

		
	//项目销毁
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//项目初始化
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		//一个项目对应自己一个servletContext
		ServletContext servletContext = arg0.getServletContext();
		//  /scw-admin-web：前面带了/，后面不带/
		servletContext.setAttribute("ctp", servletContext.getContextPath());
		
	}

}
