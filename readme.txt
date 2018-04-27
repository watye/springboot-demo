版本升级配置

v1.7 springboot整合redis

1）添加依赖
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

2）添加配置类 RedisConfig.java

3）yml文件添加redis参数配置
spring:
  redis:
    host: 172.31.118.23
    password: 123456
    port: 6379
    
4）使用示例CacheController.java

v1.8 mybatis整合redis缓存

1）添加依赖
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

2)添加MybatisRedisCache.java,MybatisRedisCacheTransfer.java

3)mapper文件添加缓存配置
<cache type="com.talelife.cache.MybatisRedisCache">
	<!-- 收回策略,LRU最近最少使用的,FIFO先进先出,SOFT软引用,WEAK弱引用  -->
	<property name="eviction" value="LRU" />
	<!-- 刷新间隔毫秒 -->
    <property name="flushInterval" value="600000" />
    <!-- 引用数目 -->
    <property name="size" value="1024" />
    <!-- 可读写的缓存会返回缓存对象的拷贝（通过序列化）。这会慢一些，但是安全，因此默认是false -->
    <property name="readOnly" value="false" />
</cache>