package com.mstx.framework.user.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

//@Configuration
public class RedisTemplateConfiguration {

    @Autowired
    Environment environment;


    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(createJedisConnectionFactory());
    }

    private JedisConnectionFactory createJedisConnectionFactory() {
        String hostName = String.valueOf(environment.getProperty("redis.hostName", "localhost"));
        int port = Integer.valueOf(environment.getProperty("redis.port", "6379")).intValue();
        int database = Integer.valueOf(environment.getProperty("redis.database", "1")).intValue();
        RedisPassword password = environment.containsProperty("redis.password") ? RedisPassword.of(environment.getProperty("redis.password").toString()) : RedisPassword.none();
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setHostName(hostName);
        redisStandaloneConfiguration.setPassword(password);
        redisStandaloneConfiguration.setPort(port);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    private JedisPoolConfig createJedisPoolConfig() {
        Integer maxTotal = Integer.parseInt(environment.getProperty("redis.maxTotal", "10"));
        Integer maxIdle = Integer.parseInt(environment.getProperty("redis.maxIdle", "5"));
        Integer maxWaitMillis = Integer.parseInt(environment.getProperty("redis.maxWait", "2000"));
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);//最大连接数
        jedisPoolConfig.setMaxIdle(maxIdle);//最大空闲连接数
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);//获取连接的最大等待时间
        return jedisPoolConfig;
    }

}
