package app.todoit.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleCustomException(final ApiException e){
		return handleExceptionInternal(e.errorCode);
	}

	protected ResponseEntity<ErrorResponse> handleExceptionInternal(ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getHttpStatus()).body(new ErrorResponse(errorCode));
	}
}
