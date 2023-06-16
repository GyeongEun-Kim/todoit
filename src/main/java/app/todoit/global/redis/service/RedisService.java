package app.todoit.global.redis.service;

import app.todoit.domain.auth.entity.User;
import app.todoit.global.exception.ApiException;
import app.todoit.global.exception.ErrorCode;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

	private final StringRedisTemplate redisTemplate;

	public final static long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24;
	public final static long REFRESH_TOKEN_VALIDATION_TIME = 1000L * 60 * 60 * 24 * 14;


	public void saveCode(String email, String code){
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		valueOperations.set("email:" + email,  code, Duration.ofMillis(1000L * 60 * 5)); // 5분
	}

	public String getCode(String email){
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		return valueOperations.get("email:"+email);
	}


	public void saveToken(User user, String atk, String rtk){
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		valueOperations.set("atk:" + user.getId().toString(),  atk, Duration.ofMillis(ACCESS_TOKEN_EXPIRE_TIME));
		valueOperations.set("rtk:" + user.getId().toString(),  rtk, Duration.ofMillis(REFRESH_TOKEN_VALIDATION_TIME));

		log.info("redis atk : {}", valueOperations.get("atk:"+user.getId().toString()));
		log.info("redis rtk : {}", valueOperations.get("rtk:"+user.getId().toString()));

	}

	public String getRefreshToken(User user){
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		return valueOperations.get("rtk:"+user.getId());
	}

	public void isExisted(User user){
		String s = redisTemplate.opsForValue().get("rtk:" + user.getId());
		if(s == null){
			throw new ApiException(ErrorCode.LOGOUT_USER);
		}
	}

	public void saveAccessToken(User user, String atk){
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		valueOperations.set("atk:" + user.getId().toString(),  atk, Duration.ofMillis(ACCESS_TOKEN_EXPIRE_TIME));

		log.info("redis atk : {}", valueOperations.get("atk:"+user.getId().toString()));
	}

	public void deleteToken(User user){
		redisTemplate.opsForValue().getAndDelete("atk:" + user.getId().toString());
		redisTemplate.opsForValue().getAndDelete("rtk:" + user.getId().toString());
	}

}
