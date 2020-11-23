package distribution.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import distribution.application.repository.MoneyDistRepository;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.HashMap;
import java.util.List;

public class GetMoneyDistUsecase extends UseCase<GetMoneyDistUsecase.RequestDTO, GetMoneyDistUsecase.ResponseDTO> {
  private final MoneyDistRepository repository;

  public GetMoneyDistUsecase(MoneyDistRepository repository) {
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

  @Data
  public static class ResponseDTO implements UseCase.ResponseDTO {
    @JsonIgnore
    private boolean isSucceeded;
    @JsonIgnore
    private String ownerId;
    private String token;
    private long createEpoch;
    private int totalAmount;
    private int distributedAmount;
    private List<HashMap<String, Integer>> distributionList;
  }

  public static final int SEVEN_DAY_IN_SEC = 60 * 60 * 24 * 7;

  @Override
  public ResponseDTO execute(RequestDTO req) {
    try {
      ResponseDTO result = repository.getDistribution(req.getToken());
      if (checkFailOnInvalidId(req, result)) {
        result.setSucceeded(false);
        return result;
      }
      if (checkFailOnTime(req, result)) {
        result.setSucceeded(false);
        return result;
      }
      if (checkFileOnInvalidToken(result)) {
        result.setSucceeded(false);
        return result;
      }
      return result;
    } catch (Exception e) {
      throw e;
    }
  }

  private boolean checkFileOnInvalidToken(ResponseDTO result) {
    return result.getToken() == null;
  }

  private boolean checkFailOnTime(RequestDTO req, ResponseDTO result) {
    return req.getRequestEpoch() - SEVEN_DAY_IN_SEC > result.getCreateEpoch();
  }

  private boolean checkFailOnInvalidId(RequestDTO req, ResponseDTO result) {
    return !req.getUserId().equals(result.getOwnerId());
  }
}
