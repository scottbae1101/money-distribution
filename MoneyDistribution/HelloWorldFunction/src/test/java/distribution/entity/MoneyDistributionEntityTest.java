//package distribution.entity;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
//import com.amazonaws.services.dynamodbv2.document.*;
//import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import distribution.application.usecase.GetMoneyDistUsecase;
//import distribution.application.usecase.UseCase;
//import lombok.Data;
//import org.junit.Test;
//
//import java.math.BigInteger;
//import java.util.*;
//
//import static org.hamcrest.Matchers.hasProperty;
//import static org.hamcrest.Matchers.in;
//import static org.junit.Assert.assertEquals;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.MatcherAssert.assertThat;
//
//public class MoneyDistributionEntityTest {
//  @Test
//  public void newDistribution() {
//    MoneyDistribution cut = MoneyDistribution.builder().build();
//    assertThat(cut, hasProperty("ownerId"));
//    assertThat(cut, hasProperty("roomId"));
//    assertThat(cut, hasProperty("token"));
//    assertThat(cut, hasProperty("createEpoch"));
//    assertThat(cut, hasProperty("totalAmount"));
//    assertThat(cut, hasProperty("guestCnt"));
//    assertThat(cut, hasProperty("distributeInfoMap"));
//    assertThat(cut, hasProperty("distributeRemainingList"));
//  }
//
//  @Test
//  public void db() throws JsonProcessingException {
//    HashMap<String, Integer> distributionInfoMap = new HashMap<>();
//    distributionInfoMap.put("sample", 100);
//    ArrayList<HashMap<String, Integer>> infoList = new ArrayList<>();
//    infoList.add(distributionInfoMap);
//    GetMoneyDistUsecase.ResponseDTO response = new GetMoneyDistUsecase.ResponseDTO();
//    response.setOwnerId("ownerId");
//    response.setCreateEpoch(10);
//    response.setTotalAmount(1000);
//    response.setToken("abc");
//    response.setDistributionList(infoList);
//    ObjectMapper mapper = new ObjectMapper();
//    String json = mapper.writeValueAsString(response);
//    System.out.println(json);
//  }
//}
//
//@Data
//class Sample implements UseCase.ResponseDTO {
//  private boolean isSucceeded;
//  private String ownerId;
//  private String token;
//  private long createEpoch;
//  private int totalAmount;
//  private int distributedAmount;
//  private HashMap<String, Integer> distributionInfoMap;
//}
