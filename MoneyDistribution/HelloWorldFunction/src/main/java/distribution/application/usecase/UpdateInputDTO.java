package distribution.application.usecase;

import lombok.Value;

@Value
public class UpdateInputDTO {
  private String userId;
  private String roomId;
  private String token;
}
