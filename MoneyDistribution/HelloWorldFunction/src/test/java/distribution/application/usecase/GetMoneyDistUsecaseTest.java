package distribution.application.usecase;

import distribution.application.repository.MoneyDistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetMoneyDistUsecaseTest {

  public static final int SEVEN_DAY_IN_SEC = 60 * 60 * 24 * 7;

  @BeforeEach
  void setUp() {
  }

  @Test
  @DisplayName("Fail on ownerId != userId")
  void tc1() {
    // Arrange
    GetMoneyDistUsecase.ResponseDTO response = new GetMoneyDistUsecase.ResponseDTO();
    response.setOwnerId("ownerId");
    response.setSucceeded(true);
    MoneyDistRepository mockRepo = mock(MoneyDistRepository.class);
    when(mockRepo.getDistribution(anyString())).thenReturn(response);

    GetMoneyDistUsecase.RequestDTO req = GetMoneyDistUsecase.RequestDTO.builder()
        .userId("userA")
        .roomId("DontCare")
        .requestEpoch(0)
        .token("abc")
        .build();
    GetMoneyDistUsecase cut = new GetMoneyDistUsecase(mockRepo);

    // Act
    GetMoneyDistUsecase.ResponseDTO actual = cut.execute(req);

    // Assert
    assertFalse(actual.isSucceeded());
  }

  @Test
  @DisplayName("Fail on time(over 7days past)")
  void tc2() {
    // Arrange
    long nowEpoch = new Date().getTime() / 1000;
    GetMoneyDistUsecase.ResponseDTO response = new GetMoneyDistUsecase.ResponseDTO();
    response.setOwnerId("ownerId");
    response.setCreateEpoch(nowEpoch - SEVEN_DAY_IN_SEC - 1);
    response.setSucceeded(true);
    MoneyDistRepository mockRepo = mock(MoneyDistRepository.class);
    when(mockRepo.getDistribution(anyString())).thenReturn(response);

    GetMoneyDistUsecase.RequestDTO req = GetMoneyDistUsecase.RequestDTO.builder()
        .userId("ownerId")
        .roomId("DontCare")
        .requestEpoch(nowEpoch)
        .token("abc")
        .build();
    GetMoneyDistUsecase cut = new GetMoneyDistUsecase(mockRepo);

    // Act
    GetMoneyDistUsecase.ResponseDTO actual = cut.execute(req);

    // Assert
    assertFalse(actual.isSucceeded());
  }

  @Test
  @DisplayName("Success on time(exact 7days)")
  void tc3() {
    // Arrange
    long nowEpoch = new Date().getTime() / 1000;
    GetMoneyDistUsecase.ResponseDTO response = new GetMoneyDistUsecase.ResponseDTO();
    response.setOwnerId("ownerId");
    response.setCreateEpoch(nowEpoch - SEVEN_DAY_IN_SEC);
    response.setSucceeded(true);
    MoneyDistRepository mockRepo = mock(MoneyDistRepository.class);
    when(mockRepo.getDistribution(anyString())).thenReturn(response);

    GetMoneyDistUsecase.RequestDTO req = GetMoneyDistUsecase.RequestDTO.builder()
        .userId("ownerId")
        .roomId("DontCare")
        .requestEpoch(nowEpoch)
        .token("abc")
        .build();
    GetMoneyDistUsecase cut = new GetMoneyDistUsecase(mockRepo);

    // Act
    GetMoneyDistUsecase.ResponseDTO actual = cut.execute(req);

    // Assert
    assertTrue(actual.isSucceeded());
  }


  @Test
  @DisplayName("Fail on invalid token")
  void tc4() {
    // Arrange
    GetMoneyDistUsecase.ResponseDTO response = new GetMoneyDistUsecase.ResponseDTO();
    response.setOwnerId("ownerId");
    response.setCreateEpoch(0);
    response.setSucceeded(true);
    response.setToken(null);
    MoneyDistRepository mockRepo = mock(MoneyDistRepository.class);
    when(mockRepo.getDistribution(anyString())).thenReturn(response);

    GetMoneyDistUsecase.RequestDTO req = GetMoneyDistUsecase.RequestDTO.builder()
        .userId("ownerId")
        .roomId("DontCare")
        .requestEpoch(0)
        .token("invalidToken")
        .build();
    GetMoneyDistUsecase cut = new GetMoneyDistUsecase(mockRepo);

    // Act
    GetMoneyDistUsecase.ResponseDTO actual = cut.execute(req);

    // Assert
    assertFalse(actual.isSucceeded());
  }
}