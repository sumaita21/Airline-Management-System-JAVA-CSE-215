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

public class StaffPassengersController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<ObservableList<String>> passengersTable;
    @FXML private TableColumn<ObservableList<String>,String> colBId, colName,
                              colFlight, colFrom, colTo, colSeat,
                              colClass, colStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colBId   .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        colName  .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colFlight.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        colFrom  .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));
        colTo    .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(4)));
        colSeat  .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(5)));
        colClass .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(6)));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(7)));
        loadPassengers("");
    }

    private void loadPassengers(String filter) {
        ObservableList<ObservableList<String>> list =
            FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            String sql =
                "SELECT b.booking_id, b.passenger_name, f.flight_no, " +
                "f.source, f.destination, b.seat_number, " +
                "b.seat_class, b.status " +
                "FROM bookings b JOIN flights f ON b.flight_id = f.id " +
                "WHERE 1=1" + filter + " ORDER BY b.booked_at DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 8; i++) row.add(rs.getString(i));
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        passengersTable.setItems(list);
    }

    @FXML private void handleSearch(ActionEvent e) {
        String s = searchField.getText().trim();
        if (!s.isEmpty())
            loadPassengers(" AND (b.passenger_name LIKE '%" + s
                    + "%' OR b.booking_id LIKE '%" + s + "%')");
    }

    @FXML private void handleShowAll(ActionEvent e) {
        searchField.clear(); loadPassengers("");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("staff_dashboard.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }
}