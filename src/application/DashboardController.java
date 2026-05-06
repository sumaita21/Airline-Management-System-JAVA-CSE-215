package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private TableView<Flight> flightsTable;
    @FXML private TableColumn<Flight, String> colFlightNo;
    @FXML private TableColumn<Flight, String> colFrom;
    @FXML private TableColumn<Flight, String> colTo;
    @FXML private TableColumn<Flight, String> colDate;
    @FXML private TableColumn<Flight, String> colTime;
    @FXML private TableColumn<Flight, Integer> colSeats;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up table columns
        colFlightNo.setCellValueFactory(new PropertyValueFactory<>("flightNo"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("from"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("to"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colSeats.setCellValueFactory(new PropertyValueFactory<>("seats"));

        // Load dummy flight data (will connect to MySQL later)
        loadFlights();
    }

    private void loadFlights() {
        ObservableList<Flight> flights = FXCollections.observableArrayList(
            new Flight("BD-101", "Dhaka",   "Dubai",    "2026-05-10", "08:00", 45),
            new Flight("BD-202", "Dhaka",   "London",   "2026-05-11", "14:30", 30),
            new Flight("BD-303", "Dhaka",   "New York", "2026-05-12", "22:00", 12),
            new Flight("BD-404", "Chittagong", "Dubai", "2026-05-13", "09:15", 55),
            new Flight("BD-505", "Dhaka",   "Toronto",  "2026-05-14", "17:45", 8)
        );
        flightsTable.setItems(flights);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
            stage.setTitle("Airline Management System - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}