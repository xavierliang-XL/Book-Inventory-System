package ca.xavier.jdbc;

import java.sql.*;

//Establish JDBC Connection with DataBase
public class JDBC {
    public static Connection jdbcConnection;
    public static Statement statement;

    static {
        try {
            jdbcConnection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Books", "root", "12345678");
            statement = jdbcConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
