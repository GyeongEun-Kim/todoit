package app.todoit.domain.auth.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.todoit.domain.auth.dto.JoinRequestDto;
import app.todoit.domain.auth.dto.KakaoUserDto;
import app.todoit.domain.auth.dto.KakaoUserResponse;
import app.todoit.domain.auth.dto.TokenDto;
import app.todoit.domain.auth.entity.User;
import app.todoit.domain.auth.service.AuthService;
import app.todoit.global.annotation.WithOutAuth;
import app.todoit.global.interceptor.UserThreadLocal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@WithOutAuth
	@PostMapping("/kakao")
	public ResponseEntity<KakaoUserResponse> getUserInfo(@RequestBody KakaoUserDto kakaoUserDto){
		boolean isJoined = authService.isJoined(kakaoUserDto);
		String message;
		if(isJoined)
			message="Exist User in DB";
		else message="No Exist User in DB";

		return ResponseEntity.ok().body(KakaoUserResponse.builder().email(kakaoUserDto.getEmail()).nickname(
				kakaoUserDto.getNickname()).isJoined(Boolean.toString(isJoined)).message(message).build());
	}

	@WithOutAuth
	@PostMapping("/join")
	public ResponseEntity joinUp(@RequestBody JoinRequestDto joinRequestDto){
		TokenDto tokenDto = authService.joinUp(joinRequestDto);
		return ResponseEntity.ok().body(tokenDto);
	}

	@WithOutAuth
	@PostMapping("/login")
	public ResponseEntity login(@RequestBody KakaoUserDto kakaoUserDto){
		TokenDto tokenDto = authService.login(kakaoUserDto);
		return ResponseEntity.ok().body(tokenDto);
	}

	@WithOutAuth
	@PostMapping("/reissue")
	public ResponseEntity reissue(@RequestHeader("Authorization") String refreshToken){
		TokenDto tokenDto = authService.reissue(refreshToken);
		return ResponseEntity.ok().body(tokenDto);
	}

	@PostMapping("/logout")
	public ResponseEntity logout(){
		User user = UserThreadLocal.get();
		User logoutUser = authService.logout(user);
		return ResponseEntity.ok().body(logoutUser.getEmail() + ": logout success");
	}

}
