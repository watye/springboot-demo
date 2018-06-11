package com.talelife.cache;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.talelife.util.SpringContextHolder;

public class MybatisRedisCache implements Cache {

	private static final Logger logger = LoggerFactory.getLogger(MybatisRedisCache.class);
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final String cacheInstanceId;
	private RedisTemplate<Object, Object> redisTemplate;
	/**
	 * redis过期时间
	 */
	private static final long EXPIRE_TIME_IN_MINUTES = 30;

	public MybatisRedisCache(String id) {
		if (Objects.isNull(id)) {
			throw new IllegalArgumentException("Cache instances require an ID");
		}
		this.cacheInstanceId = id;
	}

	@Override
	public String getId() {
		return cacheInstanceId;
	}

	/**
	 * 缓存结果集到redis
	 *
	 * @param key
	 * @param value
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void putObject(Object key, Object value) {
		ValueOperations<Object, Object> opsForValue = getRedisTemplate().opsForValue();
		opsForValue.set(key, value, EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
		logger.debug("Put query result to redis");
	}

	/**
	 * 从redis取缓存结果集
	 *
	 * @param key
	 * @return
	 */
	@Override
	public Object getObject(Object key) {
		RedisTemplate<Object, Object> redisTemplate = getRedisTemplate();
		ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
		logger.debug("Get cached query result from redis");
		return opsForValue.get(key);
	}

	/**
	 * 清除redis指定key的缓存
	 *
	 * @param key
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object removeObject(Object key) {
		RedisTemplate<Object, Object> redisTemplate = getRedisTemplate();
		redisTemplate.delete(key);
		logger.debug("Remove cached query result from redis");
		return null;
	}

	/**
	 * 清除缓存实例
	 */
	@Override
	public void clear() {
		RedisTemplate<Object, Object> redisTemplate = getRedisTemplate();
		redisTemplate.execute((RedisCallback) connection -> {
			connection.flushDb();
			return null;
		});
		logger.debug("Clear all the cached query result from redis");
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	private RedisTemplate getRedisTemplate() {
		if (redisTemplate == null) {
			redisTemplate = SpringContextHolder.getBean("redisTemplate");
		}
		return redisTemplate;
	}
}