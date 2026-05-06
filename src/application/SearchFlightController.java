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
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SearchFlightController implements Initializable {

    @FXML private TextField fromField, toField;
    @FXML private DatePicker datePicker;
    @FXML private Label resultLabel;
    @FXML private TableView<FlightSearch> flightsTable;
    @FXML private TableColumn<FlightSearch,String>  colFlightNo, colFrom,
                              colTo, colDate, colTime;
    @FXML private TableColumn<FlightSearch,Double>  colEconomy,
                              colBusiness, colFirst;
    @FXML private TableColumn<FlightSearch,Integer> colSeats;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colFlightNo .setCellValueFactory(new PropertyValueFactory<>("flightNo"));
        colFrom     .setCellValueFactory(new PropertyValueFactory<>("source"));
        colTo       .setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate     .setCellValueFactory(new PropertyValueFactory<>("flightDate"));
        colTime     .setCellValueFactory(new PropertyValueFactory<>("flightTime"));
        colEconomy  .setCellValueFactory(new PropertyValueFactory<>("economyPrice"));
        colBusiness .setCellValueFactory(new PropertyValueFactory<>("businessPrice"));
        colFirst    .setCellValueFactory(new PropertyValueFactory<>("firstPrice"));
        colSeats    .setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        // Set date range today → 20 days
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now())
                        || date.isAfter(LocalDate.now().plusDays(20)));
            }
        });
        loadFlights("");
    }

    private void loadFlights(String filter) {
        ObservableList<FlightSearch> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM flights WHERE status='scheduled'" + filter
                       + " ORDER BY flight_date, flight_time";
            ResultSet rs = conn.createStatement().executeQuery(sql);
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
        resultLabel.setText("Found " + list.size() + " flights");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String from = fromField.getText().trim();
        String to   = toField.getText().trim();
        LocalDate date = datePicker.getValue();

        String filter = "";
        if (!from.isEmpty())  filter += " AND source LIKE '%" + from + "%'";
        if (!to.isEmpty())    filter += " AND destination LIKE '%" + to + "%'";
        if (date != null)     filter += " AND flight_date = '" + date + "'";
        loadFlights(filter);
    }

    @FXML private void handleShowAll(ActionEvent e) {
        fromField.clear(); toField.clear(); datePicker.setValue(null);
        loadFlights("");
    }

    @FXML
    private void handleBookFlight(ActionEvent event) {
        FlightSearch selected = flightsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            resultLabel.setText("⚠ Please select a flight first!");
            return;
        }
        if (selected.getAvailableSeats() == 0) {
            resultLabel.setText("❌ No seats available on this flight!");
            return;
        }
        // Pass selected flight to booking page
        Session.selectedFlight = selected;
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("book_flight.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
            stage.setTitle("Book Flight");
        } catch (Exception e) { e.printStackTrace(); }
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