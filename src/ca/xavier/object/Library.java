package ca.xavier.object;

import ca.xavier.jdbc.JDBC;

import java.sql.*;
import java.util.HashMap;
import java.util.Set;

public class Library {
    Statement statement = JDBC.statement;
    public Library(){}

    //adds row to database based on given input
    public void addBook(HashMap<String, String> map) throws SQLException {
        String sql = "insert into Information values(?,?,?,?,?,?);";
        PreparedStatement st = statement.getConnection().prepareStatement(sql);
        int i = 1;
        for(String key : map.keySet()){
            st.setString(i++, map.get(key));
        }
        st.executeUpdate();
    }

    //removes row(s) from database based on given input
    public void removeBook(HashMap<String, String> map)
            throws SQLException {
        String sql = "delete from Information where ";
        StringBuilder sb = new StringBuilder(sql);
        Set<String> keys = map.keySet();
        boolean first = false;
        for (String key : keys) {
            if(!map.get(key).isEmpty()) {
                if(first) sb.append(" and ");
                else first = true;

                if(key.equals("Entry_ID")) sb.append(key).append(" = ").append(map.get(key));
                else sb.append(key).append(" = '").append(map.get(key)).append("'");

            }
        }
        sb.append(";");
        statement.executeUpdate(sb.toString());
    }

    //searches row(s) based on user input and returns the result
    public ResultSet searchBook(HashMap<String, String> map) throws SQLException {
        String sql = "select * from Information";
        StringBuilder sb = new StringBuilder(sql);
        boolean first = false;
        for(String key : map.keySet()) {
            if(!map.get(key).isEmpty()) {
                if(!first) {
                    sb.append(" where ");
                    first = true;
                } else {
                    sb.append(" and ");
                }
                if(key.equals("Entry_ID")) sb.append(key).append(" = ").append(map.get(key));
                else sb.append(key).append(" = '").append(map.get(key)).append("'");
            }
        }

        ResultSet set = JDBC.statement.executeQuery(sb.toString());
        return set;
    }

}
