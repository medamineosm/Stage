package com.pixalione.mailliageinterne;

import com.pixalione.mailliageinterne.text_extraction.Extraction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
public class SemanticProximityApplication {
	public static String ROOT = "upload-dir";

	@Bean
	CommandLineRunner init() {
		return (String[] args) -> {
			new File(ROOT).mkdir();
		};
	}
	public static void main(String[] args) throws IOException, URISyntaxException {

		String file = "Extracted_Text";
		String path = ROOT;
		String url = "http://76-renovation.cabanova.com";

		SpringApplication.run(SemanticProximityApplication.class, args);

	}
}
