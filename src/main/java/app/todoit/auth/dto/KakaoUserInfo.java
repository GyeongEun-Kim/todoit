package app.todoit.auth.dto;

import lombok.Getter;

// dto 네이밍
@Getter
public class KakaoUserInfo {
	private String nickname;
	private String email;
	private String phone;
}
