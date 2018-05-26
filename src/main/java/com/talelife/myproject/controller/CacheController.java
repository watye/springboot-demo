package com.talelife.myproject.controller;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talelife.util.Result;

/**
 * springboot整合redis示例
 * @author lwy
 * @date 2018-04-27
 */
@RestController
@RequestMapping("/cache")
public class CacheController extends BaseController{
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	@RequestMapping("/set_cache")
    public Result setCache() {
		redisTemplate.opsForValue().set("aaa", "lwy");
		return Result.success();
    }
	
	@RequestMapping("/get_cache")
    public Result getCache() {
		return Result.success(redisTemplate.opsForValue().get("aaa"));
    }
	
	
	
	
}
