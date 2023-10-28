package controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import entity.Order;
import entity.OrderDetails;
import entity.tm.CartTm;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PlaceOrderFormController implements Initializable {

    @FXML
    private AnchorPane PlaceOrderPane;

    @FXML
    private JFXButton btnAddToCart;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnPlaceOrder;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private JFXComboBox cmbCustomerID;

    @FXML
    private JFXComboBox cmbItemCode;

    @FXML
    private TreeTableColumn colAmount;

    @FXML
    private TreeTableColumn colDescription;

    @FXML
    private TreeTableColumn colItemCode;

    @FXML
    private TreeTableColumn colQTY;

    @FXML
    private TreeTableColumn colUnitPrice;

    @FXML
    private TreeTableColumn colOption;

    @FXML
    private Label lblQTYOnHand;

    @FXML
    private Label lblOrderID;

    @FXML
    private Label lblTotalAmount;

    @FXML
    private Label lblUnitePrice;

    @FXML
    private JFXTreeTableView<CartTm> tblOrders;

    @FXML
    private JFXTextField txtCustomerName;

    @FXML
    private Text txtDescription;

    @FXML
    private JFXTextField txtQTY;

    @FXML
    private JFXTextField txtQTYOnHand;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXTextField txtCodeDescription;

    @FXML
    private JFXTextField txtQTYHand;

    ObservableList<CartTm> cartTmList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colItemCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colQTY.setCellValueFactory(new TreeItemPropertyValueFactory<>("qtyOnHand"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));


        generateID();
        loadCustomerID();
        loadItemCodes();
        setCustumerName();
        setItemDetails();
    }

    private void setItemDetails() {

        cmbItemCode.setOnAction(actionEvent -> {
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE code=?");
                pstm.setString(1,cmbItemCode.getValue().toString());
                ResultSet resultSet = pstm.executeQuery();

                if(resultSet.next()){
                    txtCodeDescription.setText(resultSet.getString(2));
                    lblUnitePrice.setText(String.format("%.2f",resultSet.getDouble(3)));
                    lblQTYOnHand.setText(String.valueOf(resultSet.getInt(4)));
                }


            } catch (SQLException | RuntimeException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setCustumerName() {

        cmbCustomerID.setOnAction(actionEvent -> {

            try {
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement pstm = connection.prepareStatement("SELECT name FROM customer WHERE id=?");
                pstm.setString(1,cmbCustomerID.getValue().toString());
                ResultSet resultSet = pstm.executeQuery();

                if(resultSet.next()){
                    txtCustomerName.setText(resultSet.getString(1));
                }

            } catch (SQLException | RuntimeException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void loadItemCodes() {

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT code FROM item");
            ResultSet resultSet = pstm.executeQuery();

            ObservableList<String> itemCodes = FXCollections.observableArrayList();
            while (resultSet.next()){
                itemCodes.add(resultSet.getString(1));
            }
            cmbItemCode.setItems(itemCodes);

        } catch (SQLException | RuntimeException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCustomerID() {

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT id FROM customer");
            ResultSet resultSet = pstm.executeQuery();

            ObservableList<String> customerIds = FXCollections.observableArrayList();
            while (resultSet.next()){
                customerIds.add(resultSet.getString(1));
            }

            cmbCustomerID.setItems(customerIds);

        } catch (SQLException | RuntimeException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateID() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT id FROM orders ORDER BY id DESC LIMIT 1");
            ResultSet resultSet = pstm.executeQuery();
            if(resultSet.next()){
                int cusID = Integer.parseInt(resultSet.getString(1).split("[D]")[1]);
                cusID++;
                lblOrderID.setText(String.format("D%03d",cusID));
            }else {
                lblOrderID.setText("D001");
            }


        } catch (SQLException | RuntimeException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private double findTotal(){
        double total = 0;
        for(CartTm tm:cartTmList){
            total+=tm.getAmount();
        }
        return total;
    }

    @FXML
    void btnAddToCartOnAction(ActionEvent event) {

        Boolean isExist = false;
        for(CartTm tm : cartTmList){
            if(tm.getCode().equals(cmbItemCode.getValue().toString())){
                tm.setQtyOnHand(tm.getQtyOnHand()+Integer.parseInt(txtQTY.getText()));
                tm.setAmount(tm.getQtyOnHand()*tm.getAmount());
                isExist = true;
            }
        }
        if(!isExist){

            JFXButton btn = new JFXButton("Delete");
            BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(227, 92, 92), null, null);
            Background background = new Background(backgroundFill);
            btn.setBackground(background);



            CartTm cartTm = new CartTm(
                    cmbItemCode.getValue().toString(),
                    txtCodeDescription.getText(),
                    Double.parseDouble(lblUnitePrice.getText()),
                    Integer.parseInt(txtQTY.getText()),
                    Double.parseDouble(lblUnitePrice.getText())*Integer.parseInt(txtQTY.getText()),
                    btn
            );

            btn.setOnAction(actionEvent -> {
                cartTmList.remove(cartTm);
                lblTotalAmount.setText(String.format("%.2f",findTotal()));
                tblOrders.refresh();
            });

            cartTmList.add(cartTm);

            TreeItem<CartTm> treeItem = new RecursiveTreeItem<>(cartTmList, RecursiveTreeObject::getChildren);
            tblOrders.setRoot(treeItem);
            tblOrders.setShowRoot(false);
        }

        lblTotalAmount.setText(String.format("%.2f",findTotal()));
        tblOrders.refresh();
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        Stage stage = (Stage) PlaceOrderPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBoard.fxml"))));
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        cmbCustomerID.setValue("");
        cmbItemCode.setValue("");
        txtCodeDescription.clear();
        lblQTYOnHand.setText("0");
        lblUnitePrice.setText("0.00");
        txtCustomerName.clear();
        txtQTY.clear();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

    }


    public void btnPlaceOrderOnAction(ActionEvent actionEvent) throws SQLException {
        List<OrderDetails> detailsList = new ArrayList<>();

        for(CartTm tm : cartTmList){
            detailsList.add(new OrderDetails(
                    lblOrderID.getText(),
                    tm.getCode(),
                    tm.getQtyOnHand(),
                    tm.getUnitPrice()
            ));
        }

        Order order = new Order(
                lblOrderID.getText(),
                LocalDate.now().toString(),
                cmbCustomerID.getValue().toString()
        );

        boolean isOrderPlaced = true;
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            String sql = "INSERT INTO orders VALUES(?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);

            pstm.setString(1,order.getId());
            pstm.setDate(2, Date.valueOf(order.getDate()));
            pstm.setString(3,order.getCustomerId());

            if(pstm.executeUpdate()>0){

                for(OrderDetails detail : detailsList){

                    String quary = "INSERT INTO orderdetail VALUES(?,?,?,?)";
                    PreparedStatement ptm = connection.prepareStatement(quary);

                    ptm.setString(1,detail.getOrderId());
                    ptm.setString(2, detail.getItemCode());
                    ptm.setInt(3,detail.getQty());
                    ptm.setDouble(4,detail.getUnitPrice());

                    if(ptm.executeUpdate()<=0){
                        isOrderPlaced=false;
                    }

                }

            }else{
                isOrderPlaced=false;
                new Alert(Alert.AlertType.ERROR,"Order Not Placed...!").show();
                connection.rollback();
            }


            if(isOrderPlaced){
                new Alert(Alert.AlertType.INFORMATION,"Order Placed...!").show();
                connection.commit();
                cartTmList.clear();
                tblOrders.refresh();
                clearFields();
                generateID();
            }else {
                new Alert(Alert.AlertType.ERROR,"Order Not Placed...!").show();
                connection.rollback();
            }

        }catch (SQLException | ClassCastException | ClassNotFoundException e){
            connection.rollback();
            e.printStackTrace();
        }finally {
            connection.setAutoCommit(true);
        }


    }


}
