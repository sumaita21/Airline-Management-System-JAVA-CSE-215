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

public class StaffDashboardController implements Initializable {

    @FXML private Label welcomeLabel, flightsLabel,
                        passengersLabel, bookingsLabel;
    @FXML private TableView<FlightSearch> flightsTable;
    @FXML private TableColumn<FlightSearch,String>  colNo, colFrom,
                              colTo, colDate, colTime, colStat;
    @FXML private TableColumn<FlightSearch,Integer> colSeats;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        welcomeLabel.setText("👤 " + Session.fullName);
        setupTable();
        loadStats();
        loadFlights();
    }

    private void setupTable() {
        colNo   .setCellValueFactory(new PropertyValueFactory<>("flightNo"));
        colFrom .setCellValueFactory(new PropertyValueFactory<>("source"));
        colTo   .setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate .setCellValueFactory(new PropertyValueFactory<>("flightDate"));
        colTime .setCellValueFactory(new PropertyValueFactory<>("flightTime"));
        colSeats.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
        colStat .setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadStats() {
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet r1 = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM flights WHERE status='scheduled'");
            if (r1.next()) flightsLabel.setText(String.valueOf(r1.getInt(1)));

            ResultSet r2 = conn.createStatement().executeQuery(
                "SELECT COUNT(DISTINCT passenger_name) FROM bookings");
            if (r2.next()) passengersLabel.setText(String.valueOf(r2.getInt(1)));

            ResultSet r3 = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM bookings WHERE status='confirmed'");
            if (r3.next()) bookingsLabel.setText(String.valueOf(r3.getInt(1)));

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadFlights() {
        ObservableList<FlightSearch> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM flights WHERE status='scheduled' " +
                "ORDER BY flight_date, flight_time");
            while (rs.next()) {
                list.add(new FlightSearch(
                    rs.getInt("id"),
                    rs.getString("flight_no"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("flight_date"),
                    rs.getString("flight_time"),
                    rs.getDouble("economy_price"),
                    rs.getDouble("business_price"),
                    rs.getDouble("first_price"),
                    rs.getInt("available_seats"),
                    rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        flightsTable.setItems(list);
    }

    @FXML private void showDashboard(ActionEvent e) {
        loadStats(); loadFlights();
    }

    @FXML
    private void showFlights(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("staff_flights.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void showPassengers(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("staff_passengers.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void showBookTicket(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("staff_book.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
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
        } catch (Exception e) { e.printStackTrace(); }
    }
}