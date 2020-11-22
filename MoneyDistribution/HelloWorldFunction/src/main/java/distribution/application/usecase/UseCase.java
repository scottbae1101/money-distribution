package distribution.application.usecase;

public abstract class UseCase<Q extends UseCase.RequestDTO, P extends UseCase.ResponseDTO> {
  public abstract P execute(Q requestDTO);

  public interface RequestDTO {
  }

  public interface ResponseDTO {
  }
}
