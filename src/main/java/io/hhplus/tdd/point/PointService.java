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
        UserPoint updatedUserPoint = currentUserPoint.charge(amount);
        return userPointTable.insertOrUpdate(id, updatedUserPoint.point());
    }
}
