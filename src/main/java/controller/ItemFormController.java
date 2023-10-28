package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.DBConnection;
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
import entity.Item;
import entity.tm.ItemTm;
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

public class ItemFormController implements Initializable {

    @FXML
    private AnchorPane ItemPane;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private TreeTableColumn colCode;

    @FXML
    private TreeTableColumn colDescription;

    @FXML
    private TreeTableColumn colOption;

    @FXML
    private TreeTableColumn colQryOnHand;

    @FXML
    private TreeTableColumn colUnitePrice;

    @FXML
    private Label lblCode;

    @FXML
    private JFXTreeTableView<ItemTm> tblItem;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtQryOnHand;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXTextField txtUnitePrice;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colUnitePrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colQryOnHand.setCellValueFactory(new TreeItemPropertyValueFactory<>("qtyOnHand"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        generateCode();
        loadTable();

        tblItem.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null){
                int selectedIndex = newValue.intValue();
                TreeItem<ItemTm>selectedTreeItem = tblItem.getTreeItem(selectedIndex);
                if(selectedTreeItem != null){
                    setData(selectedTreeItem);
                }
            }
        });
    }

    private void setData(TreeItem<ItemTm> value){
        lblCode.setText(value.getValue().getCode());
        txtDescription.setText(value.getValue().getDescription());
        txtUnitePrice.setText(String.valueOf(value.getValue().getUnitPrice()));
        txtQryOnHand.setText(String.valueOf(value.getValue().getQtyOnHand()));
    }

    private void loadTable(){
        ObservableList<ItemTm> tmList = FXCollections.observableArrayList();

        try {
            List<Item> list = new ArrayList<>();

            ResultSet resultSet = CrudUtil.execute(
                    "SELECT * FROM item"
            );

            while (resultSet.next()){
                list.add(new Item(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getInt(4)
                ));
            }

            for(Item item : list){
                JFXButton btn = new JFXButton("Delete");
                BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(227, 92, 92), null, null);
                Background background = new Background(backgroundFill);
                btn.setBackground(background);

                btn.setOnAction(actionEvent -> {
                    deleteItem(item);
                });


                tmList.add(new ItemTm(
                        item.getCode(),
                        item.getDescription(),
                        item.getUnitPrice(),
                        item.getQtyOnHand(),
                        btn
                ));

            }

            TreeItem<ItemTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblItem.setRoot(treeItem);
            tblItem.setShowRoot(false);



        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void  clearField(){
        generateCode();
        txtDescription.clear();
        txtQryOnHand.clear();
        txtUnitePrice.clear();
    }

    private void generateCode() {
        try {
            ResultSet resultSet = CrudUtil.execute(
                    "SELECT code FROM item ORDER BY code DESC LIMIT 1"
            );

            if(resultSet.next()){
                int itemCode = Integer.parseInt(resultSet.getString(1).split("[P]")[1]);
                itemCode++;
                lblCode.setText(String.format("P%03d",itemCode));
            }else{
                lblCode.setText("P001");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        Stage stage = (Stage) ItemPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBoard.fxml"))));
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

        ItemTm item = tblItem.getSelectionModel().getSelectedItem().getValue();
        deleteItem(new Item(
                item.getCode(),
                item.getDescription(),
                item.getUnitPrice(),
                item.getQtyOnHand()
        ));
    }

    private void deleteItem(Item item) {
        try {

            Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to Delete " + item.getCode() + " item?", ButtonType.YES, ButtonType.NO).showAndWait();
            if(buttonType.get()==ButtonType.YES){

                Boolean isDeleted = CrudUtil.execute(
                        "DELETE FROM item WHERE code=?",
                        item.getCode()
                );

                if(isDeleted){
                    new Alert(Alert.AlertType.INFORMATION,"Item Deleted...!").show();
                    loadTable();
                    clearField();
                }else {
                    new Alert(Alert.AlertType.INFORMATION,"Item not Deleted...!").show();
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
        addItem();
    }

    private void addItem() {
        Item item = new Item(
                lblCode.getText(),
                txtDescription.getText(),
                Double.parseDouble(txtUnitePrice.getText()),
                Integer.parseInt(txtQryOnHand.getText())
        );

        try {

            Boolean isAdded = CrudUtil.execute(
                    "INSERT INTO item VALUES(?,?,?,?)",
                    item.getCode(),
                    item.getDescription(),
                    item.getUnitPrice(),
                    item.getQtyOnHand()
            );


            if(isAdded){
                new Alert(Alert.AlertType.CONFIRMATION,"Item Saved...!").show();
                clearField();
                loadTable();
            }else {
                new Alert(Alert.AlertType.INFORMATION, "Item not Saved...!").show();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void btnSearchOnAction(ActionEvent event) {

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        updateItem();

    }

    private void updateItem() {
        Item item = new Item(
                lblCode.getText(),
                txtDescription.getText(),
                Double.parseDouble(txtUnitePrice.getText()),
                Integer.parseInt(txtQryOnHand.getText())

        );

        try {

            Boolean isUpdate = CrudUtil.execute(
                    "UPDATE item SET description=?, unitPrice=?, qtyOnHand=? WHERE code=?",
                    item.getDescription(),
                    item.getUnitPrice(),
                    item.getQtyOnHand(),
                    item.getCode()
            );

            if(isUpdate){
                new Alert(Alert.AlertType.INFORMATION,"Item Updated...!").show();
                clearField();
                loadTable();
            }else {
                new Alert(Alert.AlertType.INFORMATION,"Item not Updated...!").show();
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


}
