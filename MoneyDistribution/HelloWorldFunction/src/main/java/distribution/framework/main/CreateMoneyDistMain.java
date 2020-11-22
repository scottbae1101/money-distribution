package distribution.framework.main;

import distribution.adapter.controller.CreateMoneyDistController;
import distribution.adapter.database.MoneyDistRepoDynamoDB;
import distribution.adapter.serializer.AwsLambdaSerializer;
import distribution.application.usecase.CreateMoneyDistUsecase;
import distribution.application.usecase.EqualMoneySplitAlgorithm;

public class CreateMoneyDistMain {
  public static CreateMoneyDistController load() {
    AwsLambdaSerializer serializer = new AwsLambdaSerializer();
    EqualMoneySplitAlgorithm splitAlgorithm = new EqualMoneySplitAlgorithm();
    MoneyDistRepoDynamoDB repository = new MoneyDistRepoDynamoDB();
    CreateMoneyDistUsecase usecase = new CreateMoneyDistUsecase(repository, splitAlgorithm);
    return new CreateMoneyDistController(usecase, serializer);
  }
}
