package com.atguigu.scw.api.bean;

public class ApiReturn {
	
	private Integer status; //0成功，1失败
	private String msg;//提示信息
	private Object result;//返回结果；
	
	
	
	//快速返回成功
	public static ApiReturn success(Object result){
		ApiReturn apiReturn = new ApiReturn();
		apiReturn.setStatus(0);
		apiReturn.setMsg("ok");
		apiReturn.setResult(result);
		return apiReturn;
	}
	
	//快速返回失败
	public static ApiReturn fail(Object result){
		ApiReturn apiReturn = new ApiReturn();
		apiReturn.setStatus(1);
		apiReturn.setMsg("fail");
		apiReturn.setResult(result);
		return apiReturn;
	}
	
	//完全定制的
	public static ApiReturn custom(Integer status,String msg,Object result){
		ApiReturn apiReturn = new ApiReturn();
		apiReturn.setStatus(status);
		apiReturn.setMsg(msg);
		apiReturn.setResult(result);
		return apiReturn;
	}
	
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
}
