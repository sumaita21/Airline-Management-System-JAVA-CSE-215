package application;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BookFlightController implements Initializable {

    @FXML private Label flightInfoLabel, flightDateLabel;
    @FXML private TextField passengerNameField, couponField, employeeIdField;
    @FXML private ComboBox<String> seatClassCombo, seatCombo;
    @FXML private VBox couponListBox;
    @FXML private Label couponLabel, employeeLabel;
    @FXML private Label originalPriceLabel, discountLabel,
                        finalPriceLabel, errorLabel;

    private double originalPrice   = 0;
    private double discountPercent = 0;
    private String discountSource  = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FlightSearch f = Session.selectedFlight;
        flightInfoLabel.setText("✈ " + f.getSource()
            + " → " + f.getDestination()
            + "  |  Flight: " + f.getFlightNo());
        flightDateLabel.setText("Date: " + f.getFlightDate()
            + "  |  Time: " + f.getFlightTime());

        seatClassCombo.setItems(FXCollections.observableArrayList(
            "Economy", "Business", "First"));

        loadAvailableCoupons();
    }

    // Load and display available coupons as clickable buttons
    private void loadAvailableCoupons() {
        couponListBox.getChildren().clear();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT coupon_code, discount_percent, expiry_date " +
                "FROM coupons WHERE is_active=1 AND expiry_date >= CURDATE() " +
                "ORDER BY discount_percent DESC");

            boolean found = false;
            while (rs.next()) {
                found = true;
                String code     = rs.getString("coupon_code");
                String discount = rs.getString("discount_percent");
                String expiry   = rs.getString("expiry_date");

                // Create clickable coupon card
                HBox card = new HBox(10);
                card.setStyle("-fx-background-color: #e8f5e9; " +
                              "-fx-padding: 8 12; -fx-background-radius: 6; " +
                              "-fx-cursor: hand;");

                Label codeLabel = new Label("🎟 " + code);
                codeLabel.setStyle("-fx-font-weight: bold; " +
                                   "-fx-text-fill: #1b5e20; -fx-font-size: 13px;");

                Label discLabel = new Label(discount + "% OFF");
                discLabel.setStyle("-fx-text-fill: #43a047; " +
                                   "-fx-font-weight: bold;");

                Label expLabel = new Label("Expires: " + expiry);
                expLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

                Button applyBtn = new Button("Apply");
                applyBtn.setStyle("-fx-background-color: #43a047; " +
                                  "-fx-text-fill: white; -fx-padding: 4 12; " +
                                  "-fx-cursor: hand; -fx-font-size: 11px;");

                final String finalCode = code;
                applyBtn.setOnAction(e -> applyCouponCode(finalCode));

                card.getChildren().addAll(codeLabel, discLabel,
                                          expLabel, applyBtn);
                couponListBox.getChildren().add(card);
            }

            if (!found) {
                Label none = new Label("No active coupons available");
                none.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
                couponListBox.getChildren().add(none);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleClassSelected(ActionEvent e) {
        String cls = seatClassCombo.getValue();
        if (cls == null) return;

        FlightSearch f = Session.selectedFlight;
        switch (cls) {
            case "Economy":  originalPrice = f.getEconomyPrice();  break;
            case "Business": originalPrice = f.getBusinessPrice(); break;
            case "First":    originalPrice = f.getFirstPrice();    break;
        }
        discountPercent = 0; discountSource = "";
        couponLabel.setText(""); employeeLabel.setText("");
        updatePriceDisplay();
        loadAvailableSeats(cls);
    }

    private void loadAvailableSeats(String seatClass) {
        List<String> seats = new ArrayList<>();
        String prefix = seatClass.equals("Economy") ? "E"
                      : seatClass.equals("Business") ? "B" : "F";
        int count = seatClass.equals("Economy") ? 80
                  : seatClass.equals("Business") ? 40 : 20;
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT seat_number FROM bookings " +
                "WHERE flight_id=? AND seat_class=? AND status='confirmed'");
            ps.setInt(1, Session.selectedFlight.getId());
            ps.setString(2, seatClass);
            ResultSet rs = ps.executeQuery();
            List<String> booked = new ArrayList<>();
            while (rs.next()) booked.add(rs.getString("seat_number"));
            for (int i = 1; i <= count; i++) {
                String seat = prefix + i;
                if (!booked.contains(seat)) seats.add(seat);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        seatCombo.setItems(FXCollections.observableArrayList(seats));
        if (!seats.isEmpty()) seatCombo.setValue(seats.get(0));
    }

    // Apply coupon by code (called from both button and manual entry)
    private void applyCouponCode(String code) {
        if (!discountSource.isEmpty() && discountSource.equals("employee")) {
            couponLabel.setStyle("-fx-text-fill: red;");
            couponLabel.setText("❌ Cannot combine with employee discount!");
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM coupons WHERE coupon_code=? " +
                "AND is_active=1 AND expiry_date >= CURDATE()");
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                discountPercent = rs.getDouble("discount_percent");
                discountSource  = "coupon";
                couponField.setText(code);
                couponLabel.setStyle("-fx-text-fill: green;");
                couponLabel.setText("✅ Coupon applied: "
                    + code + " (" + discountPercent + "% off)");
                updatePriceDisplay();
            } else {
                couponLabel.setStyle("-fx-text-fill: red;");
                couponLabel.setText("❌ Invalid or expired coupon!");
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @FXML
    private void handleApplyCoupon(ActionEvent e) {
        String code = couponField.getText().trim().toUpperCase();
        if (code.isEmpty()) {
            couponLabel.setStyle("-fx-text-fill: red;");
            couponLabel.setText("⚠ Enter a coupon code!"); return;
        }
        applyCouponCode(code);
    }

    @FXML
    private void handleClearCoupon(ActionEvent e) {
        if (discountSource.equals("coupon")) {
            discountPercent = 0; discountSource = "";
            updatePriceDisplay();
        }
        couponField.clear();
        couponLabel.setText("");
    }

    @FXML
    private void handleApplyEmployee(ActionEvent e) {
        if (!discountSource.isEmpty() && discountSource.equals("coupon")) {
            employeeLabel.setStyle("-fx-text-fill: red;");
            employeeLabel.setText("❌ Cannot combine with coupon!"); return;
        }
        String empId = employeeIdField.getText().trim();
        if (empId.isEmpty()) {
            employeeLabel.setStyle("-fx-text-fill: red;");
            employeeLabel.setText("⚠ Enter employee ID!"); return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT e.role, e.staff_category FROM employees e " +
                "WHERE e.employee_id=?");
            ps.setString(1, empId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role     = rs.getString("role");
                String category = rs.getString("staff_category");

                // Admin = 85%, Staff (pilot/crew) = 70%
                if (role.equals("admin")) {
                    discountPercent = 85.0;
                } else {
                    discountPercent = 70.0;
                }
                discountSource = "employee";

                String label = role.equals("admin")
                    ? "Admin (85% discount)"
                    : (category != null ? category.substring(0,1).toUpperCase()
                       + category.substring(1) : "Staff")
                      + " (70% discount)";

                employeeLabel.setStyle("-fx-text-fill: green;");
                employeeLabel.setText("✅ " + label + " applied!");
                updatePriceDisplay();
            } else {
                employeeLabel.setStyle("-fx-text-fill: red;");
                employeeLabel.setText("❌ Employee ID not found!");
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @FXML
    private void handleClearEmployee(ActionEvent e) {
        if (discountSource.equals("employee")) {
            discountPercent = 0; discountSource = "";
            updatePriceDisplay();
        }
        employeeIdField.clear();
        employeeLabel.setText("");
    }

    private void updatePriceDisplay() {
        double discount   = originalPrice * discountPercent / 100;
        double finalPrice = originalPrice - discount;
        originalPriceLabel.setText("৳ " + String.format("%.0f", originalPrice));
        discountLabel.setText(discountPercent + "% (-৳ "
            + String.format("%.0f", discount) + ")");
        finalPriceLabel.setText("৳ " + String.format("%.0f", finalPrice));
        Session.finalPrice      = finalPrice;
        Session.originalPrice   = originalPrice;
        Session.discountPercent = discountPercent;
    }

    @FXML
    private void handleProceedPayment(ActionEvent event) {
        String name = passengerNameField.getText().trim();
        String cls  = seatClassCombo.getValue();
        String seat = seatCombo.getValue();

        if (name.isEmpty() || cls == null || seat == null) {
            errorLabel.setText("⚠ Please fill all required fields!"); return;
        }

        Session.passengerName  = name;
        Session.selectedSeat   = seat;
        Session.selectedClass  = cls;

        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("payment.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
            stage.setTitle("Payment");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("search_flight.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }
}