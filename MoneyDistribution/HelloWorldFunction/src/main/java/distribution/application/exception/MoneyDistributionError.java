package distribution.application.exception;

public class MoneyDistributionError extends Exception {
  protected int statusCode = 500;
  public MoneyDistributionError(String message) {
    super(message);
  }

  public int getStatusCode() {
    return statusCode;
  }
}
