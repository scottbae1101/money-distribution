package distribution.application.usecase;

public class GetOutputDTO {
  private int stausCode;
  private String result;

  public GetOutputDTO(int stausCode) {
    this.stausCode = stausCode;
  }

  public GetOutputDTO(int stausCode, String result) {
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
