package distribution.application.usecase;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EqualMoneySplitAlgorithm implements MoneySplitAlgorithm{
  @Override
  public List<Integer> splitMoney(int amount, int cnt) {
    List<Integer> nums = IntStream
        .range(0, cnt)
        .mapToObj(i -> amount / cnt)
        .collect(Collectors.toList());
    return nums;
  }
}
