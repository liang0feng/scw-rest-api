package com.atguigu.scw.api.control;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atguigu.scw.api.bean.ApiReturn;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags="系统通过功能")
@ResponseBody
@RequestMapping("/system")
@RestController
public class SystemController{

	@ApiOperation("文件上传功能")
	@PostMapping("/uploadfile")
	public ApiReturn uploadFile(
			@ApiParam("要上传的文件：")
			@RequestParam(value="file",required=true,defaultValue="")
			MultipartFile[] file,
			
			HttpServletRequest request,
			
			@ApiParam("服务器工程路径下的目标文件夹(不写默认为files)")
			@RequestParam(value="directory",required=false,defaultValue="files" )
			String... directory
			) throws IllegalStateException, IOException {
		
		//创建一个list用户存放文件的路径
		ArrayList<String> pathList = new ArrayList<String>();
		ServletContext servletContext = request.getSession().getServletContext();
		System.out.println("directory:" + directory[0]);
		if (file == null || file.length == 0) {
			return ApiReturn.fail("文件不能为空");
		}
		
		for (MultipartFile multipartFile : file) {
			if (multipartFile.isEmpty()) {
				return ApiReturn.fail("文件不能为空");
			}
			
			
			//上传 到服务器的真实路径 为：
			String realPath = servletContext.getRealPath("/" + directory[0]);
			
			//生成唯一文件名
			String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 9);
			String fileName = uuid + "_" +  multipartFile.getOriginalFilename();
			System.out.println("上传 到服务器的真实路径 为："+realPath + "\\" +fileName);
			
			multipartFile.transferTo(new File(realPath + "/" + fileName));
			pathList.add(directory[0] + "/" + fileName);
		}
		
		return ApiReturn.custom(0, servletContext.getContextPath(), pathList);
	}
}