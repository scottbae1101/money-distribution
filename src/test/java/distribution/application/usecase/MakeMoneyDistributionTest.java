package distribution.application.usecase;

import distribution.application.repository.MoneyDistributionRepository;
import distribution.application.usecase.UseCase.UseCaseCallback;
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

public class MakeMoneyDistributionTest {
  @Mock
  MoneyDistributionRepository repoMock;
  @Mock
  MoneySplitAlgorithm splitAlgoMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("Request DTO")
  void tc1() {
    MakeMoneyDistribution.RequestDTO req = new MakeMoneyDistribution.RequestDTO("ownerId", "roomId", 1000, 2);
    assertThat(req, hasProperty("ownerId"));
    assertThat(req, hasProperty("roomId"));
    assertThat(req, hasProperty("totalAmount"));
    assertThat(req, hasProperty("questCnt"));
  }


  @Test
  @DisplayName("Response DTO")
  void tc2() {
    MakeMoneyDistribution.ResponseDTO res = new MakeMoneyDistribution.ResponseDTO("token");
    assertThat(res, hasProperty("token"));
  }

  @Test
  @DisplayName("make Money distribution")
  void tc3() {
    MakeMoneyDistribution.RequestDTO req = new MakeMoneyDistribution.RequestDTO("ownerId", "roomId", 1000, 2);
    MakeMoneyDistribution cut = new MakeMoneyDistribution(repoMock, splitAlgoMock);
    when(repoMock.save(any())).thenReturn(true);
    cut.setUseCaseCallback(new UseCaseCallback<>() {
      @Override
      public void onSuccess(MakeMoneyDistribution.ResponseDTO responseDTO) {
        assertThat(responseDTO.getToken(), hasLength(3));
      }

      @Override
      public void onError() {
        fail();
      }
    });
    cut.execute(req);
  }

  @Test
  @DisplayName("equal money split test")
  void tc4() {
    EqualMoneySplitAlgorithm cut = new EqualMoneySplitAlgorithm();
    int amount = 1000;
    int peopleCntEven = 2;
    List<Integer> actualEven = cut.splitMoney(amount, peopleCntEven);
    assertThat(actualEven.size(), is(peopleCntEven));
    actualEven.forEach(each -> assertThat(each, is(amount / peopleCntEven)));
    
    int peopleCntOdd = 3;
    List<Integer> actual = cut.splitMoney(amount, peopleCntOdd);
    assertThat(actual.size(), is(peopleCntOdd));
    actual.forEach(each -> assertThat(each, is(amount / peopleCntOdd)));
  }

  @Test
  @DisplayName("make Money distribution with equal distribute algorithm")
  void tc5() {
    MakeMoneyDistribution cut = new MakeMoneyDistribution(repoMock, new EqualMoneySplitAlgorithm());
    when(repoMock.save(any())).thenReturn(true);
    cut.setUseCaseCallback(new UseCaseCallback<MakeMoneyDistribution.ResponseDTO>() {
      @Override
      public void onSuccess(MakeMoneyDistribution.ResponseDTO responseDTO) {
        assertThat(responseDTO, hasProperty("token"));
      }

      @Override
      public void onError() {
        fail();
      }
    });
    cut.execute(new MakeMoneyDistribution.RequestDTO("ownerId", "roomId", 1000, 3));
  }

  @Test
  @DisplayName("make Money distribution with repository")
  void tc6() {
    MakeMoneyDistribution cut = new MakeMoneyDistribution(repoMock, new EqualMoneySplitAlgorithm());
    MakeMoneyDistribution spy = spy(cut);
    when(repoMock.save(any())).thenReturn(true);
    spy.setUseCaseCallback(new UseCaseCallback<MakeMoneyDistribution.ResponseDTO>() {
      @Override
      public void onSuccess(MakeMoneyDistribution.ResponseDTO responseDTO) {
        assertThat(responseDTO, hasProperty("token"));
      }

      @Override
      public void onError() {
        fail();
      }
    });
    spy.execute(new MakeMoneyDistribution.RequestDTO("ownerId", "roomId", 1000, 3));

    verify(repoMock).save(any());
  }

  @Test
  @DisplayName("When repository.save() failed 3 times then call onError")
  void tc7() {
    MakeMoneyDistribution cut = new MakeMoneyDistribution(repoMock, new EqualMoneySplitAlgorithm());
    MakeMoneyDistribution spy = spy(cut);
    when(repoMock.save(any())).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);
    UseCaseCallback mockCallback = mock(UseCaseCallback.class);
    spy.setUseCaseCallback(mockCallback);
    spy.execute(new MakeMoneyDistribution.RequestDTO("ownerId", "roomId", 1000, 3));

    verify(repoMock, times(3)).save(any());
    verify(mockCallback, timeout(1)).onError();
  }
}
