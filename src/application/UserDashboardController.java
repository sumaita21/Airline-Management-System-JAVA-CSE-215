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
import java.util.ResourceBundle;

public class UserDashboardController implements Initializable {

    @FXML private Label welcomeLabel, totalFlightsLabel, myBookingsLabel;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking,String> colBookingId, colFlight,
                              colFrom, colTo, colSeat, colClass, colStatus;
    @FXML private TableColumn<Booking,Double> colPrice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        welcomeLabel.setText("Hello, " + Session.fullName + "!");
        setupTable();
        loadStats();
        loadMyBookings();
    }

    private void setupTable() {
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colFlight   .setCellValueFactory(new PropertyValueFactory<>("flightNo"));
        colFrom     .setCellValueFactory(new PropertyValueFactory<>("source"));
        colTo       .setCellValueFactory(new PropertyValueFactory<>("destination"));
        colSeat     .setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        colClass    .setCellValueFactory(new PropertyValueFactory<>("seatClass"));
        colPrice    .setCellValueFactory(new PropertyValueFactory<>("finalPrice"));
        colStatus   .setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadStats() {
        try (Connection conn = DBConnection.getConnection()) {
            // Total available flights
            ResultSet rs1 = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM flights WHERE status='scheduled'");
            if (rs1.next()) totalFlightsLabel.setText(String.valueOf(rs1.getInt(1)));

            // My bookings count
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM bookings WHERE user_id=?");
            ps.setInt(1, Session.userId);
            ResultSet rs2 = ps.executeQuery();
            if (rs2.next()) myBookingsLabel.setText(String.valueOf(rs2.getInt(1)));

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadMyBookings() {
        ObservableList<Booking> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT b.booking_id, f.flight_no, f.source, f.destination, " +
                "f.flight_date, " +  // ← ADD THIS LINE
                "b.seat_number, b.seat_class, b.final_price, b.status " +
                "FROM bookings b JOIN flights f ON b.flight_id = f.id " +
                "WHERE b.user_id = ? ORDER BY b.booked_at DESC");
            ps.setInt(1, Session.userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Booking(
                    rs.getString("booking_id"),
                    rs.getString("flight_no"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("flight_date"),  // ← ADD THIS LINE
                    rs.getString("seat_number"),
                    rs.getString("seat_class"),
                    rs.getDouble("final_price"),
                    rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        bookingsTable.setItems(list);
    }

    @FXML private void showDashboard(ActionEvent e)    { loadMyBookings(); loadStats(); }

    @FXML
    private void showSearchFlight(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("search_flight.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
            stage.setTitle("Search Flights");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void showMyBookings(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("my_bookings.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
            stage.setTitle("My Bookings");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Session.userId = 0; Session.role = null;
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("login.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
            stage.setTitle("Login");
        } catch (Exception e) { e.printStackTrace(); }
    }
}