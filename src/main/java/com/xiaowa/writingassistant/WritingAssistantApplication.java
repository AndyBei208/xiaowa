package com.xiaowa.writingassistant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xiaowa.writingassistant.mapper")
public class WritingAssistantApplication {
	public static void main(String[] args) {
		SpringApplication.run(WritingAssistantApplication.class, args);
	}
}
