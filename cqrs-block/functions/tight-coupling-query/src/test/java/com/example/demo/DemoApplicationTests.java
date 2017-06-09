package com.example.demo;

import demo.functions.TightCouplingQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TightCouplingQuery.class)
public class DemoApplicationTests {

	@Test
	public void contextLoads() {
	}

}
