package distribution.adapter.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import distribution.adapter.controller.CreateMoneyDistController;
import distribution.adapter.controller.UpdateMoneyDistController;
import distribution.application.usecase.CreateOutputDTO;
import distribution.application.usecase.UpdateMoneyDistUsecase;
import distribution.application.usecase.UpdateOutputDTO;
import distribution.framework.main.CreateMoneyDistMain;
import distribution.framework.main.UpdateMoneyDistMain;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateMoneyDistControllerTest {
  @Test
  @Disabled
  @DisplayName("Acceptance test(PUT /distributions) with pre-condition of AWS infra(DynamoDB) and pre-stored data")
  void tc1() {
    // Arrange
    UpdateMoneyDistController ctrl = UpdateMoneyDistMain.load();
    APIGatewayProxyRequestEvent lambdaInput = mock(APIGatewayProxyRequestEvent.class);
    HashMap<String, String> headersMapMock = mock(HashMap.class);
    when(headersMapMock.get("X-USER-ID")).thenReturn("not_owner_id01");
    when(headersMapMock.get("X-ROOM-ID")).thenReturn("room01");
    when(lambdaInput.getHeaders()).thenReturn(headersMapMock);
    when(lambdaInput.getBody()).thenReturn("{\"token\":\"0a4\"}");

    // Act
    UpdateOutputDTO actual = ctrl.update(lambdaInput);

    // Assert
    assertThat(actual.getStausCode(), is(200));
    assertThat(actual.getResult().length(), greaterThanOrEqualTo("{\"money\":0}".length()));
    System.out.println(actual.getResult());
  }
}