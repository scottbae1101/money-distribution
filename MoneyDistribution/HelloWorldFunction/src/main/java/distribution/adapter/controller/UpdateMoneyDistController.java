package distribution.adapter.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import distribution.adapter.serializer.MoneyDistSerializer;
import distribution.application.exception.MoneyDistributionError;
import distribution.application.usecase.UpdateInputDTO;
import distribution.application.usecase.UpdateMoneyDistUsecase;
import distribution.application.usecase.UpdateOutputDTO;

import java.util.Date;

public class UpdateMoneyDistController {
  public static final String FORMAT_RES_FOR_UPDATE = "{\"money\": \"%d\"}";
  public static final String FORMAT_ERR = "{ \"errorMessage\": \"%s\" }";
  public static final int STAUS_CODE_OK = 200;
  public static final int STAUS_CODE_ERR = 500;
  private final MoneyDistSerializer serializer;
  private final UpdateMoneyDistUsecase usecase;

  public UpdateMoneyDistController(UpdateMoneyDistUsecase usecase, MoneyDistSerializer serializer) {
    this.serializer = serializer;
    this.usecase = usecase;
  }

  public UpdateOutputDTO update(APIGatewayProxyRequestEvent input) {
    long nowEpoch = getNowEpoch();
    try {
      UpdateInputDTO in = this.serializer.deserializeUpdate(input);
      UpdateMoneyDistUsecase.RequestDTO requestDTO = UpdateMoneyDistUsecase.RequestDTO.builder()
          .userId(in.getUserId())
          .roomId(in.getRoomId())
          .token(in.getToken())
          .requestEpoch(nowEpoch)
          .build();

      UpdateMoneyDistUsecase.ResponseDTO response = this.usecase.execute(requestDTO);
      if (!response.isSucceeded())
        throw new Exception();

      int money = response.getDistributedMoney();
      return new UpdateOutputDTO(STAUS_CODE_OK, String.format(FORMAT_RES_FOR_UPDATE, money));
    } catch (MoneyDistributionError err) {
      return new UpdateOutputDTO(err.getStatusCode(), String.format(FORMAT_ERR, err.getMessage()));
    } catch (Exception e) {
      return new UpdateOutputDTO(STAUS_CODE_ERR, String.format(FORMAT_ERR, "Unknown error"));
    }
  }

  private long getNowEpoch() {
    return new Date().getTime() / 1000;
  }
}

