package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PointServiceTest {

    private UserPointTable userPointTable;
    private PointService pointService;

    @BeforeEach
    void setup() {
        userPointTable = new UserPointTable();
        pointService = new PointService(null, userPointTable);
    }

    @Test
    @DisplayName("유저 포인트 충전하기")
    void userPointCharge() {
        // given
        long id = 1L;
        long amount = 1000L;
        userPointTable.insertOrUpdate(id, 500L);

        // when
        UserPoint updatedUserPoint = pointService.charge(id, amount);

        //then
        assertThat(updatedUserPoint.point()).isEqualTo(1500L);
    }
}
