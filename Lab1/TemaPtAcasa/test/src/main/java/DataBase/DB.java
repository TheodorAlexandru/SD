package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {

    private static final String SAVE_URL = "jdbc:sqlite:/home/theo/Documents/SD/Lab1/TemaPtAcasa/test/studenti.db";

    public static String getURL(){
        return SAVE_URL;
    }

    public static void initializeTable() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(SAVE_URL);
            stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS STUDENTI (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nume TEXT NOT NULL," +
                    "prenume TEXT NOT NULL," +
                    "varsta INTEGER NOT NULL," +
                    "an_nastere INTEGER" +
                    ")";
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("Error initializing database: " + e.getMessage());
        } finally {
            // Închidem resursele
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
