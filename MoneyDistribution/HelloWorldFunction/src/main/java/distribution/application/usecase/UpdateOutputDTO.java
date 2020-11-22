package distribution.application.usecase;

public class UpdateOutputDTO {
  private int stausCode;
  private String result;

  public UpdateOutputDTO(int stausCode) {
    this.stausCode = stausCode;
  }

  public UpdateOutputDTO(int stausCode, String result) {
    this.stausCode = stausCode;
    this.result = result;
  }

  public int getStausCode() {
    return stausCode;
  }

  public String getResult() {
    return result;
  }
}
