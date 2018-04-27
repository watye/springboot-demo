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