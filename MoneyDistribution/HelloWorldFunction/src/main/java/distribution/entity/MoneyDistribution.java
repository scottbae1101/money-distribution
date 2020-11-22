package distribution.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

@Builder
@Getter
public class MoneyDistribution {
  private final String ownerId;
  private final String roomId;
  private final String token;
  private final long createEpoch;
  private final int totalAmount;
  private final int guestCnt;
  private List<Integer> distributeRemainingList;
  private final HashMap<String, Integer> distributeInfoMap = new HashMap<>(); // <userId, amountOfMoney>
}
