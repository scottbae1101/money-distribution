package distribution.framework.main;

import distribution.adapter.controller.GetMoneyDistController;
import distribution.adapter.database.MoneyDistRepoDynamoDB;
import distribution.adapter.serializer.AwsLambdaSerializer;
import distribution.application.usecase.GetMoneyDistUsecase;

public class GetMoneyDistMain {
  public static GetMoneyDistController load() {
    AwsLambdaSerializer serializer = new AwsLambdaSerializer();
    MoneyDistRepoDynamoDB repository = new MoneyDistRepoDynamoDB();
    GetMoneyDistUsecase usecase = new GetMoneyDistUsecase(repository);
    return new GetMoneyDistController(usecase, serializer);
  }
}
