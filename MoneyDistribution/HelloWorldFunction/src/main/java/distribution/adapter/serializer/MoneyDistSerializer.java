package distribution.adapter.serializer;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import distribution.application.usecase.CreateInputDTO;
import distribution.application.usecase.GetInputDTO;
import distribution.application.usecase.UpdateInputDTO;

public interface MoneyDistSerializer {
  CreateInputDTO deserializeCreate(APIGatewayProxyRequestEvent input) throws Exception;
  UpdateInputDTO deserializeUpdate(APIGatewayProxyRequestEvent input) throws Exception;
  GetInputDTO deserializeGet(APIGatewayProxyRequestEvent input) throws Exception;
}
