package distribution.application.repository;

import distribution.application.usecase.GetMoneyDistUsecase;
import distribution.application.usecase.UpdateMoneyDistUsecase;
import distribution.entity.MoneyDistribution;

public interface MoneyDistRepository {
  boolean save(MoneyDistribution newDistribution);

  int updateDistribution(UpdateMoneyDistUsecase.RequestDTO req);

  GetMoneyDistUsecase.ResponseDTO getDistribution(String token);
}
