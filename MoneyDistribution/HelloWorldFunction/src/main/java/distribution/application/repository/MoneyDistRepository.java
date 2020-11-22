package distribution.application.repository;

import distribution.entity.MoneyDistribution;

public interface MoneyDistRepository {
  boolean save(MoneyDistribution newDistribution);
}
