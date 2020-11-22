package distribution.application.usecase;//package distribution.application.usecase;

import distribution.application.repository.MoneyDistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class CreateMoneyDistTest {
  @Mock
  MoneyDistRepository repoMock;
  @Mock
  MoneySplitAlgorithm splitAlgoMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("Request DTO")
  void tc1() {
    CreateMoneyDistUsecase.RequestDTO req = new CreateMoneyDistUsecase.RequestDTO("ownerId", "roomId", 1000, 2);
    assertThat(req, hasProperty("ownerId"));
    assertThat(req, hasProperty("roomId"));
    assertThat(req, hasProperty("totalAmount"));
    assertThat(req, hasProperty("questCnt"));
  }

  @Test
  @DisplayName("Response DTO")
  void tc2() {
    CreateMoneyDistUsecase.ResponseDTO res = new CreateMoneyDistUsecase.ResponseDTO("token");
    assertThat(res, hasProperty("token"));
  }

  @Test
  @DisplayName("make Money distribution")
  void tc3() {
    // Arrange
    CreateMoneyDistUsecase.RequestDTO req = new CreateMoneyDistUsecase.RequestDTO("ownerId", "roomId", 1000, 2);
    CreateMoneyDistUsecase cut = new CreateMoneyDistUsecase(repoMock, splitAlgoMock);
    when(repoMock.save(any())).thenReturn(true);

    // Act
    CreateMoneyDistUsecase.ResponseDTO res = cut.execute(req);

    // Assert
    assertThat(res.getToken(), hasLength(3));
  }

  @Test
  @DisplayName("equal money split test")
  void tc4() {
    // Arrange
    EqualMoneySplitAlgorithm cut = new EqualMoneySplitAlgorithm();
    int amount = 1000;
    int peopleCntEven = 2;

    // Act
    List<Integer> actualEven = cut.splitMoney(amount, peopleCntEven);

    // Assert
    assertThat(actualEven.size(), is(peopleCntEven));
    actualEven.forEach(each -> assertThat(each, is(amount / peopleCntEven)));

    // Arrange
    int peopleCntOdd = 3;

    // Act
    List<Integer> actual = cut.splitMoney(amount, peopleCntOdd);

    // Assert
    assertThat(actual.size(), is(peopleCntOdd));
    actual.forEach(each -> assertThat(each, is(amount / peopleCntOdd)));
  }

  @Test
  @DisplayName("make Money distribution with equal distribute algorithm")
  void tc5() {
    // Arrange
    CreateMoneyDistUsecase cut = new CreateMoneyDistUsecase(repoMock, new EqualMoneySplitAlgorithm());
    when(repoMock.save(any())).thenReturn(true);

    // Act
    CreateMoneyDistUsecase.ResponseDTO actual = cut.execute(new CreateMoneyDistUsecase.RequestDTO("ownerId", "roomId", 1000, 3));

    // Assert
    assertThat(actual, hasProperty("token"));
  }

  @Test
  @DisplayName("make Money distribution with repository")
  void tc6() {
    // Arrange
    CreateMoneyDistUsecase cut = new CreateMoneyDistUsecase(repoMock, new EqualMoneySplitAlgorithm());
    CreateMoneyDistUsecase spy = spy(cut);
    when(repoMock.save(any())).thenReturn(true);

    // Act
    CreateMoneyDistUsecase.ResponseDTO actual = spy.execute(new CreateMoneyDistUsecase.RequestDTO("ownerId", "roomId", 1000, 3));

    // Assert
    verify(repoMock).save(any());
    assertThat(actual, hasProperty("token"));
  }

  @Test
  @DisplayName("When repository.save() failed 3 times then call onError")
  void tc7() {
    // Arrange
    CreateMoneyDistUsecase cut = new CreateMoneyDistUsecase(repoMock, new EqualMoneySplitAlgorithm());
    CreateMoneyDistUsecase spy = spy(cut);
    when(repoMock.save(any())).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);

    // Act
    spy.execute(new CreateMoneyDistUsecase.RequestDTO("ownerId", "roomId", 1000, 3));

    // Assert
    verify(repoMock, times(3)).save(any());
  }
}
