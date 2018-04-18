package com.atguigu.jamesEmail;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Test;


public class EmailTest {
	
	@Test
	public void test() throws EmailException {
	
		SimpleEmail email = new SimpleEmail();
		
		
		email.setFrom("472383881@qq.com");
		email.addTo("1287295259@qq.com");
		//发送短信生成第三方授权码:iyvgakcbsxlhbjab
		email.setAuthentication("472383881@qq.com", "iyvgakcbsxlhbjab");
		//ssl加密
		email.setSSLOnConnect(true);
		
		email.setHostName("smtp.qq.com");
		email.setMsg("晚上吃饭!");
		email.setSubject("晚会安排");
		
		
		email.send();
	}
	
	@Test
	public void testJamesEmail() throws EmailException {
		SimpleEmail email = new SimpleEmail();
		
		
		email.setFrom("admin@atguigu.com");
		email.addTo("1287295259@qq.com");
		//发送短信生成第三方授权码:iyvgakcbsxlhbjab
		email.setAuthentication("admin@atguigu.com", "123456");
		//ssl加密
		//email.setSSLOnConnect(true);
		//smtp.qq.com
		email.setHostName("localhost");
		email.setMsg("晚上吃饭!");
		email.setSubject("晚会安排");
		
		
		email.send();
	}
}
