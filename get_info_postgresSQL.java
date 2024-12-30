import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresResourceUtilizationTest {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "******";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // 获取数据库的资源利用率信息
            getDatabaseStatistics(conn);
            getBackgroundWriterStats(conn);
            getTransactionStatistics(conn);
        } catch (SQLException e) {
            System.out.println("Failed to get resource utilization information.");
            e.printStackTrace();
        }
    }

    private static void getDatabaseStatistics(Connection conn) throws SQLException {
        String query = "SELECT datname, numbackends, xact_commit, xact_rollback, blks_read, blks_hit, tup_returned, tup_fetched FROM pg_stat_database";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Database Name: " + rs.getString("datname"));
                System.out.println("Number of Backends: " + rs.getInt("numbackends"));
                System.out.println("Transactions Committed: " + rs.getLong("xact_commit"));
                System.out.println("Transactions Rolled Back: " + rs.getLong("xact_rollback"));
                System.out.println("Blocks Read: " + rs.getLong("blks_read"));
                System.out.println("Blocks Hit: " + rs.getLong("blks_hit"));
                System.out.println("Tuples Returned: " + rs.getLong("tup_returned"));
                System.out.println("Tuples Fetched: " + rs.getLong("tup_fetched"));
                System.out.println("-------------------------------");
            }
        }
    }

    private static void getBackgroundWriterStats(Connection conn) throws SQLException {
        String query = "SELECT checkpoints_timed, checkpoints_req, buffers_checkpoint, buffers_clean, maxwritten_clean, buffers_backend, buffers_backend_fsync, buffers_alloc FROM pg_stat_bgwriter";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("Checkpoints Timed: " + rs.getLong("checkpoints_timed"));
                System.out.println("Checkpoints Requested: " + rs.getLong("checkpoints_req"));
                System.out.println("Buffers Checkpoint: " + rs.getLong("buffers_checkpoint"));
                System.out.println("Buffers Clean: " + rs.getLong("buffers_clean"));
                System.out.println("Maxwritten Clean: " + rs.getLong("maxwritten_clean"));
                System.out.println("Buffers Backend: " + rs.getLong("buffers_backend"));
                System.out.println("Buffers Backend Fsync: " + rs.getLong("buffers_backend_fsync"));
                System.out.println("Buffers Allocated: " + rs.getLong("buffers_alloc"));
                System.out.println("-------------------------------");
            }
        }
    }

    private static void getTransactionStatistics(Connection conn) throws SQLException {
        String query = "SELECT datname, xact_commit, xact_rollback FROM pg_stat_database";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Database Name: " + rs.getString("datname"));
                System.out.println("Transactions Committed: " + rs.getLong("xact_commit"));
                System.out.println("Transactions Rolled Back: " + rs.getLong("xact_rollback"));
                System.out.println("-------------------------------");
            }
        }
    }
}
