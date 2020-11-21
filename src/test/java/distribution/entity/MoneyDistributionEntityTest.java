package distribution.entity;
import org.junit.Test;

import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MoneyDistributionEntityTest {
  @Test
  public void newDistribution() {
    MoneyDistribution cut = MoneyDistribution.builder().build();
    cut.getDistributeInfoTable();
    assertThat(cut, hasProperty("ownerId"));
    assertThat(cut, hasProperty("roomId"));
    assertThat(cut, hasProperty("token"));
    assertThat(cut, hasProperty("createEpoch"));
    assertThat(cut, hasProperty("totalAmount"));
    assertThat(cut, hasProperty("guestCnt"));
    assertThat(cut, hasProperty("distributeInfoTable"));
    assertThat(cut, hasProperty("distributeRemainingList"));
  }
}
