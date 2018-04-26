package com.talelife.myproject.service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
/**
 * 用户业务测试类
 * date: 2017-08-17 17:54:46
 * 
 * @author Liuweiyao
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest{
	
	@Autowired
	private UserService userService;
	
	@Test
	public void testFindAll(){
		System.out.println(userService.findAll());
	}
	
}