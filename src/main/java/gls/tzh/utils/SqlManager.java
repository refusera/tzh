package gls.tzh.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlManager {

    public static Connection getConn() {

        String driver = "com.mysql.jdbc.Driver";
        String url = Constants.SQL_URL;
        String username = "geluosi";
        String password = "sbbGLS_4dmin777";
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
