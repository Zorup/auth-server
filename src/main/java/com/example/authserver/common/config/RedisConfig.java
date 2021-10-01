package com.example.authserver.common.config;

import com.example.authserver.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){
        // 매개변수로 host, port 안줘도 application.yml에서 자동설정됨
        return new LettuceConnectionFactory(host, Integer.parseInt(port));
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(){
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // serializer로 default는 json형태로 사용하도록 세팅 (java serialization보다 용량이 적다)
        // 원래의 default는 java serialization 사용하기때문에, default로 사용시에 Serializable 구현 안한 클래스의 객체 넣을시 예외발생했음
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());

        // 이런식으로 명시적 설정 가능
        redisTemplate.setKeySerializer(new StringRedisSerializer());  // String.getBytes() 사용. 설정시 key값으로 String만 가능
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 트랜잭션 관리에 포함 (@Transactional) (디폴트가 false)
        redisTemplate.setEnableTransactionSupport(true);

        return redisTemplate;
    }

}
