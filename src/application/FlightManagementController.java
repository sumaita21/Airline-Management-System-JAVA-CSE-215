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

public class FlightManagementController implements Initializable {

    @FXML private TextField flightNoField, sourceField, destField,
                            timeField, economyPriceField,
                            businessPriceField, firstPriceField, seatsField;
    @FXML private DatePicker datePicker;
    @FXML private Label formMessage;
    @FXML private TableView<FlightSearch> flightsTable;
    @FXML private TableColumn<FlightSearch,String>  colNo, colFrom, colTo,
                              colDate, colTime;
    @FXML private TableColumn<FlightSearch,Double>  colEconomy,
                              colBusiness, colFirst;
    @FXML private TableColumn<FlightSearch,Integer> colSeats;
    @FXML private TableColumn<FlightSearch,String>  colStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNo      .setCellValueFactory(new PropertyValueFactory<>("flightNo"));
        colFrom    .setCellValueFactory(new PropertyValueFactory<>("source"));
        colTo      .setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate    .setCellValueFactory(new PropertyValueFactory<>("flightDate"));
        colTime    .setCellValueFactory(new PropertyValueFactory<>("flightTime"));
        colEconomy .setCellValueFactory(new PropertyValueFactory<>("economyPrice"));
        colBusiness.setCellValueFactory(new PropertyValueFactory<>("businessPrice"));
        colFirst   .setCellValueFactory(new PropertyValueFactory<>("firstPrice"));
        colSeats   .setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
        colStatus  .setCellValueFactory(new PropertyValueFactory<>("status"));
        loadFlights();
    }

    private void loadFlights() {
        ObservableList<FlightSearch> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT * FROM flights ORDER BY flight_date");
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
                    rs.getInt("available_seats")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        flightsTable.setItems(list);
    }

    @FXML
    private void handleAddFlight(ActionEvent e) {
        String flightNo  = flightNoField.getText().trim();
        String source    = sourceField.getText().trim();
        String dest      = destField.getText().trim();
        String time      = timeField.getText().trim();
        String ePrice    = economyPriceField.getText().trim();
        String bPrice    = businessPriceField.getText().trim();
        String fPrice    = firstPriceField.getText().trim();
        String seats     = seatsField.getText().trim();

        if (flightNo.isEmpty() || source.isEmpty() || dest.isEmpty()
                || time.isEmpty() || ePrice.isEmpty()
                || datePicker.getValue() == null) {
            setMessage("⚠ Please fill all fields!", "red"); return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO flights (flight_no, source, destination, " +
                "flight_date, flight_time, economy_price, business_price, " +
                "first_price, total_seats, available_seats, status) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,'scheduled')");
            ps.setString(1, flightNo);
            ps.setString(2, source);
            ps.setString(3, dest);
            ps.setString(4, datePicker.getValue().toString());
            ps.setString(5, time);
            ps.setDouble(6, Double.parseDouble(ePrice));
            ps.setDouble(7, bPrice.isEmpty() ? 0 : Double.parseDouble(bPrice));
            ps.setDouble(8, fPrice.isEmpty() ? 0 : Double.parseDouble(fPrice));
            ps.setInt(9,    seats.isEmpty() ? 150 : Integer.parseInt(seats));
            ps.setInt(10,   seats.isEmpty() ? 150 : Integer.parseInt(seats));
            ps.executeUpdate();

            setMessage("✅ Flight added successfully!", "green");
            clearForm();
            loadFlights();
        } catch (Exception ex) {
            setMessage("❌ Error: " + ex.getMessage(), "red");
        }
    }

    @FXML
    private void handleCancelFlight(ActionEvent e) {
        FlightSearch selected = flightsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setMessage("⚠ Select a flight first!", "red"); return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE flights SET status='cancelled' WHERE id=?");
            ps.setInt(1, selected.getId());
            ps.executeUpdate();
            setMessage("✅ Flight cancelled!", "green");
            loadFlights();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @FXML
    private void handleDeleteFlight(ActionEvent e) {
        FlightSearch selected = flightsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setMessage("⚠ Select a flight first!", "red"); return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.initOwner(((Node) e.getSource()).getScene().getWindow());
        confirm.setContentText("Delete flight " + selected.getFlightNo() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM flights WHERE id=?");
                ps.setInt(1, selected.getId());
                ps.executeUpdate();
                setMessage("✅ Flight deleted!", "green");
                loadFlights();
            } catch (Exception ex) {
                setMessage("❌ Cannot delete — has bookings!", "red");
            }
        }
    }

    private void setMessage(String msg, String color) {
        formMessage.setStyle("-fx-text-fill: " + color + ";");
        formMessage.setText(msg);
    }

    private void clearForm() {
        flightNoField.clear(); sourceField.clear(); destField.clear();
        timeField.clear(); economyPriceField.clear();
        businessPriceField.clear(); firstPriceField.clear();
        seatsField.clear(); datePicker.setValue(null);
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