package com.atguigu.scw.swaggerconfig;

import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2 // 开启swagger2功能
public class SwaggerConfig {
	// 和<bean class="" id="">：
	// @Bean将方法的返回值的类型作为组件的类型，返回值作为这个组件对象，id默认是方法名
	@Bean("docket")
	public Docket petApi() {
		// new ApiInfo(xx,x,x,x,x,x,x,x,x);
		// 创建一个大对象；1）、工厂模式； 2）、建造者模式（使用建造者创建对象）
		ApiInfoBuilder infoBuilder = new ApiInfoBuilder();
		infoBuilder.description("这是尚筹网REST-API文档");
		infoBuilder.license("SCW-license");
		infoBuilder.licenseUrl("http://www.atguigu.com");
		infoBuilder.title("尚筹网API");
		infoBuilder.version("1.0.0");
		infoBuilder.contact(new Contact("leif", "http://www.baidu.com", "lfy@atguigu.com"));

		// 构建
		ApiInfo apiInfo = infoBuilder.build();

		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo);
	}

}
