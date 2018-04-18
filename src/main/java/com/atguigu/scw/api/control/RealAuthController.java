package com.atguigu.scw.api.control;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.scw.api.bean.ApiReturn;
import com.atguigu.scw.api.bean.AuthMemberVO;
import com.atguigu.scw.common.bean.TCert;
import com.atguigu.scw.common.bean.TMember;
import com.atguigu.scw.common.bean.TMemberCert;
import com.atguigu.scw.common.service.MemberCertService;
import com.atguigu.scw.common.service.MemberService;
import com.atguigu.scw.common.service.SystemService;
import com.atguigu.scw.common.util.AppCacheUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "实名认证")
@ResponseBody
@RequestMapping("/auth")
@RestController
public class RealAuthController {

	@Autowired
	MemberService memberService;
	@Autowired
	SystemService systemService;
	@Autowired
	MemberCertService memberCertService;

	@ApiOperation("①保存用户基本信息")
	@PostMapping("/baseinfo")
	public ApiReturn saveBaseInfo(AuthMemberVO vo, @ApiParam("用户授权码") @RequestParam("authcode") String token) {

		TMember member = new TMember();
		// 1将传入的vo复制到member中
		BeanUtils.copyProperties(vo, member);

		// 2根据authcode，在缓存中查出用户id
		TMember login = (TMember) AppCacheUtils.get(token);

		// 3.判断用户是否存在
		if (login != null) {
			Integer id = login.getId();
			member.setId(id);

			memberService.saveBaseINfo(member);

			return ApiReturn.success(vo);
		}
		return ApiReturn.fail("用户授权码不存在！");
	}

	

	//操作TmemberCert表保存上传资质文件
	/**
	 * 
	 * @param certid 资质类型id
	 * @param certpath 资质文件保存在服务器上的路径
	 * @param authcode 用户授权码
	 * @return 返回插入的TmemberCert表中的数据
	 */
	@ApiOperation("②保存用户上传的资质")
	@PostMapping("/savecert")
	public ApiReturn saveCert(
			@ApiParam("资质id")
			@RequestParam("certid")
			String[] certid,
			
			@ApiParam("资质保存路径")
			@RequestParam("certpath")
			String[] certpath,
			
			@ApiParam("用户授权码")
			@RequestParam("authcode")
			String authcode	) {
		
		//校验授权码
		TMember login = (TMember) AppCacheUtils.get(authcode);
		if (login == null) {
			return ApiReturn.fail("用户授权码错误!");
		}
		
		//校验上传文件
		if (certid == null || certid.length == 0) {
			return ApiReturn.fail("上传文件id不存在");
		}
		if(certpath == null || certpath.length == 0) {
			return ApiReturn.fail("上传文件路径不存在");
		}
		
		//遍历
		List<TMemberCert> memberCertList = new ArrayList<>();
		for (int i = 0; i < certpath.length; i++) {
			TMemberCert memberCert = new TMemberCert();
			memberCert.setMemberid(login.getId());
			memberCert.setCertid(Integer.parseInt(certid[i]));
			memberCert.setIconpath(certpath[i]);
			memberCertList.add(memberCert);
		}
		//上传资质文件 
		memberCertService.saveCerts(memberCertList);
		
		return ApiReturn.success(memberCertList);
	}

	// 发送验证码到邮箱，并将验证码保存到cache中方便，之后 的验证使用

	/**
	 * 实名认证流程成功完成之后 ：自动调用该方法发送邮件验证码
	 * @param authcode 用户授权码
	 * @return
	 */
	@ApiOperation("③发送实名认证的验证码到用户邮箱")
	@GetMapping("/emailcode")
	public ApiReturn sendEmailCode(
			@ApiParam("用户授权码") 
			@RequestParam("authcode") 
			String authcode) {
		
		//1.校验用户授权码
		TMember login = (TMember) AppCacheUtils.get(authcode);
		if (login == null) {
			//未登录
			return ApiReturn.fail("用户授权码错误！");
		}
		
		//2.登录成功：取出用户名，用户邮箱
		String realname = login.getRealname(); 
		String email = login.getEmail(); //接收到邮箱地址
		//邮件内容:验证码
		String valiCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
		//将验证码保存到缓存中
		AppCacheUtils.put(valiCode, email);
		String content = "验证码为："+ valiCode; 
		String subject = "《尚筹网》实名认证验证码"; //邮件主题 
		//授权码校验O,发送邮件
		try {
			systemService.sendCustomEmail(subject, content, email);
		} catch (EmailException e) {
			e.printStackTrace();
			return ApiReturn.fail("邮件发送失败，原因："+e.getCause().toString());
		}
		
		
		return ApiReturn.success("邮件发送成功！");
	}

	
	/**
	 * 
	 * 接收验证码，验证并启用实名认证流程 需要的参数 
	 * 			1.邮箱收到的验证码；2.用户授权码
	 * 
	 * 执行过程： 1.验证邮箱与用户授权码 2.启用实名认证流程
	 */
	@ApiOperation("④校验提交的实名认证验证码，并启动实名认证流程")
	@PostMapping("/validate")
	public ApiReturn validate(
			@ApiParam("邮箱实名验证码") 
			@RequestParam("valiCode") 
			String valiCode,

			@ApiParam("用户授权码") 
			@RequestParam("authcode") 
			String authcode) {
		
		//1.校验用户授权码
		TMember login = (TMember) AppCacheUtils.get(authcode);
		if (login == null) {
			return ApiReturn.fail("用户授权码错误!");
		}
		
		//2.校验邮箱验证码
		String email = (String) AppCacheUtils.get(valiCode);
		if (email == null) {
			return ApiReturn.fail("实名认证验证码错误!");
		}
		
		//移出缓存中的验证码:使失效
		AppCacheUtils.remove(valiCode);
		
		//3.启用实名认证流程
		//需要新建一个表:关联会员与其所动的流程等相关信息:
		//字段:id(自增)/memberid/procsinstid/creattime/protype
		memberService.startAuthProcess(login.getId());
		
		return null;
	}
	
	
	
	/**
		 * 根据用户账户类型查询需要上传的资质
		 * 1、根据授权码查出用户的账户类型 2、根据账户类型查出需要上传的资质
		 * 
		 * @param authcode
		 *            用户登录的授权码
		 * @return
		 */
		@ApiOperation("根据用户的账户类型查询出需要上传的资质 ")
		@GetMapping("/certs")
		public ApiReturn getCerts(
				@ApiParam("用户授权码") 
				@RequestParam("authcode") 
				String authcode) {
			
			//1.校验用户授权码
			TMember login = (TMember) AppCacheUtils.get(authcode);
			if (login == null) {
				//未登录
				return ApiReturn.fail("用户授权码错误！");
			}

			// 登录成功：账户类型
			String accttype = login.getAccttype();
			
			// 2.上传资质
			List<TCert> certs = memberService.getCertByAccttype(accttype);
			return ApiReturn.success(certs);
		}

}
