package sample;
import java.sql.*;

public class SQLiteJDBC {

    public SQLiteJDBC(){
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = getConnection();
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String createTable =
                    "CREATE TABLE IF NOT EXISTS city" +
                    " (id  INTEGER    NOT NULL UNIQUE, " +
                    " name  TEXT    NOT NULL)";
            stmt.executeUpdate(createTable);
            stmt.close();
            c.close();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );

        }
        System.out.println("Table created successfully");
    }

    public Connection getConnection() {
        String url = "jdbc:sqlite:cities.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public long getIdFromName(String name) {
        try {
            PreparedStatement ps;
            Connection con = this.getConnection();
            String sqlSelect = "SELECT id FROM city WHERE name LIKE ?";
            ps = con.prepareStatement(sqlSelect);
            ps.setString(1,name);
            ResultSet rs = ps.executeQuery();
            while ( rs.next() )
            {
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            System.err.println( e.getMessage() );
        }

        return 0;
    }
}
