package app.todoit.global.exception;

public class ApiException extends RuntimeException{
  public ErrorCode errorCode;

  public ApiException(ErrorCode errorCode){
    this.errorCode = errorCode;
  }
  public ApiException(ErrorCode errorCode, String msg){
    super(msg);
    this.errorCode = errorCode;
  }
}
