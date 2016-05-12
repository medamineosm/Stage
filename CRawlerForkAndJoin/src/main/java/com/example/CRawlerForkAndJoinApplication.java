package com.example;

import com.example.Crawler.WebCrawler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
public class CRawlerForkAndJoinApplication {

	public static void main(String[] args) {


		SpringApplication.run(CRawlerForkAndJoinApplication.class, args);
	}
}
