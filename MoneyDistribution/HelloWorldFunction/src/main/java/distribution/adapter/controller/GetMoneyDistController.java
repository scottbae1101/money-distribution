package distribution.adapter.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import distribution.adapter.serializer.MoneyDistSerializer;
import distribution.application.exception.MoneyDistributionError;
import distribution.application.usecase.UpdateInputDTO;
import distribution.application.usecase.GetMoneyDistUsecase;
import distribution.application.usecase.GetOutputDTO;

import java.util.Date;

public class GetMoneyDistController {
  public static final String FORMAT_ERR = "{ \"Message\": \"%s\" }";
  public static final int STAUS_CODE_OK = 200;
  public static final int STAUS_CODE_ERR = 400;
  private final MoneyDistSerializer serializer;
  private final GetMoneyDistUsecase usecase;

  public GetMoneyDistController(GetMoneyDistUsecase usecase, MoneyDistSerializer serializer) {
    this.serializer = serializer;
    this.usecase = usecase;
  }

  public GetOutputDTO get(APIGatewayProxyRequestEvent input) {
    long nowEpoch = getNowEpoch();
    try {
      UpdateInputDTO in = this.serializer.deserializeUpdate(input);
      GetMoneyDistUsecase.RequestDTO requestDTO = GetMoneyDistUsecase.RequestDTO.builder()
          .userId(in.getUserId())
          .roomId(in.getRoomId())
          .token(in.getToken())
          .requestEpoch(nowEpoch)
          .build();

      GetMoneyDistUsecase.ResponseDTO response = this.usecase.execute(requestDTO);
      if (!response.isSucceeded())
        throw new Exception();

      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writeValueAsString(response);

      return new GetOutputDTO(STAUS_CODE_OK, json);
    } catch (MoneyDistributionError err) {
      return new GetOutputDTO(err.getStatusCode(), String.format(FORMAT_ERR, err.getMessage()));
    } catch (Exception e) {
      return new GetOutputDTO(STAUS_CODE_ERR, String.format(FORMAT_ERR, "No distribution available or Not allowed to get distribution"));
    }
  }

  private long getNowEpoch() {
    return new Date().getTime() / 1000;
  }
}

