package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML private Label summaryFlightLabel, summaryPassengerLabel,
                        summarySeatLabel, finalAmountLabel, errorLabel;
    @FXML private RadioButton bankRadio, cardRadio, bkashRadio;
    @FXML private VBox bankFields, cardFields, bkashFields;
    @FXML private TextField accountNumberField, cardNumberField, bkashNumberField;
    @FXML private PasswordField bankPinField, cardPinField, bkashPinField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FlightSearch f = Session.selectedFlight;

        summaryFlightLabel.setText("✈ " + f.getSource()
            + " → " + f.getDestination()
            + "  |  Flight: " + f.getFlightNo()
            + "  |  Date: " + f.getFlightDate());
        summaryPassengerLabel.setText("👤 Passenger: "
            + Session.passengerName);
        summarySeatLabel.setText("💺 Seat: " + Session.selectedSeat
            + "  |  Class: " + Session.selectedClass);
        finalAmountLabel.setText("Total: ৳ "
            + String.format("%.0f", Session.finalPrice));

        // Group radio buttons
        ToggleGroup group = new ToggleGroup();
        bankRadio .setToggleGroup(group);
        cardRadio .setToggleGroup(group);
        bkashRadio.setToggleGroup(group);
    }

    @FXML
    private void handlePaymentMethod(ActionEvent e) {
        bankFields .setVisible(false); bankFields .setManaged(false);
        cardFields .setVisible(false); cardFields .setManaged(false);
        bkashFields.setVisible(false); bkashFields.setManaged(false);

        if      (bankRadio .isSelected()) {
            bankFields .setVisible(true); bankFields .setManaged(true);
        }
        else if (cardRadio .isSelected()) {
            cardFields .setVisible(true); cardFields .setManaged(true);
        }
        else if (bkashRadio.isSelected()) {
            bkashFields.setVisible(true); bkashFields.setManaged(true);
        }
    }

    @FXML
    private void handleConfirmPayment(ActionEvent event) {
        if (!bankRadio.isSelected() && !cardRadio.isSelected()
                && !bkashRadio.isSelected()) {
            errorLabel.setText("⚠ Please select a payment method!");
            return;
        }

        String paymentMethod = "";
        String accountNo     = "";
        String pin           = "";

        if (bankRadio.isSelected()) {
            accountNo     = accountNumberField.getText().trim();
            pin           = bankPinField.getText().trim();
            paymentMethod = "Bank";
        } else if (cardRadio.isSelected()) {
            accountNo     = cardNumberField.getText().trim();
            pin           = cardPinField.getText().trim();
            paymentMethod = "Card";
        } else {
            accountNo     = bkashNumberField.getText().trim();
            pin           = bkashPinField.getText().trim();
            paymentMethod = "Bkash";
        }

        if (accountNo.isEmpty() || pin.isEmpty()) {
            errorLabel.setText("⚠ Please fill all payment fields!");
            return;
        }
        if (pin.length() < 4) {
            errorLabel.setText("⚠ PIN must be at least 4 digits!");
            return;
        }

        saveBooking(paymentMethod, event);
    }

    private void saveBooking(String paymentMethod, ActionEvent event) {
        try (Connection conn = DBConnection.getConnection()) {
            String bookingId = "BK" + System.currentTimeMillis();

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO bookings (booking_id, user_id, flight_id, " +
                "passenger_name, seat_number, seat_class, original_price, " +
                "discount, final_price, payment_method, status) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,'confirmed')");
            ps.setString(1, bookingId);
            ps.setInt   (2, Session.userId);
            ps.setInt   (3, Session.selectedFlight.getId());
            ps.setString(4, Session.passengerName);
            ps.setString(5, Session.selectedSeat);
            ps.setString(6, Session.selectedClass);
            ps.setDouble(7, Session.originalPrice);
            ps.setDouble(8, Session.discountPercent);
            ps.setDouble(9, Session.finalPrice);
            ps.setString(10, paymentMethod);
            ps.executeUpdate();

            // Reduce available seats
            PreparedStatement ps2 = conn.prepareStatement(
                "UPDATE flights SET available_seats = available_seats - 1 " +
                "WHERE id=?");
            ps2.setInt(1, Session.selectedFlight.getId());
            ps2.executeUpdate();

            Session.bookingId     = bookingId;
            Session.paymentMethod = paymentMethod;

            // Navigate to ticket
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("ticket.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
            stage.setTitle("Your Ticket ✈");

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("❌ Payment failed! " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("book_flight.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }
}