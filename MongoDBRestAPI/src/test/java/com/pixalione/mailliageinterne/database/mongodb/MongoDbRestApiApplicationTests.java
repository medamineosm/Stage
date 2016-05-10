package com.pixalione.mailliageinterne.database.mongodb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MongoDbRestApiApplication.class)
@WebAppConfiguration
public class MongoDbRestApiApplicationTests {

	@Test
	public void contextLoads() {
	}

}
