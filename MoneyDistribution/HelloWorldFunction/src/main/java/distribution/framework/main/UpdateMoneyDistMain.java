package distribution.framework.main;

import distribution.adapter.controller.UpdateMoneyDistController;
import distribution.adapter.database.MoneyDistRepoDynamoDB;
import distribution.adapter.serializer.AwsLambdaSerializer;
import distribution.application.usecase.UpdateMoneyDistUsecase;

public class UpdateMoneyDistMain {
  public static UpdateMoneyDistController load() {
    AwsLambdaSerializer serializer = new AwsLambdaSerializer();
    MoneyDistRepoDynamoDB repository = new MoneyDistRepoDynamoDB();
    UpdateMoneyDistUsecase usecase = new UpdateMoneyDistUsecase(repository);
    return new UpdateMoneyDistController(usecase, serializer);
  }
}
