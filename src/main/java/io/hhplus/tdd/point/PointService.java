package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class PointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    private static final long MAX_POINT = 1000000L;

    private final Map<Long, Object> locks = new ConcurrentHashMap<>();

    private Object getUserLock(long userId) {
        return locks.computeIfAbsent(userId, id -> new Object());
    }

    public UserPoint charge(long id, long amount) {
        synchronized (getUserLock(id)) {
            UserPoint currentUserPoint = userPointTable.selectById(id);
            UserPoint updatedUserPoint = currentUserPoint.charge(amount);

            pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
            return userPointTable.insertOrUpdate(id, updatedUserPoint.point());
        }
    }

    public UserPoint useUserPoint(long id, long amount) {
        UserPoint currentUserPoint = userPointTable.selectById(id);
        UserPoint updatedUserPoint = currentUserPoint.use(amount);

        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(id, updatedUserPoint.point());
    }

    public UserPoint getUserPointById(long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getUserPointHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
