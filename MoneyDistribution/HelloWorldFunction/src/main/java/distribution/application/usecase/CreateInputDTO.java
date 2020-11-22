package distribution.application.usecase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class CreateInputDTO {
  private String userId;
  private String roomId;
  private int totalAmount;
  private int guestCnt;
}
