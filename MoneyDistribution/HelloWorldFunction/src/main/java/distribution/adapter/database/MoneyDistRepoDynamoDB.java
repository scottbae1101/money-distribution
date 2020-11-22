package distribution.adapter.database;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import distribution.application.repository.MoneyDistRepository;
import distribution.entity.MoneyDistribution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoneyDistRepoDynamoDB implements MoneyDistRepository {

  public static final String ATTR_TOKEN_PK = "PK";
  public static final String ATTR_OWNER_ID = "ownerId";
  public static final String ATTR_ROOM_ID = "roomId";
  public static final String ATTR_CREATE_EPOCH = "createEpoch";
  public static final String ATTR_TOTAL_AMOUNT = "totalAmount";
  public static final String ATTR_GUEST_CNT = "guestCnt";
  public static final String ATTR_DISTRIBUTE_REMAINING_LIST = "distributeRemainingList";

  public MoneyDistRepoDynamoDB() {
  }

  @Override
  public boolean save(MoneyDistribution newDistribution) {
    AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard().build();

    String ownerId = newDistribution.getOwnerId();
    String roomId = newDistribution.getRoomId();
    String token = String.format("TOKEN#%s", newDistribution.getToken());
    String createEpoch = String.valueOf(newDistribution.getCreateEpoch());
    String totalAmount = String.valueOf(newDistribution.getTotalAmount());
    String guestCnt = String.valueOf(newDistribution.getGuestCnt());
    List<AttributeValue> distributeRemainingList = newDistribution.getDistributeRemainingList()
        .stream()
        .map(each -> new AttributeValue().withN(String.valueOf(each)))
        .collect(Collectors.toList());

    Map<String, AttributeValue> item = new HashMap<>();
    item.put(ATTR_OWNER_ID, (new AttributeValue()).withS(ownerId));
    item.put(ATTR_ROOM_ID, (new AttributeValue()).withS(roomId));
    item.put(ATTR_TOKEN_PK, (new AttributeValue()).withS(token));
    item.put(ATTR_CREATE_EPOCH, (new AttributeValue()).withN(createEpoch));
    item.put(ATTR_TOTAL_AMOUNT, (new AttributeValue()).withN(totalAmount));
    item.put(ATTR_GUEST_CNT, (new AttributeValue()).withN(guestCnt));
    item.put(ATTR_DISTRIBUTE_REMAINING_LIST, (new AttributeValue().withL(distributeRemainingList)));

    PutItemRequest putItemRequest = (new PutItemRequest())
        .withTableName("money-distribution")
        .withConditionExpression("attribute_not_exists(" + ATTR_TOKEN_PK + ")")
        .withItem(item);

    PutItemResult putItemResult = ddb.putItem(putItemRequest);
    int actualCode = putItemResult.getSdkHttpMetadata().getHttpStatusCode();
    if (actualCode != 200) {
      return false;
    }
    return true;
  }
}
