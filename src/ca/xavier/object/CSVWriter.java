package ca.xavier.object;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

//Write and download CSV file to the project folder
public class CSVWriter {
    public void writeCSV(ResultSet set) throws IOException, SQLException {
        FileWriter fw = new FileWriter("./data.csv");
        ResultSetMetaData metaData = set.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            fw.append(metaData.getColumnName(i));
            if (i < columnCount) {
                fw.append(",");
            }
        }
        fw.append("\n");
        while (set.next()) {
            for (int i = 1; i <= columnCount; i++) {
                fw.append(set.getString(i));
                if (i < columnCount) {
                    fw.append(",");
                }
            }
            fw.append("\n");
        }
        fw.close();
    }
}
