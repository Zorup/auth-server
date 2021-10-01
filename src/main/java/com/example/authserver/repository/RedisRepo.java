package com.example.authserver.repository;

import com.example.authserver.common.exception.RedisException;
import com.example.authserver.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepo {

    private final RedisTemplate redisTemplate;

    private long refreshTokenValidTime = UserDto.ForRedis.refreshTokenValidTime + 5;    // second 단위, 토큰표기시간보다 5초 여유

    @Value("${redis-key-prefix}")
    private String prefix;


    /**
     *  refreshToken 정보 저장에 Redis 사용
     *
     *  redis collection type: string
     *  key: prefix + UserDto.ForRedis.userId   ex) if userId == 3 && prefix == "auth:" -> key= "auth:3"
     *  value: UserDto.ForRedis
     *
     */

    public void insertRefreshToken(UserDto.ForRedis value) throws RedisException {
        ValueOperations<String, UserDto.ForRedis> ops = redisTemplate.opsForValue();
        String key = prefix + value.getUserId().toString();
        try {
            ops.set(key, value);
            redisTemplate.expire(key, refreshTokenValidTime, TimeUnit.SECONDS); // 유효기간 만료시 자동삭제됨
        } catch(RuntimeException e) {
            log.info("redisException:"+e.toString());
            throw new RedisException();
        }
    }

    public Optional<UserDto.ForRedis> findRefreshToken(Long userId){
        ValueOperations<String, UserDto.ForRedis> ops = redisTemplate.opsForValue();
        String key = prefix + userId.toString();
        return Optional.ofNullable(ops.get(key));
    }

    public boolean deleteRefreshToken(Long userId) throws RedisException {
        String key = prefix + userId.toString();
        try {
            return redisTemplate.delete(key);
        } catch(RuntimeException e){
            log.info("redisException:"+e.toString());
            throw new RedisException();
        }
    }

}
