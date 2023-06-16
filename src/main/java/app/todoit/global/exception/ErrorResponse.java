package app.todoit.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {
	private final HttpStatus httpStatus;
	private final String message;

	public ErrorResponse(ErrorCode errorCode){
		this.httpStatus = errorCode.getHttpStatus();
		this.message = errorCode.getMessage();
	}
}
