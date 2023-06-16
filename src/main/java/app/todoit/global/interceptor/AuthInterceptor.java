package app.todoit.global.interceptor;

import app.todoit.domain.auth.entity.User;
import app.todoit.domain.auth.repository.UserRepository;
import app.todoit.domain.auth.token.JwtUtil;
import app.todoit.global.annotation.WithOutAuth;
import app.todoit.global.exception.ApiException;
import app.todoit.global.exception.ErrorCode;
import app.todoit.global.exception.NotFoundException;
import app.todoit.global.redis.service.RedisService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

	public final static String AUTHORIZATION = "Authorization";
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final RedisService redisService;

	public AuthInterceptor(JwtUtil jwtUtil, UserRepository userRepository, RedisService redisService) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
		this.redisService = redisService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		log.info("interceptor start: {}", request.getRequestURI());

		String accessToken = request.getHeader(AUTHORIZATION);

		WithOutAuth withoutAuth = ((HandlerMethod) handler).getMethodAnnotation(WithOutAuth.class);

		if(withoutAuth == null){
			if(accessToken == null){
				log.error("{}", "요청헤더에 토큰이 존재하지 않습니다.");
				throw new ApiException(ErrorCode.UNAUTHORIZED);
			}
			jwtUtil.validateToken(accessToken);

			String userId = jwtUtil.getSubject(accessToken);
			User user = userRepository.findById(Long.parseLong(userId))
				.orElseThrow(()->new NotFoundException(ErrorCode.NOT_FOUND_USER, userId));
			log.info("user : {}", user.getEmail());

			// 로그아웃 체크
			redisService.isExisted(user);

			UserThreadLocal.set(user);

		}
		return true;

	}
}

