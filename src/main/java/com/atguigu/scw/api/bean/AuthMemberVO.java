package com.atguigu.scw.api.bean;

import io.swagger.annotations.ApiModelProperty;

public class AuthMemberVO {

	@ApiModelProperty("用户的账户类型：  【1-商业公司】【2-个体工商户】【3-个人经营】【4-政府及非营利组织】")
	private String accttype;
	
	@ApiModelProperty("真实姓名")
	private String realname;
	@ApiModelProperty("身份证号")
	private String cardnum;
	
	public String getAccttype() {
		return accttype;
	}
	public void setAccttype(String accttype) {
		this.accttype = accttype;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getCardnum() {
		return cardnum;
	}
	public void setCardnum(String cardnum) {
		this.cardnum = cardnum;
	}
}
