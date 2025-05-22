package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PointChargeConcurrencyIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserPointTable userPointTable;

    @BeforeEach
    void setup() {
        userPointTable.insertOrUpdate(1L, 0L);
    }

    @Test
    @DisplayName("100개 포인트 충전 동시 요청에 대한 정확한 포인트 누적")
    void concurrentChargeTest() throws Exception {
        int threadCount = 100;
        long amount = 100L;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    mockMvc.perform(patch("/point/1/charge")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(String.valueOf(amount)))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        UserPoint userPoint = userPointTable.selectById(1L);
        assertThat(userPoint.point()).isEqualTo(threadCount * amount);
    }
}
