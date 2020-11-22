package distribution.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.Hashtable;
import java.util.List;

@Builder
@Getter
public class MoneyDistribution {
  private final String ownerId;
  private final String roomId;
  private final String token; // 뿌리기 ID
  private final long createEpoch;
  private final int totalAmount; // 뿌릴 금액
  private final int guestCnt; // 뿌릴 인원수
  private List<Integer> distributeRemainingList;
  private final Hashtable<String, Integer> distributeInfoTable = new Hashtable<>(); // 뿌린 정보 ==> <Who, amountOfMoney>
}
