package com.talelife.cache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import junit.framework.TestCase;
/**
 * 用户业务测试类
 * date: 2018-06-08
 * 
 * @author Liuweiyao
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisSuperTest{
	
	@Autowired  
    private RedisSuper redis;  
      
    @Test  
    public void testSetAndGet(){  
    	String name = "lwy";
    	redis.setRedisStringValue("name",name);
        String cacheName = redis.getRedisStringValue("name");
        TestCase.assertEquals(name, cacheName);
    } 
}