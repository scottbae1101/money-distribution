package distribution.adapter.serializer;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import distribution.application.usecase.CreateInputDTO;

public interface MoneyDistSerializer {
  CreateInputDTO deserializeCreate(APIGatewayProxyRequestEvent input) throws Exception;
}
