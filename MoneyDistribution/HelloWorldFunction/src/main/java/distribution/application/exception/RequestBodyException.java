package distribution.application.exception;

public class RequestBodyException extends MoneyDistributionError {
  public RequestBodyException(String message) {
    super(message);
    super.statusCode = 400;
  }
}
