package app.todoit.domain.auth.dto;

import app.todoit.domain.auth.entity.User;
import lombok.Getter;

@Getter
public class KakaoUserDto {
	private String email;
	private String nickname;

	public User toEntity(){
		return User.builder()
			.email(email)
			.nickname(nickname)
			.build();
	}
}
