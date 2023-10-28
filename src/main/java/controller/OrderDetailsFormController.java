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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import entity.Order;
import entity.OrderDetails;
import entity.tm.OrderDetailsTm;
import entity.tm.OrderTm;
import util.CrudUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDetailsFormController {

    @FXML
    private AnchorPane OrderDetailsFormPane;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private TreeTableColumn<?, ?> colAmount;

    @FXML
    private TreeTableColumn<?, ?> colCustomerName;

    @FXML
    private TreeTableColumn<?, ?> colDate;

    @FXML
    private TreeTableColumn<?, ?> colDescription;

    @FXML
    private TreeTableColumn<?, ?> colItemCode;

    @FXML
    private TreeTableColumn<?, ?> colOption;

    @FXML
    private TreeTableColumn<?, ?> colOrderID;

    @FXML
    private TreeTableColumn<?, ?> colQTY;

    @FXML
    private JFXTreeTableView<OrderDetailsTm> tblOrderDetails;

    @FXML
    private JFXTreeTableView<OrderTm> tblOrders;

    @FXML
    private JFXTextField txtSearch;

    public void initialize(){
        colOrderID.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        colCustomerName.setCellValueFactory(new TreeItemPropertyValueFactory<>("custName"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        colItemCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colQTY.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));

        loadOrders();

        tblOrders.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null){
                int selectedIndex = newValue.intValue();
                TreeItem<OrderTm>selectedTreeItem = tblOrders.getTreeItem(selectedIndex);
                if(selectedTreeItem != null){
                    loadDetails(selectedTreeItem);
                }
            }
        });
    }

    private void loadDetails(TreeItem<OrderTm> selectedTreeItem) {

        ObservableList<OrderDetailsTm> tmList = FXCollections.observableArrayList();

        try {
            List<OrderDetails> list = new ArrayList<>();

//            Connection connection = DBConnection.getInstance().getConnection();
//            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM orderdetail WHERE orderId=?");
//            pstm.setString(1,selectedTreeItem.getValue().getId());
//            ResultSet resultSet = pstm.executeQuery();

            ResultSet resultSet = CrudUtil.execute(
                    "SELECT * FROM orderdetail WHERE orderId=?",
                    selectedTreeItem.getValue().getId()
            );

            while (resultSet.next()){



                list.add(new OrderDetails(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getDouble(4)
                ));
            }

            for(OrderDetails detail : list){

//                pstm = connection.prepareStatement("SELECT description FROM item WHERE code=?");
//                pstm.setString(1,detail.getItemCode());
//                ResultSet rsSet = pstm.executeQuery();

                ResultSet rsSet = CrudUtil.execute(
                        "SELECT description FROM item WHERE code=?",
                        detail.getItemCode()
                );

                rsSet.next();
                tmList.add(new OrderDetailsTm(
                        detail.getItemCode(),
                        rsSet.getString(1),
                        detail.getQty(),
                        detail.getUnitPrice()*detail.getQty()
                ));

            }

            TreeItem<OrderDetailsTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblOrderDetails.setRoot(treeItem);
            tblOrderDetails.setShowRoot(false);



        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadOrders() {
        ObservableList<OrderTm> tmList = FXCollections.observableArrayList();

        try {
            List<Order> list = new ArrayList<>();

//            Connection connection = DBConnection.getInstance().getConnection();
//            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM orders");
//            ResultSet resultSet = pstm.executeQuery();

            ResultSet resultSet = CrudUtil.execute(
                    "SELECT * FROM orders"
            );

            while (resultSet.next()){
                list.add(new Order(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3)
                ));
            }

            for(Order order : list){
                JFXButton btn = new JFXButton("Delete");
                BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(227, 92, 92), null, null);
                Background background = new Background(backgroundFill);
                btn.setBackground(background);

                btn.setOnAction(actionEvent -> {

                    try {
//                        PreparedStatement pst = connection.prepareStatement("DELETE FROM orders WHERE id=?");
//                        pst.setString(1,order.getId());

                        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to Delete "+ order.getId()+" Order?", ButtonType.YES, ButtonType.NO).showAndWait();
                        if(buttonType.get()==ButtonType.YES){

                            Boolean isDeleted = CrudUtil.execute(
                                    "DELETE FROM orders WHERE id=?",
                                    order.getId()
                            );

                            if(isDeleted){
                                new Alert(Alert.AlertType.INFORMATION,"Order Deleted...!").show();
                                loadOrders();
                            }else {
                                new Alert(Alert.AlertType.INFORMATION,"Order not Deleted...!").show();
                            }
                        }

                    } catch (SQLException | RuntimeException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }


                });

//                PreparedStatement pt = connection.prepareStatement("SELECT name FROM customer WHERE id=?");
//                pt.setString(1,order.getCustomerId());
//                ResultSet rst = pt.executeQuery();

                ResultSet rst = CrudUtil.execute(
                        "SELECT name FROM customer WHERE id=?",
                        order.getCustomerId()
                );
                rst.next();
                tmList.add(new OrderTm(
                        order.getId(),
                        order.getDate(),
                        rst.getString(1),
                        btn
                ));

            }

            TreeItem<OrderTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblOrders.setRoot(treeItem);
            tblOrders.setShowRoot(false);



        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void btnBackOnAction(ActionEvent event) {

        Stage stage = (Stage) OrderDetailsFormPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBoard.fxml"))));
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {

    }
}

