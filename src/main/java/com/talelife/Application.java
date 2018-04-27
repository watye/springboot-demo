package com.talelife;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.talelife.util.SpringContextHolder;

@SpringBootApplication
@MapperScan("com.talelife.myproject.mapper")
public class Application {

	public static void main(String[] args) throws Exception {
       SpringApplication.run(Application.class, args);
  }


}
