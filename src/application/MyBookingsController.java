package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyBookingsController implements Initializable {

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking,String> colBookingId, colFlight,
                              colFrom, colTo, colDate, colSeat, colClass, colStatus;
    @FXML private TableColumn<Booking,Double> colPrice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colFlight   .setCellValueFactory(new PropertyValueFactory<>("flightNo"));
        colFrom     .setCellValueFactory(new PropertyValueFactory<>("source"));
        colTo       .setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate     .setCellValueFactory(new PropertyValueFactory<>("flightDate"));
        colSeat     .setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        colClass    .setCellValueFactory(new PropertyValueFactory<>("seatClass"));
        colPrice    .setCellValueFactory(new PropertyValueFactory<>("finalPrice"));
        colStatus   .setCellValueFactory(new PropertyValueFactory<>("status"));
        loadBookings();
    }

    private void loadBookings() {
        ObservableList<Booking> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT b.booking_id, f.flight_no, f.source, f.destination, " +
                "f.flight_date, b.seat_number, b.seat_class, " +
                "b.final_price, b.status " +
                "FROM bookings b JOIN flights f ON b.flight_id = f.id " +
                "WHERE b.user_id=? ORDER BY b.booked_at DESC");
            ps.setInt(1, Session.userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Booking(
                    rs.getString("booking_id"),
                    rs.getString("flight_no"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("flight_date"),
                    rs.getString("seat_number"),
                    rs.getString("seat_class"),
                    rs.getDouble("final_price"),
                    rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        bookingsTable.setItems(list);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a booking to cancel!"); return;
        }
        if (selected.getStatus().equals("cancelled")) {
            showAlert("This booking is already cancelled!"); return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.initOwner(((Node) event.getSource()).getScene().getWindow());
        confirm.setTitle("Cancel Booking");
        confirm.setContentText("Cancel booking " + selected.getBookingId() + "?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE bookings SET status='cancelled' WHERE booking_id=?");
                ps.setString(1, selected.getBookingId());
                ps.executeUpdate();
                loadBookings();
                showAlert("✅ Booking cancelled successfully!");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.initOwner(bookingsTable.getScene().getWindow());
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("user_dashboard.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }
}