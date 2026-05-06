package application;

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
import java.util.Optional;
import java.util.ResourceBundle;

public class TicketController implements Initializable {

    @FXML private Label sourceLabel, destLabel, passengerLabel;
    @FXML private Label bookingIdLabel, flightNoLabel, dateLabel, timeLabel;
    @FXML private Label seatLabel, classLabel, paymentLabel;
    @FXML private Label originalPriceLabel, discountLabel, finalPriceLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FlightSearch f = Session.selectedFlight;

        sourceLabel.setText(f.getSource());
        destLabel.setText(f.getDestination());
        passengerLabel.setText(Session.passengerName);
        bookingIdLabel.setText(Session.bookingId);
        flightNoLabel.setText(f.getFlightNo());
        dateLabel.setText(f.getFlightDate());
        timeLabel.setText(f.getFlightTime());
        seatLabel.setText(Session.selectedSeat);
        classLabel.setText(Session.selectedClass);
        paymentLabel.setText(Session.paymentMethod);
        originalPriceLabel.setText("৳ "
            + String.format("%.0f", Session.originalPrice));
        discountLabel.setText(Session.discountPercent + "% OFF");
        finalPriceLabel.setText("৳ "
            + String.format("%.0f", Session.finalPrice));
    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("user_dashboard.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
            stage.setTitle("Dashboard");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleCancelBooking(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(((Node) event.getSource()).getScene().getWindow());
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to cancel booking "
            + Session.bookingId + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getConnection()) {
                // Update booking status
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE bookings SET status='cancelled' " +
                    "WHERE booking_id=?");
                ps.setString(1, Session.bookingId);
                ps.executeUpdate();

                // Restore seat
                PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE flights SET available_seats = available_seats + 1 " +
                    "WHERE id=?");
                ps2.setInt(1, Session.selectedFlight.getId());
                ps2.executeUpdate();

                // Show success
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.initOwner(((Node) event.getSource()).getScene().getWindow());
                success.setTitle("Cancelled");
                success.setContentText("Booking cancelled successfully!");
                success.showAndWait();

                // Go to dashboard
                AnchorPane root = FXMLLoader.load(
                    getClass().getResource("user_dashboard.fxml"));
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));

            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}