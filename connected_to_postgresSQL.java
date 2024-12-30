import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseConcurrencyTest {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "******";
    private static final String QUERY = "SELECT 1"; // 一个简单的查询
    private static final int CONCURRENCY_LEVEL = 100; // 并发线程数
    private static final int QUERIES_PER_THREAD = 1000; // 每个线程执行的查询次数

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENCY_LEVEL);
        CountDownLatch latch = new CountDownLatch(CONCURRENCY_LEVEL);
        long totalQueries = CONCURRENCY_LEVEL * QUERIES_PER_THREAD;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < CONCURRENCY_LEVEL; i++) {
            executor.submit(() -> {
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                    for (int j = 0; j < QUERIES_PER_THREAD; j++) {
                        try (PreparedStatement stmt = conn.prepareStatement(QUERY);
                             ResultSet rs = stmt.executeQuery()) {
                            // 处理结果集（这里只是简单地执行查询，不处理结果）
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(); // 等待所有线程完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time: " + totalTime + " ms");
        System.out.println("Total queries executed: " + totalQueries);
        System.out.println("Throughput: " + (totalQueries * 1000.0 / totalTime) + " queries per second");

        executor.shutdown();
    }
}
