package distribution.application.exception;

public class RequestHeaderException extends MoneyDistributionError {
  public RequestHeaderException(String message) {
    super(message);
    super.statusCode = 400;
  }
}
