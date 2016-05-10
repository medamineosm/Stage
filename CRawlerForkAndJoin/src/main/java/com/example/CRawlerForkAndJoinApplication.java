package com.example;

import com.example.Crawler.WebCrawler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CRawlerForkAndJoinApplication {

	public static void main(String[] args) {
		new WebCrawler("http://www.javaworld.com", 64).startCrawling();
		SpringApplication.run(CRawlerForkAndJoinApplication.class, args);
	}
}
