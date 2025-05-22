package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    @DisplayName("충전 시 최대 포인트 잔고 초과하면 예외 발생")
    void chargeOverMaxPointShouldThrowException() {
        // given
        long id = 1L;
        long currentPoint = 900000L;
        long chargePoint = 200000L;

        userPointTable.insertOrUpdate(id, currentPoint);

        // when & then
        assertThatThrownBy(() -> pointService.charge(id, chargePoint))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("최대 포인트 잔고를 초과했습니다.");
    }

    @Test
    @DisplayName("유저 포인트 조회하기")
    void getUserPoint() {
        // given
        long id = 1L;
        long currentPoint = 100000L;

        UserPoint expectedUserPoint = userPointTable.insertOrUpdate(id, currentPoint);

        // when
        UserPoint userPoint = pointService.getUserPointById(id);

        // then
        assertThat(expectedUserPoint.point()).isEqualTo(userPoint.point());
    }
}
