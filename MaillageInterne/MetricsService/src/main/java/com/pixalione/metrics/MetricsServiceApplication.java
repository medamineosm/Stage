package com.pixalione.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@SpringBootApplication
public class MetricsServiceApplication {

	public static void main(String[] args) throws ScriptException {


		SpringApplication.run(MetricsServiceApplication.class, args);
	}
}
