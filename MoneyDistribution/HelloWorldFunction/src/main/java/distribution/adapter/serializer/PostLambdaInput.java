package distribution.adapter.serializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

public class PostLambdaInput {
  private int totalAmount;
  private int guestCnt;
  @JsonCreator
  public PostLambdaInput(
      @JsonProperty(value = "totalAmount", required = true) int totalAmount,
      @JsonProperty(value = "guestCnt", required = true) int guestCnt) {

    this.totalAmount = totalAmount;
    this.guestCnt = guestCnt;
  }

  public int getTotalAmount() {
    return totalAmount;
  }

  public int getGuestCnt() {
    return guestCnt;
  }
}
