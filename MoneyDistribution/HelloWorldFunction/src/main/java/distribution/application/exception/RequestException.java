package distribution.application.exception;

public class RequestException extends MoneyDistributionError {
  public RequestException(String message) {
    super(message);
    super.statusCode = 400;
  }
}
