package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PointServiceTest {

    private PointHistoryTable pointHistoryTable;
    private UserPointTable userPointTable;
    private PointService pointService;

    @BeforeEach
    void setup() {
        pointHistoryTable = mock(PointHistoryTable.class);
        userPointTable = new UserPointTable();
        pointService = new PointService(pointHistoryTable, userPointTable);
    }

    @Test
    @DisplayName("유저 포인트 충전")
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
    @DisplayName("유저 포인트 사용")
    void useUserPoint() {
        // given
        long id = 1L;
        long currentPoint = 100000L;
        long usePoint = 50000L;
        userPointTable.insertOrUpdate(id, currentPoint);

        // when
        UserPoint userPoint = pointService.useUserPoint(id, usePoint);

        // then
        assertThat(userPoint.point()).isEqualTo(currentPoint - usePoint);
    }

    @Test
    @DisplayName("사용 시 포인트 잔고 부족하면 예외 발생")
    void useOverPointShouldThrowException() {
        // given
        long id = 1L;
        long currentPoint = 10000L;
        long usePoint = 50000L;
        userPointTable.insertOrUpdate(id, currentPoint);

        // when & then
        assertThatThrownBy(() -> pointService.useUserPoint(id, usePoint))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("포인트 잔고가 부족합니다.");
    }

    @Test
    @DisplayName("유저 포인트 조회")
    void getUserPoint() {
        // given
        long id = 1L;
        long point = 100000L;

        userPointTable.insertOrUpdate(id, point);

        // when
        UserPoint userPoint = pointService.getUserPointById(id);

        // then
        assertThat(userPoint.point()).isEqualTo(point);
    }

    @Test
    @DisplayName("유저 포인트 충전/사용 내역 조회")
    void getUserPointHistories() {
        // given
        long userId = 1L;
        long now = System.currentTimeMillis();
        List<PointHistory> fakeHistories = List.of(
                new PointHistory(1, userId, 1000L, TransactionType.CHARGE, now),
                new PointHistory(2, userId, 500L, TransactionType.USE, now)
        );

        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(fakeHistories);

        // when
        List<PointHistory> pointHistories = pointService.getUserPointHistories(userId);

        // then
        assertThat(pointHistories).hasSize(2);
        assertThat(pointHistories.get(0).type()).isEqualTo(TransactionType.CHARGE);
        assertThat(pointHistories.get(1).type()).isEqualTo(TransactionType.USE);
    }
}
