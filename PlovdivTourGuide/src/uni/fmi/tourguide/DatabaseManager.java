package uni.fmi.tourguide;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:tourguide.db";

    public DatabaseManager() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user_feedback (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "location_name TEXT NOT NULL," +
                     "rating INTEGER," +
                     "comment TEXT" +
                     ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFeedback(String locationName, int rating, String comment) {
        String sql = "INSERT INTO user_feedback(location_name, rating, comment) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, locationName);
            pstmt.setInt(2, rating);
            pstmt.setString(3, comment);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printAllFeedback() {
        String sql = "SELECT * FROM user_feedback";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getString("location_name") + " | Rating: " +
                                   rs.getInt("rating") + " | Comment: " +
                                   rs.getString("comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public String getAllFeedbackAsString() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT * FROM user_feedback";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sb.append(rs.getString("location_name"))
                  .append(" | Rating: ")
                  .append(rs.getInt("rating"))
                  .append(" | Comment: ")
                  .append(rs.getString("comment"))
                  .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
