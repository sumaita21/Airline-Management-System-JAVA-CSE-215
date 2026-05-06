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

public class StaffFlightsController implements Initializable {

    @FXML private TextField searchFrom, searchTo;
    @FXML private TableView<FlightSearch> flightsTable;
    @FXML private TableColumn<FlightSearch,String>  colNo, colFrom, colTo,
                              colDate, colTime;
    @FXML private TableColumn<FlightSearch,Double>  colEconomy,
                              colBusiness, colFirst;
    @FXML private TableColumn<FlightSearch,Integer> colSeats;

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
        loadFlights("");
    }

    private void loadFlights(String filter) {
        ObservableList<FlightSearch> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM flights WHERE status='scheduled'" + filter);
            while (rs.next()) {
                list.add(new FlightSearch(
                    rs.getInt("id"), rs.getString("flight_no"),
                    rs.getString("source"), rs.getString("destination"),
                    rs.getString("flight_date"), rs.getString("flight_time"),
                    rs.getDouble("economy_price"), rs.getDouble("business_price"),
                    rs.getDouble("first_price"), rs.getInt("available_seats")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        flightsTable.setItems(list);
    }

    @FXML private void handleSearch(ActionEvent e) {
        String from = searchFrom.getText().trim();
        String to   = searchTo.getText().trim();
        String f = "";
        if (!from.isEmpty()) f += " AND source LIKE '%" + from + "%'";
        if (!to.isEmpty())   f += " AND destination LIKE '%" + to + "%'";
        loadFlights(f);
    }

    @FXML private void handleShowAll(ActionEvent e) {
        searchFrom.clear(); searchTo.clear(); loadFlights("");
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