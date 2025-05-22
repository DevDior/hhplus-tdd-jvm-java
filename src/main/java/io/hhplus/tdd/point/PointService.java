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

    public UserPoint charge(long id, long amount) {
        UserPoint currentUserPoint = userPointTable.selectById(id);
        long updatedAmount = currentUserPoint.point() + amount;

        return userPointTable.insertOrUpdate(id, updatedAmount);
    }
}
