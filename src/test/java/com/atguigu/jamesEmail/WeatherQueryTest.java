package com.atguigu.jamesEmail;

import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class WeatherQueryTest {

	@Test
	public void queryWeather() {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "APPCODE 93b7e19861a24c519a7548b17dc16d75");

		HttpEntity requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String> entity = restTemplate.exchange(
				"http://jisutianqi.market.alicloudapi.com/weather/query?city={city}", HttpMethod.GET, requestEntity,
				String.class, "龙华区");

		System.out.println(entity.toString());
		System.out.println(entity.getBody());
	}

	// http://localhost:8080/scw-admin-web/user/edit.html
	@Test
	public void queryWeather1() {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "APPCODE 93b7e19861a24c519a7548b17dc16d75");

		HttpEntity requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String> entity = restTemplate.exchange(
				"http://localhost:8080/scw-admin-web/user/edit.html", HttpMethod.GET, requestEntity,
				String.class, "龙华区");

		System.out.println(entity.toString());
		System.out.println(entity.getBody());
	}
	
	
	@Test
	public void exchangeTest() {
		RestTemplate template = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		HttpEntity requestEntity = new HttpEntity<>(headers );
		template.exchange("http://localhost:8080/scw-admin-web/user/edit.html", HttpMethod.GET, requestEntity,
				String.class, "龙华区");
	}

}
