package distribution.adapter.serializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateLambdaInputBody {
  private String token;

  @JsonCreator
  public UpdateLambdaInputBody(
      @JsonProperty(value = "token", required = true) String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
