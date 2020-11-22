package distribution.adapter.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import distribution.adapter.serializer.MoneyDistSerializer;
import distribution.application.exception.MoneyDistributionError;
import distribution.application.usecase.CreateInputDTO;
import distribution.application.usecase.CreateMoneyDistUsecase;
import distribution.application.usecase.CreateOutputDTO;

public class CreateMoneyDistController {
  public static final int STAUS_CODE_OK = 200;
  public static final int STAUS_CODE_ERR = 500;
  public static final String FORMAT_ERR = "{ \"errorMessage\": \"%s\" }";
  public static final String FORMAT_RES_FOR_CREATE = "{\"token\": \"%s\", \"distributionLink\":\"distributions/%s\"}";
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
      return new CreateOutputDTO(STAUS_CODE_OK, String.format(FORMAT_RES_FOR_CREATE, token, token));
    } catch (MoneyDistributionError err) {
      return new CreateOutputDTO(err.getStatusCode(),String.format(FORMAT_ERR, err.getMessage()));
    } catch (Exception e) {
      return new CreateOutputDTO(STAUS_CODE_ERR, String.format(FORMAT_ERR, "Unknown error"));
    }
  }
}

