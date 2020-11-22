package distribution.adapter.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import distribution.adapter.serializer.AwsLambdaSerializer;
import distribution.adapter.serializer.PostLambdaInput;
import distribution.application.repository.MoneyDistRepository;
import distribution.application.usecase.CreateMoneyDistUsecase;
import distribution.application.usecase.CreateOutputDTO;
import distribution.application.usecase.EqualMoneySplitAlgorithm;
import distribution.entity.MoneyDistribution;
import distribution.framework.main.CreateMoneyDistMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class CreateMoneyDistControllerTest {
//  private MoneyDistController cut = new MoneyDistController(mock(CreateMoneyDist.class), mock(AwsLambdaSerializer.class));

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("Input body validator")
  void tc1() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    PostLambdaInput input = new PostLambdaInput(1000, 2);

    String jsonStr = mapper.writeValueAsString(input);
    System.out.println(jsonStr);

    PostLambdaInput postDistributionInput = mapper.readValue("{\"totalAmount\":1000,\"guestCnt\":2}", PostLambdaInput.class);
    System.out.println(postDistributionInput);
  }

  @Test
  @DisplayName("Input body validator with invalid input")
  void tc1_1() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      PostLambdaInput postDistributionInput = mapper.readValue("{\"guestCnt\":2}", PostLambdaInput.class);
      fail("should throw exception with invalid input");
    } catch (MismatchedInputException e) {
      // Ignore
    } catch (JsonMappingException e) {
      fail("Should not reach to here");
    } catch (JsonProcessingException e) {
      fail("Should not reach to here");
    }
  }

  @Test
  @DisplayName("AWS Lambda should serialize valid input")
  void tc2() {
    // Arrange
    APIGatewayProxyRequestEvent lambdaInput = mock(APIGatewayProxyRequestEvent.class);
    HashMap<String, String> headersMapMock = mock(HashMap.class);
    when(headersMapMock.get("X-USER-ID")).thenReturn("user_id");
    when(headersMapMock.get("X-ROOM-ID")).thenReturn("room_id");
    when(lambdaInput.getBody()).thenReturn("{\"totalAmount\":1000,\"guestCnt\":2}");
    when(lambdaInput.getHeaders()).thenReturn(headersMapMock);
    CreateMoneyDistController cut = new CreateMoneyDistController(new CreateMoneyDistUsecase(new MoneyDistRepository() {
      @Override
      public boolean save(MoneyDistribution newDistribution) {
        return true;
      }
    }, new EqualMoneySplitAlgorithm()), new AwsLambdaSerializer());

    // Act
    CreateOutputDTO actual = cut.create(lambdaInput);

    // Assert
    assertThat(actual.getStausCode(), is(200));
    assertThat(actual.getResult().length(), greaterThanOrEqualTo("{\"token\":\"abc\"}".length()));
    System.out.println(actual.getResult());
  }

  @Test
  @Disabled
  @DisplayName("Acceptance test(POST /distributions) with pre-condition of AWS infra(DynamoDB)")
  void tc3() {
    // Arrange
    CreateMoneyDistController ctrl = CreateMoneyDistMain.load();
    APIGatewayProxyRequestEvent lambdaInput = mock(APIGatewayProxyRequestEvent.class);
    HashMap<String, String> headersMapMock = mock(HashMap.class);
    when(headersMapMock.get("X-USER-ID")).thenReturn("user_id");
    when(headersMapMock.get("X-ROOM-ID")).thenReturn("room_id");
    when(lambdaInput.getHeaders()).thenReturn(headersMapMock);
    when(lambdaInput.getBody()).thenReturn("{\"totalAmount\":1000,\"guestCnt\":3}");

    // Act
    CreateOutputDTO actual = ctrl.create(lambdaInput);

    // Assert
    assertThat(actual.getStausCode(), is(200));
    assertThat(actual.getResult().length(), greaterThanOrEqualTo("{\"token\":\"abc\"}".length()));
    System.out.println(actual.getResult());
  }
}
