package com.gitee.uidhxd.drools7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
//@MapperScan(basePackages = "com.gitee.uidhxd.drools7.mapper")
public class Drools7Application {
	//private Logger logger = LoggerFactory.getLogger(Drools7Application.class);
	public static void main(String[] args) {
		SpringApplication.run(Drools7Application.class, args);
	}

}
