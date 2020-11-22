package distribution.application.repository;

import distribution.application.usecase.UpdateMoneyDistUsecase;
import distribution.entity.MoneyDistribution;

public interface MoneyDistRepository {
  boolean save(MoneyDistribution newDistribution);

  int updateDistribution(UpdateMoneyDistUsecase.RequestDTO req);
}
