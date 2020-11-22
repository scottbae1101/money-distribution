package distribution.adapter.serializer;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import distribution.application.usecase.CreateInputDTO;
import distribution.application.exception.RequestBodyException;
import distribution.application.exception.RequestHeaderException;

import java.util.Map;

public class AwsLambdaSerializer implements MoneyDistSerializer {
  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public CreateInputDTO deserializeCreate(APIGatewayProxyRequestEvent input) throws Exception {
    // Validate Header
    Map<String, String> recvHeaders = input.getHeaders();
    String userId = recvHeaders.get("X-USER-ID");
    String roomId = recvHeaders.get("X-ROOM-ID");
    validateHeader(userId, roomId);

    // Validate Body
    try {
      String body = input.getBody();
      PostLambdaInput postLambdaInput = mapper.readValue(body, PostLambdaInput.class);
      return new CreateInputDTO(userId, roomId, postLambdaInput.getTotalAmount(), postLambdaInput.getGuestCnt());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new RequestBodyException("Some required values are missing in request body");
    }
  }

  private void validateHeader(String userId, String roomId) throws Exception {
    if (userId == null)
      throw new RequestHeaderException("userId in header is missing");
    if (roomId == null)
      throw new RequestHeaderException("roomId in header is missing");
  }
}
