package distribution.application.usecase;

public class CreateOutputDTO {
  private int stausCode;
  private String result;

  public CreateOutputDTO(int stausCode) {
    this.stausCode = stausCode;
  }

  public CreateOutputDTO(int stausCode, String result) {
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
