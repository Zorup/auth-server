package com.example.authserver;

import com.example.authserver.common.config.RedisConfig;
import com.example.authserver.domain.Role;
import com.example.authserver.domain.User;
import com.example.authserver.dto.UserDto;
import com.example.authserver.repository.UserJpaRepo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = RedisConfig.class)
class AuthServerApplicationTests {

	@Autowired
	UserJpaRepo userJpaRepo;

	@Autowired
	RedisTemplate redisTemplate;

	@Value("${redis-key-prefix}")
	private String prefix;

//	@Test
//	void contextLoads() {
//	}

	@Test
	public void builderTest(){

		System.out.println("hihi");
		System.out.println(redisTemplate);
		System.out.println("hihihi");

		System.out.println("help me");

		UserDto userDto1 = UserDto.builder()
//				.userId(4L)
				.name("someone2")
				.role(Role.ROLE_USER)
				.email("canItWork@naver.com")
				.build();

//		User user = User.of(userDto1);
//		User user2 = User.of(userDto1);
//		userJpaRepo.save(user);
//		userJpaRepo.save(user2);

//		UserDto userDto2 = UserDto.of(user);
//
//		UserDto.ForRedis userRedisDto = UserDto.ForRedis.of(user);
//
////		System.out.println(0);
////		HashMapping hm = new HashMapping();
////		System.out.println(1);
////		hm.write(userRedisDto.getUserId().toString(), userRedisDto);
////		System.out.println(2);
////		UserDto.UserRedisDto result = hm.load(userRedisDto.getUserId().toString());
////		System.out.println(3);
////		System.out.println(result.getUserId() + " " + result.getRole());
//
//
//		final ValueOperations<String, UserDto.ForRedis> valueOperations = redisTemplate.opsForValue();
//		valueOperations.set("auth:" + userRedisDto.getUserId().toString(), userRedisDto);
//		UserDto.ForRedis result = valueOperations.get(prefix + ":" + userRedisDto.getUserId().toString());
//		System.out.println("check: " + prefix + ":" + userRedisDto.getUserId());
//		redisTemplate.expire(prefix + ":" + userRedisDto.getUserId(), 30, TimeUnit.SECONDS);
////		final HashOperations<String, Long, UserDto.UserRedisDto> ho = redisTemplate.opsForHash();
////		ho.put("auth", userRedisDto.getUserId(), userRedisDto);
////		UserDto.UserRedisDto result = ho.get("auth", userRedisDto.getUserId());
//		System.out.println(result == userRedisDto);
//		System.out.println(userRedisDto.getUserId());
//		System.out.println(userRedisDto.getRole());

		List<String> testList = new ArrayList<>();
		testList.add("test");
		testList.add("test2");

		final ValueOperations<String, String> ops = redisTemplate.opsForValue();

//		ops.set("test", "testvalue1");
//		ops.set("test2", "testvalue2");

		List<String> result = ops.multiGet(testList);
		System.out.println("result: " + result);

//		System.out.println("check return value");
//		System.out.println(redisTemplate.delete("auth:3"));

	}

}
//
//class HashMapping {
//	@Autowired
//	RedisTemplate redisTemplate;
//
//	HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
//
//	HashMapper<Object, String, Object> mapper = new Jackson2HashMapper(true);
//	public void write(String key, UserDto.UserRedisDto value){
//		System.out.println("a");
//		Map<String, Object> mappedHash = mapper.toHash(value);
//		System.out.println("b");
//		hashOperations.putAll(key, mappedHash);
//		System.out.println("c");
//	}
//
//	public UserDto.UserRedisDto load(String key){
//		System.out.println("aa");
//		Map<String, Object> loadedHash = hashOperations.entries(key);
//		System.out.println("bb");
//		return (UserDto.UserRedisDto)mapper.fromHash(loadedHash);
//	}
//
//}
