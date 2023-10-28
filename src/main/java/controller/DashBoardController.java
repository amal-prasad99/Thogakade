package controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashBoardController implements Initializable {

    public AnchorPane dashboardPane;
    @FXML
    private JFXButton btnCustomer;

    @FXML
    private JFXButton btnItem;

    @FXML
    private JFXButton btnOrder;

    @FXML
    private JFXButton btnPlaceOrder;

    @FXML
    private Label lbblDate;

    @FXML
    private Label lblTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manageDateAndTime();
    }

    private void manageDateAndTime() {
        Timeline date = new Timeline(new KeyFrame(Duration.ZERO,
                actionEvent -> lbblDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))),
                new KeyFrame(Duration.seconds(1)));
        date.setCycleCount(Animation.INDEFINITE);
        date.play();

        Timeline time = new Timeline(new KeyFrame(
                Duration.ZERO,
                actionEvent -> lblTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        ), new KeyFrame(Duration.seconds(1)));
        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }


    public void btnCustomerOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) dashboardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/CustomerForm.fxml"))));
        }catch (IOException | RuntimeException e){
            e.printStackTrace();
        }
        stage.show();

    }

    public void btnItemOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) dashboardPane.getScene().getWindow();

        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/ItemForm.fxml"))));
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    public void btnOrderOnAction(ActionEvent actionEvent) {

        Stage stage = (Stage) dashboardPane.getScene().getWindow();

        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/OrderDetailsForm.fxml"))));
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
        stage.show();

    }

    public void btnAPlaceOrderction(ActionEvent actionEvent) {

        Stage stage = (Stage) dashboardPane.getScene().getWindow();

        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/PlaceOrderForm.fxml"))));
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
        stage.show();

    }


}
