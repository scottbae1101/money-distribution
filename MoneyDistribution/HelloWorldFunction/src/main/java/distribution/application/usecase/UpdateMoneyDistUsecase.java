package distribution.application.usecase;

import distribution.application.repository.MoneyDistRepository;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

public class UpdateMoneyDistUsecase extends UseCase<UpdateMoneyDistUsecase.RequestDTO, UpdateMoneyDistUsecase.ResponseDTO> {
  private final MoneyDistRepository repository;

  public UpdateMoneyDistUsecase(MoneyDistRepository repository) {
    super();
    this.repository = repository;
  }

  @Data
  @Builder
  public static class RequestDTO implements UseCase.RequestDTO {
    private String userId;
    private String roomId;
    private String token;
    private long requestEpoch;
  }

  @Value
  public static class ResponseDTO implements UseCase.ResponseDTO {
    private boolean isSucceeded;
    private int distributedMoney;
  }

  @Override
  public ResponseDTO execute(RequestDTO req) {
    try {
      int amount = repository.updateDistribution(req);
      return new ResponseDTO(true, amount);
    } catch (Exception e) {
      return new ResponseDTO(false, 0);
    }
  }
}
