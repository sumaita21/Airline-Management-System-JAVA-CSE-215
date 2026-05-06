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

public class ReportsController implements Initializable {

    @FXML private Label revenueLabel, confirmedLabel,
                        cancelledLabel, passengersLabel;
    @FXML private TableView<ObservableList<String>> routesTable, allBookingsTable;
    @FXML private TableColumn<ObservableList<String>,String> colRoute,
                              colBookings, colRevenue;
    @FXML private TableColumn<ObservableList<String>,String> colBId, colBUser,
                              colBFlight, colBRoute, colBClass,
                              colBPrice, colBMethod, colBStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumns();
        loadStats();
        loadTopRoutes();
        loadAllBookings();
    }

    private void setupColumns() {
        colRoute   .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        colBookings.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colRevenue .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));

        colBId    .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        colBUser  .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colBFlight.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        colBRoute .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));
        colBClass .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(4)));
        colBPrice .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(5)));
        colBMethod.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(6)));
        colBStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(7)));
    }

    private void loadStats() {
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet r1 = conn.createStatement().executeQuery(
                "SELECT SUM(final_price) FROM bookings WHERE status='confirmed'");
            if (r1.next()) revenueLabel.setText("৳ "
                + String.format("%.0f", r1.getDouble(1)));

            ResultSet r2 = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM bookings WHERE status='confirmed'");
            if (r2.next()) confirmedLabel.setText(String.valueOf(r2.getInt(1)));

            ResultSet r3 = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM bookings WHERE status='cancelled'");
            if (r3.next()) cancelledLabel.setText(String.valueOf(r3.getInt(1)));

            ResultSet r4 = conn.createStatement().executeQuery(
                "SELECT COUNT(DISTINCT user_id) FROM bookings");
            if (r4.next()) passengersLabel.setText(String.valueOf(r4.getInt(1)));

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadTopRoutes() {
        ObservableList<ObservableList<String>> list =
            FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT CONCAT(f.source,' → ',f.destination) as route, " +
                "COUNT(b.id) as total, SUM(b.final_price) as revenue " +
                "FROM bookings b JOIN flights f ON b.flight_id = f.id " +
                "WHERE b.status='confirmed' " +
                "GROUP BY f.source, f.destination " +
                "ORDER BY total DESC LIMIT 10");
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rs.getString("route"));
                row.add(String.valueOf(rs.getInt("total")));
                row.add("৳ " + String.format("%.0f", rs.getDouble("revenue")));
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        routesTable.setItems(list);
    }

    private void loadAllBookings() {
        ObservableList<ObservableList<String>> list =
            FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT b.booking_id, u.username, f.flight_no, " +
                "CONCAT(f.source,' → ',f.destination), " +
                "b.seat_class, b.final_price, b.payment_method, b.status " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.id " +
                "JOIN flights f ON b.flight_id = f.id " +
                "ORDER BY b.booked_at DESC");
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 8; i++) row.add(rs.getString(i));
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        allBookingsTable.setItems(list);
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