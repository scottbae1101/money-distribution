package distribution.framework.awslambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import distribution.application.usecase.CreateOutputDTO;
import distribution.adapter.controller.CreateMoneyDistController;
import distribution.framework.main.CreateMoneyDistMain;

import java.util.HashMap;
import java.util.Map;

public class PostMoneyDistribution implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
  public static final int RESULT_OK = 200;
  private CreateMoneyDistController controller = CreateMoneyDistMain.load();

  public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
    CreateOutputDTO result = this.controller.create(input);

    APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(buildHeaders());
    int resultCode = result.getStausCode();
    if (resultCode == RESULT_OK) {
      return response
          .withStatusCode(RESULT_OK)
          .withBody(result.getResult());
    } else {
      return response
          .withBody(result.getResult())
          .withStatusCode(resultCode);
    }
  }

  private Map<String, String> buildHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("X-Custom-Header", "application/json");
    return headers;
  }
}
