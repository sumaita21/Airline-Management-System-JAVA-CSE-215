package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CouponManagementController implements Initializable {

    @FXML private TextField couponCodeField, discountField;
    @FXML private DatePicker expiryPicker;
    @FXML private Label formMessage;
    @FXML private TableView<ObservableList<String>> couponsTable;
    @FXML private TableColumn<ObservableList<String>,String> colCode,
                              colDiscount, colExpiry, colActive;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCode    .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        colDiscount.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colExpiry  .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        colActive  .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));
        loadCoupons();
    }

    private void loadCoupons() {
        ObservableList<ObservableList<String>> list =
            FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT * FROM coupons ORDER BY expiry_date DESC");
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rs.getString("coupon_code"));
                row.add(rs.getString("discount_percent") + "%");
                row.add(rs.getString("expiry_date"));
                row.add(rs.getBoolean("is_active") ? "✅ Yes" : "❌ No");
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        couponsTable.setItems(list);
    }

    @FXML
    private void handleAddCoupon(ActionEvent e) {
        String code     = couponCodeField.getText().trim().toUpperCase();
        String discount = discountField.getText().trim();
        if (code.isEmpty() || discount.isEmpty() || expiryPicker.getValue() == null) {
            setMsg("⚠ Fill all fields!", "red"); return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO coupons (coupon_code, discount_percent, " +
                "expiry_date, is_active) VALUES (?,?,?,1)");
            ps.setString(1, code);
            ps.setDouble(2, Double.parseDouble(discount));
            ps.setString(3, expiryPicker.getValue().toString());
            ps.executeUpdate();
            setMsg("✅ Coupon created: " + code, "green");
            couponCodeField.clear(); discountField.clear();
            expiryPicker.setValue(null);
            loadCoupons();
        } catch (Exception ex) {
            setMsg("❌ Code already exists!", "red");
        }
    }

    @FXML
    private void handleDeactivate(ActionEvent e) {
        ObservableList<String> selected =
            couponsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setMsg("⚠ Select a coupon first!", "red"); return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE coupons SET is_active=0 WHERE coupon_code=?");
            ps.setString(1, selected.get(0));
            ps.executeUpdate();
            setMsg("✅ Coupon deactivated!", "green");
            loadCoupons();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void setMsg(String msg, String color) {
        formMessage.setStyle("-fx-text-fill: " + color + ";");
        formMessage.setText(msg);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("admin_dashboard.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }
}