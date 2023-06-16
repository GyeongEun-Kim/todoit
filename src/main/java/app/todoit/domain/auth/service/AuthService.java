package app.todoit.domain.auth.service;

import app.todoit.domain.auth.dto.JoinRequestDto;
import app.todoit.domain.auth.dto.KakaoUserDto;
import app.todoit.domain.auth.dto.TokenDto;
import app.todoit.domain.auth.entity.User;
import app.todoit.domain.auth.repository.UserRepository;
import app.todoit.domain.auth.token.JwtUtil;
import app.todoit.global.exception.ApiException;
import app.todoit.global.exception.ErrorCode;
import app.todoit.global.exception.NotFoundException;
import app.todoit.global.redis.service.RedisService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final RedisService  redisService;

	public boolean isJoined(KakaoUserDto kakaoUserDto){
		Optional<User> kakaoUser = userRepository.findByEmail(kakaoUserDto.getEmail());
		if(kakaoUser.isEmpty())
			return false;
		else
			return true;
	}

	public TokenDto joinUp(JoinRequestDto joinRequestDto){

		Optional<User> kakaoUser = userRepository.findByEmail(joinRequestDto.getEmail());

		if(kakaoUser.isPresent())
			throw new NotFoundException(ErrorCode.ALREADY_EXIST_USER, joinRequestDto.getEmail());

		User user = userRepository.save(joinRequestDto.toEntity());

		String atk = jwtUtil.generateAccessToken(user);
		String rtk = jwtUtil.generateRefreshToken(user);

		redisService.saveToken(user, atk, rtk);

		return TokenDto.builder().email(user.getEmail())
			.accessToken(atk).refreshToken(rtk).build();
	}

	public TokenDto login(KakaoUserDto kakaoUserDto){
		User reqUser = kakaoUserDto.toEntity();
		User user = userRepository.findByEmail(reqUser.getEmail())
			.orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER, reqUser.getEmail()));

		String atk = jwtUtil.generateAccessToken(user);
		String rtk = jwtUtil.generateRefreshToken(user);

		redisService.saveToken(user, atk, rtk);

		return TokenDto.builder().email(user.getEmail())
			.accessToken(atk).refreshToken(rtk).build();
	}

	public TokenDto reissue(String refreshToken){
		jwtUtil.validateToken(refreshToken);

		String reqUserId = jwtUtil.getSubject(refreshToken);
		Optional<User> user = userRepository.findById(Long.parseLong(reqUserId));

		String savedRefreshToken = redisService.getRefreshToken(user.get());

		if(!refreshToken.equals(savedRefreshToken)){
			throw new ApiException(ErrorCode.INVALID_TOKEN);
		}
		String atk = jwtUtil.generateAccessToken(user.get());
		redisService.saveAccessToken(user.get(), atk);

		return TokenDto.builder().email(user.get().getEmail())
			.accessToken(atk).refreshToken(refreshToken).build();
	}

	public User logout(User user){
		redisService.deleteToken(user);
		return user;
	}

}
