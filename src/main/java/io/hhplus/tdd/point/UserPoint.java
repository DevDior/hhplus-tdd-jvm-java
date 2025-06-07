package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    private static final long MAX_POINT = 1000000L;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint charge(long amount) {
        long total = point + amount;
        if (total > MAX_POINT) {
            throw new IllegalStateException("최대 포인트 잔고를 초과했습니다.");
        }

        return new UserPoint(id, total, System.currentTimeMillis());
    }

    public UserPoint use(long amount) {
        long total = point - amount;
        if (total < 0L) {
            throw new IllegalStateException("포인트 잔고가 부족합니다.");
        }

        return new UserPoint(id, total, System.currentTimeMillis());
    }
}
