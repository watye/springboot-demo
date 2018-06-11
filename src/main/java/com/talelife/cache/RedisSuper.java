package com.talelife.cache;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.talelife.myproject.model.User;
/**
 * redis缓存超级类
 * date: 2018-06-08 10:56:49
 * 
 * @author lwy
 * @since 1.0
 */
@Component
public class RedisSuper {
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	@Resource
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 存储值
	 * 
	 * @param key
	 * @param value
	 */
	public void setRedisStringValue(String key, String value) {
		stringRedisTemplate.opsForValue().set(key, value);
	}

	/**
	 * 获取值
	 * 
	 * @param key
	 * @return
	 */
	public String getRedisStringValue(String key) {
		return stringRedisTemplate.opsForValue().get(key) == null ? "" : stringRedisTemplate.opsForValue().get(key);
	}
}
