package com.atguigu.scw.api.test;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.atguigu.scw.api.bean.ApiReturn;
import com.atguigu.scw.common.bean.TUser;
import com.atguigu.scw.common.service.UserService;

import io.swagger.annotations.ApiOperation;

@RestController
public class HelloController {

	@Autowired
	UserService userService;

	@ResponseBody
	@GetMapping("/getUser")
	public TUser getUser() {

		return userService.getUserById(1);
	}
	
	@ApiOperation("多服务器访问")
	@GetMapping("/outer")
	public ApiReturn outerTest() {
		
		RestTemplate template = new RestTemplate();
		
		ResponseEntity<String> entity = template.getForEntity("http://localhost:8080/scw-admin-web/user/edit.html", String.class);
		
		System.out.println(entity);
		return ApiReturn.success(entity.getBody());
	}
}
