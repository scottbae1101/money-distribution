package distribution.adapter.serializer;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import distribution.application.exception.RequestException;
import distribution.application.usecase.CreateInputDTO;
import distribution.application.exception.RequestBodyException;
import distribution.application.exception.RequestHeaderException;
import distribution.application.usecase.GetInputDTO;
import distribution.application.usecase.UpdateInputDTO;

import java.util.Map;

public class AwsLambdaSerializer implements MoneyDistSerializer {
  private ObjectMapper mapper = new ObjectMapper();
  private String userId;
  private String roomId;

  @Override
  public CreateInputDTO deserializeCreate(APIGatewayProxyRequestEvent input) throws Exception {
    // Validate Header
    validateHeader(input.getHeaders());

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

  @Override
  public UpdateInputDTO deserializeUpdate(APIGatewayProxyRequestEvent input) throws Exception {
    // Validate Header
    validateHeader(input.getHeaders());

    // Validate Body
    try {
      String body = input.getBody();
      UpdateLambdaInputBody updateLambdaInputBody = mapper.readValue(body, UpdateLambdaInputBody.class);
      return new UpdateInputDTO(userId, roomId, updateLambdaInputBody.getToken());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new RequestBodyException("Some required values are missing in request body");
    }
  }

  private void validateHeader(Map<String, String> recvHeaders) throws Exception {
    userId = recvHeaders.get("X-USER-ID");
    roomId = recvHeaders.get("X-ROOM-ID");
    if (userId == null)
      throw new RequestHeaderException("userId in header is missing");
    if (roomId == null)
      throw new RequestHeaderException("roomId in header is missing");
  }

  @Override
  public GetInputDTO deserializeGet(APIGatewayProxyRequestEvent input) throws Exception {
    // Validate Header
    validateHeader(input.getHeaders());

    String token = input.getPathParameters().get("token");
    if(token == null)
      throw new RequestException("token is missing in path-parameter");
    return new GetInputDTO(userId, roomId, token);
  }
}
