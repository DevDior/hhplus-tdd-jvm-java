package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;
    private static final long MAX_POINT = 1000000L;

    public UserPoint charge(long id, long amount) {
        UserPoint currentUserPoint = userPointTable.selectById(id);
        long updatedAmount = currentUserPoint.point() + amount;

        if (updatedAmount > MAX_POINT) {
            throw new IllegalStateException("최대 포인트 잔고를 초과했습니다.");
        }

        return userPointTable.insertOrUpdate(id, updatedAmount);
    }
}
