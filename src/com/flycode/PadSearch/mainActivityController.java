package com.flycode.PadSearch;

import com.flycode.PadSearch.Constants.Constants;
import com.flycode.PadSearch.Entities.Tenant;
import com.flycode.PadSearch.PadSql.MySqlHelper;
import com.flycode.PadSearch.PadSql.PadSqlUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
public class mainActivityController extends DbUtil implements Initializable {
    @FXML
    ComboBox<String> comboBox;
    @FXML
    TableView tableView;
    @FXML
    PasswordField password_field;
    @FXML
    TextField login_field;
    @FXML
    private TextField textField1;
    @FXML
    private TextField textField2;
    @FXML
    private TextField textField3;
    @FXML
    private TextField textField4;
    @FXML
    private TextField textField5;
    @FXML
    private TextField textField6;
    @FXML
    private Label info_label;
    @FXML
    private Label label1;
    @FXML
    private Label label2;
    @FXML
    private Label label3;
    @FXML
    private Label label4;
    @FXML
    private Label label5;
    @FXML
    private Label label6;
    @FXML
    private Tab tab_sheets;
    @FXML
    private Button loadButton;
    @FXML
    private Button deleteButton;

    private ResultSet resultSet;
    private ObservableList<String> tableNames = FXCollections.observableArrayList();
    private ObservableList data = FXCollections.observableArrayList();
    private String SelectedTable;
    private Constants constants;
    private MySqlHelper sqlhelp;
    private PadSqlUtil padsql;


    public void onClickLoginButton() {
        info_label.setVisible(true);
        info_label.setText("logging in...");
        info_label.setTextFill(Color.BLACK);
        padsql = new PadSqlUtil(login_field.getText(),password_field.getText());
        if (padsql.CONNECTION_STATUS) {
            tab_sheets.setDisable(false);
            info_label.setText("login Successful");
            info_label.setTextFill(Color.GREEN);
            fillComboBox();
        } else {
            tab_sheets.setDisable(true);
            info_label.setText("Login Not Successful");
            info_label.setTextFill(Color.RED);
        }
    }

    private void fillComboBox() {
        tableNames.addAll("client_test","tenant","owner","building");
        comboBox.setItems(tableNames);
    }

    public void onClickLoadButton() {
        data.clear();
        tableView.getColumns().clear();
        try {
            buildData(SelectedTable);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void onClickAddButton() {
        /*String query = "INSERT INTO " + constants.TABLE_NAME + " values (" + "\""
                + textField1.getText() + "\",\""
                + textField2.getText() + "\",\""
                + textField3.getText() + "\")";
        sqlhelp.doUpdate(query);*/

        Tenant tenant = new Tenant();
        tenant.setFirst(textField1.getText());
        tenant.setSecond(textField2.getText());
        tenant.setSurname(textField3.getText());
        tenant.setTell(67324254/*Integer.valueOf(textField4.getText())*/);
        tenant.setNational_ID(45234253/*Integer.valueOf(textField5.getText())*/);
        tenant.setBio(textField6.getText());
        padsql.addTenant(tenant);
        
        onClickLoadButton();
    }

    public void onClickDeleteButton() {
        Object string = data.get(tableView.getSelectionModel().getSelectedIndex());
        String id = (String) ((ObservableList) string).get(0);

        //sqlhelp.doUpdate("delete from " + constants.TABLE_NAME + " where id=" + id);
        padsql.DeleteTenant(id);
        onClickLoadButton();

    }

    public void onMouseClickOnTable() {
        int index = tableView.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            Object string = data.get(index);
            textField1.setText(String.valueOf(((ObservableList) string).get(0)));
            textField2.setText(String.valueOf(((ObservableList) string).get(1)));
            textField3.setText(String.valueOf(((ObservableList) string).get(2)));
            textField4.setText(String.valueOf(((ObservableList) string).get(3)));
            textField5.setText(String.valueOf(((ObservableList) string).get(4)));
            textField6.setText(String.valueOf(((ObservableList) string).get(5)));
        } else {
            textField1.setText("");
            textField2.setText("");
            textField3.setText("");
            textField4.setText("");
            textField5.setText("");
            textField6.setText("");
        }
    }

    //TODO: Make onClickUpdateButton() use the tenant class to parse data for updating.
    public void onClickUpdateButton() {
        //TODO: find a way to detect selection of the records correctly.
        Object string = data.get(tableView.getSelectionModel().getSelectedIndex());
        String First = (String) ((ObservableList) string).get(0);
        try {
            String query = "UPDATE client_test SET " // + constants.TABLE_NAME + " SET "
                    + label1.getText() + "=" + textField1.getText() + ","
                    + label2.getText() + "=" + textField2.getText() + ","
                    + label3.getText() + "=" + textField3.getText() + ","
                    + label4.getText() + "=" + textField4.getText() + ","
                    + label5.getText() + "=" + textField5.getText() + ","
                    + label6.getText() + "=" + textField6.getText()
                    + " WHERE First=" + First+";";
            sqlhelp.doUpdate(query);
            onClickLoadButton();
        }catch (Exception ex){
            System.out.println("Error in Updating row");
        }
        
        
    }

    public void onSelectComboBoxTableName() {
        SelectedTable = comboBox.getValue();
        loadButton.setDisable(false);
        deleteButton.setDisable(false);
        onClickLoadButton();
    }

    //TODO: fix buildData() to inflate TableView correctly
    private void buildData(String table) throws SQLException {
        try{
        resultSet = padsql.selectTable(table); //SELECT ALL THE COLUMNS
        } catch (Exception e){
            System.out.println("Cannot retrieve data from database");
            e.printStackTrace();
        }

        /**********************************
         * TABLE COLUMN ADDED DYNAMICALLY *
         **********************************/
        for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
            //We are using non property style for making dynamic table
            final int j = i;
            TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });

            tableView.getColumns().addAll(col);

        }
        //labels to display appropriate names for the columns
        label1.setText(resultSet.getMetaData().getColumnName(1));
        label2.setText(resultSet.getMetaData().getColumnName(2));
        label3.setText(resultSet.getMetaData().getColumnName(3));
        label4.setText(resultSet.getMetaData().getColumnName(4));
        label5.setText(resultSet.getMetaData().getColumnName(5));
        label6.setText(resultSet.getMetaData().getColumnName(6));

        /********************************
         * Data added to ObservableList *
         ********************************/
        while (resultSet.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(resultSet.getString(i));
            }
            data.add(row);

        }

        //FINALLY ADDED TO TableView
        tableView.setItems(data);

        if (conn != null)
            conn.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


}
