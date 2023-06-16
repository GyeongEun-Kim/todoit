package app.todoit.global.exception;

public class NotFoundException extends ApiException{

  public NotFoundException(ErrorCode errorCode){
    super(errorCode);
  }
  public NotFoundException(ErrorCode errorCode, String userNotFound) {
    super(errorCode, "not found in: "+ userNotFound);
  }
}
