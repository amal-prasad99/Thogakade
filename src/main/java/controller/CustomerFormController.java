package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import entity.Customer;
import entity.tm.CustomerTm;
import util.CrudUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class CustomerFormController implements Initializable {

    @FXML
    private Label lblCustomerID;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnSave;

    @FXML
    private TreeTableColumn colAddress;

    @FXML
    private TreeTableColumn colCustomerId;

    @FXML
    private TreeTableColumn colName;

    @FXML
    private TreeTableColumn colSalary;

    @FXML
    private TreeTableColumn colOption;

    @FXML
    private AnchorPane customerPane;

    @FXML
    private JFXTreeTableView<CustomerTm> tblCustomerList;

    @FXML
    private JFXButton tnUpdate;

    @FXML
    private JFXButton tnUpdate1;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtCustomerSearch;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtSalary;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCustomerId.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        colName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new TreeItemPropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new TreeItemPropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btnOption"));
        generateID();
        loadTable();

        tblCustomerList.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) ->{
            if (newValue != null) {
                int selectedIndex = newValue.intValue(); // Convert the Number to int
                TreeItem<CustomerTm> selectedTreeItem = tblCustomerList.getTreeItem(selectedIndex);
                if (selectedTreeItem != null) {
                    setData(selectedTreeItem);
                }
            }
            });

        txtCustomerSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                tblCustomerList.setPredicate(new Predicate<TreeItem<CustomerTm>>() {
                    @Override
                    public boolean test(TreeItem<CustomerTm> customerTmTreeItem) {
                        boolean flag = customerTmTreeItem.getValue().getId().contains(newValue) ||
                                customerTmTreeItem.getValue().getName().contains(newValue);
                        return flag;
                    }
                });
            }
        });

    }

    private void setData(TreeItem<CustomerTm> value) {
        lblCustomerID.setText(value.getValue().getId());
        txtName.setText(value.getValue().getName());
        txtAddress.setText(value.getValue().getAddress());
        txtSalary.setText(String.valueOf(value.getValue().getSalary()));

    }

    private void loadTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();

        try {
            List<Customer> list = new ArrayList<>();

            ResultSet resultSet = CrudUtil.execute(
                    "SELECT * FROM customer"
            );

            while (resultSet.next()){
                list.add(new Customer(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getDouble(4)
                ));
            }

            for(Customer customer: list){
                JFXButton btn = new JFXButton("Delete");
                BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(227, 92, 92), null, null);
                Background background = new Background(backgroundFill);
                btn.setBackground(background);

                btn.setOnAction(actionEvent -> {
                    deleteCustomer(customer);
                });

                tmList.add(new CustomerTm(
                        customer.getId(),
                        customer.getName(),
                        customer.getAddress(),
                        customer.getSalary(),
                        btn
                ));
            }

            TreeItem<CustomerTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblCustomerList.setRoot(treeItem);
            tblCustomerList.setShowRoot(false);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateID() {
        try {
            ResultSet resultSet = CrudUtil.execute(
                    "SELECT id FROM customer ORDER BY id DESC LIMIT 1"
            );

            if(resultSet.next()){
                int cusID = Integer.parseInt(resultSet.getString(1).split("[C]")[1]);
                cusID++;
                lblCustomerID.setText(String.format("C%03d",cusID));
            }else {
                lblCustomerID.setText("C001");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void btnBackOnAction(ActionEvent event) {
        Stage stage = (Stage) customerPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBoard.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.show();
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearField();
    }

    private void clearField() {
        generateID();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        CustomerTm selectedCustomer = tblCustomerList.getSelectionModel().getSelectedItem().getValue();

        if(selectedCustomer != null){
            deleteCustomer(new Customer(
                    selectedCustomer.getId(),
                    selectedCustomer.getName(),
                    selectedCustomer.getAddress(),
                    selectedCustomer.getSalary()
            ));
        }

    }

    private void deleteCustomer(Customer customer) {
        try {
            Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete " + customer.getId() + " customer? ", ButtonType.YES, ButtonType.NO).showAndWait();
            if(buttonType.get() == ButtonType.YES){

                Boolean isDelete = CrudUtil.execute(
                        "DELETE FROM customer WHERE id=?",
                        customer.getId()
                );

                if(isDelete){
                    new Alert(Alert.AlertType.INFORMATION,"Customer Deleted...!").show();
                    clearField();
                    loadTable();
                }else{
                    new Alert(Alert.AlertType.INFORMATION,"Customer not Deleted...!").show();
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {

        Customer customer = new Customer(
                lblCustomerID.getText(),
                txtName.getText(),
                txtAddress.getText(),
                Double.parseDouble(txtSalary.getText())
        );

        try {

            Boolean isSaved =CrudUtil.execute(
                    "INSERT INTO customer VALUES(?,?,?,?)",
                    customer.getId(),
                    customer.getName(),
                    customer.getAddress(),
                    customer.getSalary()
                    );

            if(isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Customer Saved...!").show();
                clearField();
                generateID();
                loadTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Customer not Added...!").show();
            }


        }catch (SQLException | ClassCastException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }


    @FXML
    void btnSearchOnAction(ActionEvent event) {

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Customer customer = new Customer(
                lblCustomerID.getText(),
                txtName.getText(),
                txtAddress.getText(),
                Double.parseDouble(txtSalary.getText())
        );

        try {

            Boolean isUpdate =CrudUtil.execute(
                    "UPDATE customer SET name=?, address=?, salary=? WHERE id=?",
                    customer.getName(),
                    customer.getAddress(),
                    customer.getSalary(),
                    customer.getId()
            );

            if(isUpdate){
                new Alert(Alert.AlertType.INFORMATION,"Customer Updated...!").show();
                clearField();
                loadTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Customer not Updated...!").show();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
