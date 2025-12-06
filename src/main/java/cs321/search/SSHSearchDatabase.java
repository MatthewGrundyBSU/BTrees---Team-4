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

        if (db == null || type == null || topFrequency <= 0) {
            throw new ParseArgumentException("Missing required arguments.");
        }

        String orderTable = "SELECT Key, Frequency FROM " + type + " ORDER BY Frequency DESC LIMIT " + topFrequency;
        String url = "jdbc:sqlite:" + db;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(orderTable);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Top " + topFrequency + " SSH Keys by Frequency in " + type + " table:");
            while (rs.next()) {
                String key = rs.getString("Key");
                int frequency = rs.getInt("Frequency");
                System.out.println("Key: " + key + ", Frequency: " + frequency);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
