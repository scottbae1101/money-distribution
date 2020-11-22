package distribution.application.usecase;

import java.util.List;

public interface MoneySplitAlgorithm {
  List<Integer> splitMoney(int amount, int cnt);
}
