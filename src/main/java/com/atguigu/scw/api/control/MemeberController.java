package com.atguigu.scw.api.control;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.scw.api.bean.ApiReturn;
import com.atguigu.scw.common.bean.TMember;
import com.atguigu.scw.common.exception.InsertException;
import com.atguigu.scw.common.exception.LoginAcctExitException;
import com.atguigu.scw.common.exception.LoginEmailExitException;
import com.atguigu.scw.common.exception.TokenInvaildException;
import com.atguigu.scw.common.service.MemberService;
import com.atguigu.scw.common.service.SystemService;
import com.atguigu.scw.common.util.AppCacheUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "会员操作")
@RequestMapping("/member")
@RestController
public class MemeberController {

	@Autowired
	SystemService systemService;
	@Autowired
	MemberService memberSerive;

	@ApiOperation("发送密码重置邮件")
	@PostMapping("/sendResetEmail")
	public ApiReturn sendResetPasswordEmail(@RequestParam("email") @ApiParam("注册所用的邮箱：") String email) {
		try {
			memberSerive.sendResetEmail(email);
		} catch (EmailException e) {
			return ApiReturn.fail("邮件发送失败！");
		}

		return ApiReturn.success("邮件发送成功！");
	}

	
	@ApiOperation("提交新密码")
	@PostMapping("/resetPassword")
	public ApiReturn resetPassword(@RequestParam("token") @ApiParam("令牌") String token,
			@RequestParam("newpassword") @ApiParam("新密码") String newPassword) {
		try {
			memberSerive.updateMember(token, newPassword);
		} catch (TokenInvaildException e) {
			return ApiReturn.fail("令牌非法，密码修改失败！");
		}
		return ApiReturn.success("密码修改成功！");
	}

	@ApiOperation("用户注销")
	@GetMapping("/logout")
	public ApiReturn logout(
			@ApiParam("用户授权码")
			@RequestParam("authcode")
			String authcode
			) {
		
		TMember member = (TMember) AppCacheUtils.get(authcode);
		if (member == null) {
			return ApiReturn.custom(0, "要注销的用户不存在！", null);
		}
		
		AppCacheUtils.remove(authcode);
		
		return ApiReturn.success(null);
	}
	
	@ApiOperation("用户登录")
	@PostMapping("/login")
	public ApiReturn login(@RequestParam("loginacct") @ApiParam("账户") String loginacct,
			@RequestParam("password") @ApiParam("密码") String password) {

		System.out.println("loginacct:"+loginacct);
		// 登录的用户放在session中；
		// session共享； 使用缓存库 redis（内存库）、
		// 用一个缓存中心来保存共享数据；
		TMember member = memberSerive.login(loginacct, password);
		if (member == null) {
			// 登陆失败
			return ApiReturn.fail("用户名密码错误");
		} else {
			// 将登陆的用户放在缓存中；可以方便下次使用；
			// ${loginacct}：无session的情况下怎么标识当前用户的身份；
			// 每一个用户登陆进系统以后，都会有一个授权码；授权码可以返回给客户端；
			// 1、产生一个和当前登陆用户唯一对应的授权码 dsjakldjsakljdaslkjdlak=1
			String authCode = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
			// 2、为什么要有授权码？举个例子；
			// 以结账操作为例；无状态的情况下一些关键数据千万不能让前端传递进来；
			AppCacheUtils.put(authCode, member);

			// 3、把授权码也返回出去;用户以后提交的所有请求都必须携带这个授权码来标识用户身份
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("authcode", authCode);
			result.put("member", member);
			return ApiReturn.success(result);
		}
	}

	@ApiOperation("授权码登录接口")
	@PostMapping("/authcodeLogin")
	public ApiReturn authcodeLogin(
			@ApiParam("用户授权码") 
			@RequestParam("authcode") 
			String authcode
			) {
		TMember loginUser = (TMember) AppCacheUtils.get(authcode);
		if (loginUser == null || "".equals(loginUser) ) {
			//授权码错误
			return ApiReturn.fail("授权码错误！");
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("authcode", authcode);
		result.put("member", loginUser);
		return ApiReturn.success(result);
	}

	
	/**
	 * 用户注册api
	 * @param loginacct 用户名
	 * @param userpswd 密码
	 * @param email	邮箱
	 * @return
	 */
	@ApiOperation("用户注册")
	@PostMapping("/regist")
	public ApiReturn regist(
			@ApiParam("账户") 
			@RequestParam("loginacct") 
			String loginacct,

			@ApiParam("密码") 
			@RequestParam("userpswd") 
			String userpswd,

			@ApiParam("邮箱") 
			@RequestParam("email") 
			String email) {

		TMember member = new TMember();
		member.setEmail(email);
		member.setLoginacct(loginacct);
		member.setUserpswd(userpswd);
		
		try {
			memberSerive.regist(member);
			// return 1   return 2   return 3  4 5  6
		} catch (LoginAcctExitException e) {
			return ApiReturn.fail("用户名已经被注册");
		} catch (LoginEmailExitException e) {
			return ApiReturn.fail("邮箱已经被注册");
		} catch (InsertException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ApiReturn.success(null);
	}
}
