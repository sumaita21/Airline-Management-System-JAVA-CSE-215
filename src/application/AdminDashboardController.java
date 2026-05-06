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

public class AdminDashboardController implements Initializable {

    @FXML private Label totalFlightsLabel, totalUsersLabel,
                        totalBookingsLabel, totalRevenueLabel;
    @FXML private TableView<AdminBooking> recentBookingsTable;
    @FXML private TableColumn<AdminBooking,String> colBId, colBUser,
                              colBFlight, colBFrom, colBTo, colBStatus;
    @FXML private TableColumn<AdminBooking,Double> colBPrice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadStats();
        loadRecentBookings();
    }

    private void setupTable() {
        colBId    .setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colBUser  .setCellValueFactory(new PropertyValueFactory<>("username"));
        colBFlight.setCellValueFactory(new PropertyValueFactory<>("flightNo"));
        colBFrom  .setCellValueFactory(new PropertyValueFactory<>("source"));
        colBTo    .setCellValueFactory(new PropertyValueFactory<>("destination"));
        colBPrice .setCellValueFactory(new PropertyValueFactory<>("finalPrice"));
        colBStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadStats() {
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet r1 = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM flights");
            if (r1.next())
                totalFlightsLabel.setText(String.valueOf(r1.getInt(1)));

            ResultSet r2 = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM users WHERE role='user'");
            if (r2.next())
                totalUsersLabel.setText(String.valueOf(r2.getInt(1)));

            ResultSet r3 = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM bookings " +
                              "WHERE status='confirmed'");
            if (r3.next())
                totalBookingsLabel.setText(String.valueOf(r3.getInt(1)));

            ResultSet r4 = conn.createStatement()
                .executeQuery("SELECT SUM(final_price) FROM bookings " +
                              "WHERE status='confirmed'");
            if (r4.next())
                totalRevenueLabel.setText(
                    String.format("%.0f", r4.getDouble(1)));

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadRecentBookings() {
        ObservableList<AdminBooking> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT b.booking_id, u.username, f.flight_no, " +
                "f.source, f.destination, b.final_price, b.status " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.id " +
                "JOIN flights f ON b.flight_id = f.id " +
                "ORDER BY b.booked_at DESC LIMIT 20");
            while (rs.next()) {
                list.add(new AdminBooking(
                    rs.getString("booking_id"),
                    rs.getString("username"),
                    rs.getString("flight_no"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getDouble("final_price"),
                    rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        recentBookingsTable.setItems(list);
    }

    @FXML
    private void showDashboard(ActionEvent e) {
        loadStats();
        loadRecentBookings();
    }

    @FXML
    private void showFlightManagement(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("flight_management.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void showUserManagement(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("user_management.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void showEmployeeManagement(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("employee_management.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void showCouponManagement(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("coupon_management.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void showReports(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("reports.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void showAdminBook(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("admin_book.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Session.userId = 0;
            Session.role   = null;
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("login.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
            stage.setTitle("Login");
        } catch (Exception e) { e.printStackTrace(); }
    }
}