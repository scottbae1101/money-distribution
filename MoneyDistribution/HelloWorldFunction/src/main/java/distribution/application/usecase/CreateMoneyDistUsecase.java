package distribution.application.usecase;

import distribution.application.repository.MoneyDistRepository;
import distribution.entity.MoneyDistribution;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CreateMoneyDistUsecase extends UseCase<CreateMoneyDistUsecase.RequestDTO, CreateMoneyDistUsecase.ResponseDTO> {
  private final MoneySplitAlgorithm splitAlgorithm;
  private final MoneyDistRepository repository;

  public CreateMoneyDistUsecase(MoneyDistRepository repository, MoneySplitAlgorithm splitAlgorithm) {
    super();
    this.repository = repository;
    this.splitAlgorithm = splitAlgorithm;
  }

  @Data
  @Builder
  public static class RequestDTO implements UseCase.RequestDTO {
    private String ownerId;
    private String roomId;
    private int totalAmount;
    private int questCnt;
  }

  @Value
  public static class ResponseDTO implements UseCase.ResponseDTO {
    String token;
  }

  @Override
  public ResponseDTO execute(RequestDTO req) {
    final int MAX_REPO_FAIL_TRIAL = 3; // TODO: Should be configured by policy

    List<Integer> dividendParts = calcDividendParts(req);

    for (int i = 0; i < MAX_REPO_FAIL_TRIAL; i++) {
      String randomToken = genRandomToken();
      long nowEpoch = getNowEpoch();
      MoneyDistribution newDistribution = newMoneyDistribution(req, dividendParts, nowEpoch, randomToken);
      if (repository.save(newDistribution)) {
        return new ResponseDTO(randomToken);
      }
    }
    return new ResponseDTO("");
  }

  private List<Integer> calcDividendParts(RequestDTO req) {
    return this.splitAlgorithm.splitMoney(req.getTotalAmount(), req.getQuestCnt());
  }

  private String genRandomToken() {
    return UUID.randomUUID().toString().substring(0, 3);
  }

  private long getNowEpoch() {
    return new Date().getTime();
  }

  private MoneyDistribution newMoneyDistribution(RequestDTO req, List<Integer> dividendParts, long nowEpoch, String randomToken) {
    MoneyDistribution newDistribution = MoneyDistribution.builder()
        .ownerId(req.ownerId)
        .roomId(req.roomId)
        .token(randomToken)
        .createEpoch(nowEpoch)
        .totalAmount(req.totalAmount)
        .guestCnt(req.questCnt)
        .distributeRemainingList(dividendParts)
        .build();
    return newDistribution;
  }
}
