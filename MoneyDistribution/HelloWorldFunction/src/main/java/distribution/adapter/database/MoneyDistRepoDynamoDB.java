package distribution.adapter.database;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import distribution.application.repository.MoneyDistRepository;
import distribution.application.usecase.GetMoneyDistUsecase;
import distribution.application.usecase.UpdateMoneyDistUsecase;
import distribution.entity.MoneyDistribution;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class MoneyDistRepoDynamoDB implements MoneyDistRepository {

  public static final String ATTR_TOKEN_PK = "PK";
  public static final String ATTR_OWNER_ID = "ownerId";
  public static final String ATTR_ROOM_ID = "roomId";
  public static final String ATTR_CREATE_EPOCH = "createEpoch";
  public static final String ATTR_TOTAL_AMOUNT = "totalAmount";
  public static final String ATTR_GUEST_CNT = "guestCnt";
  public static final String ATTR_DISTRIBUTE_REMAINING_LIST = "distributeRemainingList";
  public static final String FORMAT_PK_TOKEN = "TOKEN#%s";
  public static final int RESPONSE_OK = 200;
  public static final String ATTR_DISTRIBUTE_INFO_MAP = "distributeInfoMap";
  private final AmazonDynamoDB ddb;
  private String tableName;

  public MoneyDistRepoDynamoDB() {
    ddb = AmazonDynamoDBClientBuilder.standard().build();
    tableName = System.getenv("DDB_NAME") == null ? "money-distribution" : System.getenv("DDB_NAME");
  }

  @Override
  public boolean save(MoneyDistribution newDistribution) {
    String ownerId = newDistribution.getOwnerId();
    String roomId = newDistribution.getRoomId();
    String token = String.format(FORMAT_PK_TOKEN, newDistribution.getToken());
    String createEpoch = String.valueOf(newDistribution.getCreateEpoch());
    String totalAmount = String.valueOf(newDistribution.getTotalAmount());
    String guestCnt = String.valueOf(newDistribution.getGuestCnt());
    List<AttributeValue> distributeRemainingList = newDistribution.getDistributeRemainingList()
        .stream()
        .map(each -> new AttributeValue().withN(String.valueOf(each)))
        .collect(Collectors.toList());
    Map<String, AttributeValue> distInfoMap = new HashMap<>();
    newDistribution.getDistributeInfoMap().forEach((s, integer) -> distInfoMap.put(s, new AttributeValue().withN(String.valueOf(integer))));

    Map<String, AttributeValue> item = new HashMap<>();
    item.put(ATTR_OWNER_ID, (new AttributeValue()).withS(ownerId));
    item.put(ATTR_ROOM_ID, (new AttributeValue()).withS(roomId));
    item.put(ATTR_TOKEN_PK, (new AttributeValue()).withS(token));
    item.put(ATTR_CREATE_EPOCH, (new AttributeValue()).withN(createEpoch));
    item.put(ATTR_TOTAL_AMOUNT, (new AttributeValue()).withN(totalAmount));
    item.put(ATTR_GUEST_CNT, (new AttributeValue()).withN(guestCnt));
    item.put(ATTR_DISTRIBUTE_REMAINING_LIST, (new AttributeValue().withL(distributeRemainingList)));
    item.put(ATTR_DISTRIBUTE_INFO_MAP, (new AttributeValue().withM(distInfoMap)));

    PutItemRequest putItemRequest = (new PutItemRequest())
        .withTableName(tableName)
        .withConditionExpression("attribute_not_exists(" + ATTR_TOKEN_PK + ")")
        .withItem(item);

    PutItemResult putItemResult = ddb.putItem(putItemRequest);
    int actualCode = putItemResult.getSdkHttpMetadata().getHttpStatusCode();
    if (actualCode != RESPONSE_OK) {
      return false;
    }
    return true;
  }

  @Override
  public int updateDistribution(UpdateMoneyDistUsecase.RequestDTO req) {
    String token = req.getToken();
    String tokenPk = String.format(FORMAT_PK_TOKEN, token);
    String userId = req.getUserId();
    String roomId = req.getRoomId();
    long epoch10MinAgo = req.getRequestEpoch() - 60 * 10;

    try {
      DynamoDB dynamoDB = new DynamoDB(ddb);
      Table table = dynamoDB.getTable(tableName);
      UpdateItemOutcome outcome = checkAndUpdateDistribution(tokenPk, userId, roomId, epoch10MinAgo, table);
      List<BigDecimal> distributeRemainingList = (List) outcome.getItem().get("distributeRemainingList");
      int money = distributeRemainingList.get(0).toBigInteger().intValue();

      updateDistributionInfo(tokenPk, userId, money);
      updateRecvUsers(tokenPk, userId, table);
      return money;
    } catch (Exception e) {
      System.err.println(e.getMessage());
      throw e;
    }
  }

  private UpdateItemOutcome checkAndUpdateDistribution(String tokenPk, String userId, String roomId, long epoch10MinAgo, Table table) {
    // fail: token invalid
    // fail: userId == ownerId
    // fail: roomId != roomId
    // fail: check time(>10min)
    // fail: userId in recvUsers
    UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("PK", tokenPk)
        .withConditionExpression("attribute_exists(PK)" + " AND " +
            "createEpoch >= :epoch AND " +
            "ownerId <> :user AND " +
            "roomId = :room AND " +
            "not attribute_exists(" + userId + ") AND " +
            "size(distributeRemainingList) >= :sizeVal")
        .withValueMap(
            new ValueMap().withNumber(":epoch", epoch10MinAgo)
                .withString(":user", userId)
                .withString(":room", roomId)
                .withNumber(":sizeVal", 1)
        )
        .withUpdateExpression("REMOVE distributeRemainingList[0]")
        .withReturnValues(ReturnValue.ALL_OLD);
    UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
    return outcome;
  }

  private void updateDistributionInfo(String tokenPk, String userId, int money) {
    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
    Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
    key.put(ATTR_TOKEN_PK, new AttributeValue().withS(tokenPk));
    expressionAttributeValues.put(":user", (new AttributeValue()).withN(String.valueOf(money)));
    UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(tableName).withKey(key)
        .withUpdateExpression("SET " + userId + " = :user")
        .withExpressionAttributeValues(expressionAttributeValues).withReturnValues(ReturnValue.NONE);
    ddb.updateItem(updateItemRequest);
  }

  private void updateRecvUsers(String tokenPk, String userId, Table table) {
    Map<String, String> expressionAttributeNames = new HashMap<String, String>();
    expressionAttributeNames.put("#A", "recvUsers");

    Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
    expressionAttributeValues.put(":val1",
        new HashSet<String>(Arrays.asList(userId)));

    table.updateItem(ATTR_TOKEN_PK, tokenPk, "add #A :val1", expressionAttributeNames, expressionAttributeValues);
  }

  @Override
  public GetMoneyDistUsecase.ResponseDTO getDistribution(String token) {
    DynamoDB dynamoDB = new DynamoDB(ddb);
    Table table = dynamoDB.getTable(tableName);

    QuerySpec querySpec = buildQueryParam(token);
    GetMoneyDistUsecase.ResponseDTO res = new GetMoneyDistUsecase.ResponseDTO();
    try {
      ItemCollection<QueryOutcome> items = table.query(querySpec);
      Iterator<Item> iterator = items.iterator();
      Item item = null;
      while (iterator.hasNext()) {
        item = iterator.next();
      }

      res.setCreateEpoch(item.getBigInteger("createEpoch").intValue());
      res.setTotalAmount(item.getBigInteger("totalAmount").intValue());
      res.setOwnerId(item.getString("ownerId"));
      if(item != null) {
        res.setToken(token);
      }

      HashMap<String, Integer> distInfoMap = new HashMap<>();
      ArrayList<HashMap<String, Integer>> list = new ArrayList<>();
      list.add(distInfoMap);
      res.setDistributionList(list);
      List<String> recvUsers = item.getList("recvUsers");
      int distributedAmount = 0;
      for (String recvUser : recvUsers) {
        int money = item.getBigInteger(recvUser).intValue();
        distributedAmount += money;
        distInfoMap.put(recvUser, money);
      }
      res.setDistributedAmount(distributedAmount);
      res.setSucceeded(true);
      return res;
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    return res;
  }

  private QuerySpec buildQueryParam(String token) {
    HashMap<String, String> nameMap = new HashMap<>();
    nameMap.put("#pk", "PK");
    HashMap<String, Object> valueMap = new HashMap<>();
    valueMap.put(":pk", String.format(FORMAT_PK_TOKEN, token));

    QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#pk = :pk").withNameMap(nameMap)
        .withValueMap(valueMap);
    return querySpec;
  }
}

