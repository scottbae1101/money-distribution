package distribution.application.usecase;

import lombok.Value;

@Value
public class GetInputDTO {
  private String userId;
  private String roomId;
  private String token;
}
