package distribution.adapter.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import distribution.adapter.serializer.MoneyDistSerializer;
import distribution.application.exception.MoneyDistributionError;
import distribution.application.usecase.CreateInputDTO;
import distribution.application.usecase.CreateMoneyDistUsecase;
import distribution.application.usecase.CreateOutputDTO;

public class CreateMoneyDistController {
  private final MoneyDistSerializer serializer;
  private final CreateMoneyDistUsecase usecase;

  public CreateMoneyDistController(CreateMoneyDistUsecase usecase, MoneyDistSerializer serializer) {
    this.serializer = serializer;
    this.usecase = usecase;
  }

  public CreateOutputDTO create(APIGatewayProxyRequestEvent input) {
    try {
      CreateInputDTO in = this.serializer.deserializeCreate(input);
      CreateMoneyDistUsecase.RequestDTO requestDTO = CreateMoneyDistUsecase.RequestDTO.builder()
          .ownerId(in.getUserId())
          .roomId(in.getRoomId())
          .totalAmount(in.getTotalAmount())
          .questCnt(in.getGuestCnt())
          .build();
      CreateMoneyDistUsecase.ResponseDTO response = this.usecase.execute(requestDTO);
      String token = response.getToken();
      return new CreateOutputDTO(200, String.format("{\"token\": \"%s\", \"distributionLink\":\"distributions/%s\"}", token, token));
    } catch (MoneyDistributionError err) {
      return new CreateOutputDTO(err.getStatusCode(), String.format("{ \"errorMessage\": \"%s\" }", err.getMessage()));
    } catch (Exception e) {
      return new CreateOutputDTO(500, "{ \"errorMessage\": \"Unknown error\" }");
    }
  }
}

