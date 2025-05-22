package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PointServiceTest {

    @Test
    @DisplayName("유저 포인트 충전하기")
    void userPointCharge() {
        // given
        long id = 1L;
        long amount = 1000L;
        UserPointTable userPointTable = new UserPointTable();
        UserPoint beforeUserPoint = userPointTable.insertOrUpdate(id, 500L);

        // when
        UserPoint userPoint = pointService.charge(id, amount);

        //then
        assertThat(userPoint.point()).isEqualTo(1500L);
    }
}
