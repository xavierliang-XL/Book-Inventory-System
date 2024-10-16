package ca.xavier.UI;
import ca.xavier.jdbc.JDBC;
import ca.xavier.object.CSVWriter;
import ca.xavier.object.Library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

//Simple UI Design
public class GUI{
    //external objects
    Library library;
    CSVWriter writer;

    //basic setup
    JFrame jframe;
    JScrollPane jscrollpane;
    JTable jTable;
    String[] col;
    Object[][] data;

    //panels setup
    JPanel controlPanel;
    JPanel buttonPanel;
    JPanel inputPanel1;
    JPanel inputPanel2;

    //text fields setup
    JTextField entryID;
    JTextField title;
    JTextField author;
    JTextField genre;
    JTextField publicationDate;
    JTextField isbn;

    //labels setup
    JLabel entryLabel;
    JLabel titleLabel;
    JLabel authorLabel;
    JLabel genreLabel;
    JLabel publicationDateLabel;
    JLabel isbnLabel;
    JLabel errorLabel;

    //buttons setup
    JButton add;
    JButton remove;
    JButton search;
    JButton export;

    public GUI() throws SQLException {
        //1.init data for the table
        library = new Library();
        writer = new CSVWriter();
        col = new String[]{"Entry_ID", "Title", "Author", "Genre", "Publication_Date", "ISBN"};
        data = getData(JDBC.statement.executeQuery("select * from Information"));

        //2.create gui
        jframe = new JFrame();
        jframe.setTitle("BookAdmin");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setMinimumSize(new Dimension(800, 600));

        //3.create table and scroll pane
        jTable = new JTable();
        jTable.setModel(new DefaultTableModel(data, col));
        jscrollpane = new JScrollPane(jTable);
        jscrollpane.setPreferredSize(new Dimension(800, 400));
        jscrollpane.setViewportView(jTable);

        //4.create control panel
        controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(800, 150));

        //5.create input panel which is a sub panel of input panel
        inputPanel1 = new JPanel();
        inputPanel2 = new JPanel();
        entryID = new JTextField(10);
        title = new JTextField(10);
        author = new JTextField(10);
        genre = new JTextField(10);
        publicationDate = new JTextField(10);
        isbn = new JTextField(10);

        //5.5.add labels for input panel
        entryLabel = new JLabel("Entry ID (Unique):");
        titleLabel = new JLabel("Title:");
        authorLabel = new JLabel("Author:");
        genreLabel = new JLabel("Genre:");
        publicationDateLabel = new JLabel("Publication Date (yyyy-mm-dd / yyyymmdd):");
        isbnLabel = new JLabel("ISBN:");
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);

        //6.create button panel which is a sub panel of control panel
        buttonPanel = new JPanel();
        remove = new JButton("Remove");
        add = new JButton("Add");
        search = new JButton("Search");
        export = new JButton("Export");

        //remove action:
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                HashMap<String, String> map = getText();

                if(!removeCheck(map)) {
                    errorLabel.setText("Error: At least one field must be entered");
                    errorLabel.setVisible(true);
                    return;
                }


                try {
                    library.removeBook(map);
                    Update();
                } catch (SQLException e) {
                    errorLabel.setVisible(false);
                    errorLabel.setText("Error: Did not enter fields based on the requirements!");
                    errorLabel.setVisible(true);
                    throw new RuntimeException(e);
                }
                errorLabel.setVisible(false);
            }
        });

        //add action:
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                HashMap<String, String> map = getText();
                if(!addCheck(map)) {
                    errorLabel.setText("Error: all fields must be entered!");
                    errorLabel.setVisible(true);
                    return;
                }
                try {
                    library.addBook(map);
                    Update();
                } catch (SQLException e) {
                    errorLabel.setVisible(false);
                    errorLabel.setText("Error: Did not enter fields based on the requirements!");
                    errorLabel.setVisible(true);
                    throw new RuntimeException(e);
                }
                errorLabel.setVisible(false);
            }
        });

        //search action:
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                HashMap<String, String> map = getText();
                try {
                    data = getData(library.searchBook(map));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                jTable.setModel(new DefaultTableModel(data, col));
                jframe.revalidate();
                jframe.repaint();
            }
        });

        //export action:
        export.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    writer.writeCSV(JDBC.statement.executeQuery("select * from Information"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        buttonPanel.add(remove);
        buttonPanel.add(add);
        buttonPanel.add(search);
        buttonPanel.add(export);

        //7.put all the components together
        inputPanel1.add(entryLabel);
        inputPanel1.add(entryID);
        inputPanel1.add(titleLabel);
        inputPanel1.add(title);
        inputPanel1.add(authorLabel);
        inputPanel1.add(author);
        inputPanel1.add(errorLabel);
        inputPanel2.add(genreLabel);
        inputPanel2.add(genre);
        inputPanel2.add(publicationDateLabel);
        inputPanel2.add(publicationDate);
        inputPanel2.add(isbnLabel);
        inputPanel2.add(isbn);

        controlPanel.add(inputPanel1, BorderLayout.NORTH);
        controlPanel.add(inputPanel2, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        jframe.add(jscrollpane, BorderLayout.CENTER);
        jframe.add(controlPanel, BorderLayout.SOUTH);
        jframe.pack();
        jframe.setVisible(true);

    }

    //convert ResultSet to Object[][]
    public Object[][] getData(ResultSet set) throws SQLException {
        List<List<Object>> raw = new ArrayList<>();
        while(set.next()) {
            List<Object> list = new ArrayList<>();
            list.add(set.getInt(1));
            list.add(set.getString(2));
            list.add(set.getString(3));
            list.add(set.getString(4));
            list.add(set.getString(5));
            list.add(set.getString(6));
            raw.add(list);
        }

        Object[][] data = new Object[raw.size()][col.length];
        for(int i = 0; i < raw.size(); i++) {
            for(int j = 0; j < col.length; j++) {
                data[i][j] = raw.get(i).get(j);
            }
        }

        return data;
    }

    //Get User Input
    public LinkedHashMap<String, String> getText() {
        String Entry_ID = entryID.getText();
        String Title = title.getText();
        String Author = author.getText();
        String Genre = genre.getText();
        String PublicationDate = publicationDate.getText();
        String ISBN = isbn.getText();

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("Entry_ID", Entry_ID);
        map.put("Title", Title);
        map.put("Author", Author);
        map.put("Genre", Genre);
        map.put("Publication_Date", PublicationDate);
        map.put("ISBN", ISBN);
        return map;
    }

    //Check if removable
    public boolean removeCheck(HashMap<String, String> map) {
        for(String items: map.values()) {
            if(!items.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    //Check if addable
    public boolean addCheck(HashMap<String, String> map) {
        for(String items: map.values()) {
            if(items.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    //Update page
    public void Update() throws SQLException {
        data = getData(JDBC.statement.executeQuery("select * from Information"));
        jTable.setModel(new DefaultTableModel(data, col));
        jframe.revalidate();
        jframe.repaint();
    }
}
