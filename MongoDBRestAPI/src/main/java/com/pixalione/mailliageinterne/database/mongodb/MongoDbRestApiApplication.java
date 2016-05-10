package com.pixalione.mailliageinterne.database.mongodb;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class MongoDbRestApiApplication extends AbstractMongoConfiguration{
	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient();
	}

	@Override
	protected String getDatabaseName() {
		return "testCrawling";
	}
	public static void main(String[] args) {
		SpringApplication.run(MongoDbRestApiApplication.class, args);
	}
}
