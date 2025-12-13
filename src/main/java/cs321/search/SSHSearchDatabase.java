package cs321.search;

import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * The driver class for searching a Database of a B-Tree.
 */
public class SSHSearchDatabase
{
	
    public static void main(String[] args) throws Exception
    {
        String db = null;
        String type = null;
        int topFrequency = 0;

        if (args.length < 3) {
            System.out.println("Usage: java -jar build/libs/SSHSearchDatabase.jar --database=<database.db> --type=<table-type> --top-frequency=<top frequency>\n");
            return;
        }

        for (String arg : args) {
            if (arg.startsWith("--database=")) {
                db = ParseArgumentUtils.parseDatabaseArgument(arg);
            } else if (arg.startsWith("--type=")) {
                type = ParseArgumentUtils.parseTypeArgument(arg);
            } else if (arg.startsWith("--top-frequency=")) {
                topFrequency = ParseArgumentUtils.parseTopFrequencyArgument(arg);
            } else {
                throw new ParseArgumentException("Unknown argument: " + arg);
            }
        }

        if (db == null || type == null) {
            throw new ParseArgumentException("Missing required arguments.");
        }

        if ("test".equals(type)) {
            createAndSeedTestDatabase(db);
            System.out.println("Created and seeded database '" + db + "' with table 'acceptedip' (25 rows).");
            return;
        }

        if (topFrequency <= 0) {
            throw new ParseArgumentException("Missing required arguments.");
        }

        String table = type;
        if ("accepted-ip".equals(type) || "accepted_ip".equals(type)) {
            table = "acceptedip";
        }


        String orderTable = "SELECT Key, Frequency FROM " + table + " ORDER BY Frequency DESC LIMIT " + topFrequency;
        String url = "jdbc:sqlite:" + db;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(orderTable);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Top " + topFrequency + " SSH Keys by Frequency in " + table + " table:");
            while (rs.next()) {
                String key = rs.getString("Key");
                int frequency = rs.getInt("Frequency");
                System.out.println("Key: " + key + ", Frequency: " + frequency);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createAndSeedTestDatabase(String dbFile) throws Exception {
        String url = "jdbc:sqlite:" + dbFile;

        String createTableSql =
                "CREATE TABLE IF NOT EXISTS acceptedip (" +
                "Key TEXT PRIMARY KEY, " +
                "Frequency INTEGER NOT NULL" +
                ");";

        String insertSql =
                "INSERT OR REPLACE INTO acceptedip (Key, Frequency) VALUES (?, ?);";

        Object[][] rows = new Object[][]{
                {"Accepted-111.222.107.90", 25},
                {"Accepted-112.96.173.55", 3},
                {"Accepted-112.96.33.40", 3},
                {"Accepted-113.116.236.34", 6},
                {"Accepted-113.118.187.34", 2},
                {"Accepted-113.99.127.215", 2},
                {"Accepted-119.137.60.156", 1},
                {"Accepted-119.137.62.123", 9},
                {"Accepted-119.137.62.142", 1},
                {"Accepted-119.137.63.195", 14},
                {"Accepted-123.255.103.142", 5},
                {"Accepted-123.255.103.215", 5},
                {"Accepted-137.189.204.138", 1},
                {"Accepted-137.189.204.155", 1},
                {"Accepted-137.189.204.220", 1},
                {"Accepted-137.189.204.236", 1},
                {"Accepted-137.189.204.246", 1},
                {"Accepted-137.189.204.253", 3},
                {"Accepted-137.189.205.44", 2},
                {"Accepted-137.189.206.152", 1},
                {"Accepted-137.189.206.243", 1},
                {"Accepted-137.189.207.18", 1},
                {"Accepted-137.189.207.28", 1},
                {"Accepted-137.189.240.159", 1},
                {"Accepted-137.189.241.19", 2}
        };

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);

            try (PreparedStatement createStmt = conn.prepareStatement(createTableSql)) {
                createStmt.executeUpdate();
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Object[] r : rows) {
                    insertStmt.setString(1, (String) r[0]);
                    insertStmt.setInt(2, (Integer) r[1]);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            conn.commit();
        }
    }
}
