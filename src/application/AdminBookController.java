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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminBookController implements Initializable {

    @FXML private TableView<FlightSearch> flightsTable;
    @FXML private TableColumn<FlightSearch,String>  colNo, colFrom, colTo, colDate;
    @FXML private TableColumn<FlightSearch,Double>  colEconomy;
    @FXML private TableColumn<FlightSearch,Integer> colSeats;
    @FXML private TextField passengerField;
    @FXML private ComboBox<String> classCombo, seatCombo;
    @FXML private Label originalPriceLabel, finalPriceLabel, message;

    private double originalPrice  = 0;
    private static final double ADMIN_DISCOUNT = 85.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNo     .setCellValueFactory(new PropertyValueFactory<>("flightNo"));
        colFrom   .setCellValueFactory(new PropertyValueFactory<>("source"));
        colTo     .setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate   .setCellValueFactory(new PropertyValueFactory<>("flightDate"));
        colEconomy.setCellValueFactory(new PropertyValueFactory<>("economyPrice"));
        colSeats  .setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        classCombo.setItems(FXCollections.observableArrayList(
            "Economy", "Business", "First"));
        loadFlights();
    }

    private void loadFlights() {
        ObservableList<FlightSearch> list = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM flights WHERE status='scheduled' " +
                "ORDER BY flight_date");
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

    @FXML
    private void handleClassSelected(ActionEvent e) {
        FlightSearch selected =
            flightsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            message.setStyle("-fx-text-fill: red;");
            message.setText("⚠ Select a flight first!"); return;
        }
        String cls = classCombo.getValue();
        if (cls == null) return;
        switch (cls) {
            case "Economy":  originalPrice = selected.getEconomyPrice();  break;
            case "Business": originalPrice = selected.getBusinessPrice(); break;
            case "First":    originalPrice = selected.getFirstPrice();    break;
        }
        double finalPrice = originalPrice * (1 - ADMIN_DISCOUNT / 100);
        originalPriceLabel.setText("৳ " + String.format("%.0f", originalPrice));
        finalPriceLabel   .setText("৳ " + String.format("%.0f", finalPrice));
        loadSeats(selected.getId(), cls);
    }

    private void loadSeats(int flightId, String seatClass) {
        List<String> seats = new ArrayList<>();
        String prefix = seatClass.equals("Economy") ? "E"
                      : seatClass.equals("Business") ? "B" : "F";
        int count = seatClass.equals("Economy") ? 80
                  : seatClass.equals("Business") ? 40 : 20;
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT seat_number FROM bookings " +
                "WHERE flight_id=? AND seat_class=? AND status='confirmed'");
            ps.setInt(1, flightId); ps.setString(2, seatClass);
            ResultSet rs = ps.executeQuery();
            List<String> booked = new ArrayList<>();
            while (rs.next()) booked.add(rs.getString("seat_number"));
            for (int i = 1; i <= count; i++) {
                String seat = prefix + i;
                if (!booked.contains(seat)) seats.add(seat);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        seatCombo.setItems(FXCollections.observableArrayList(seats));
        if (!seats.isEmpty()) seatCombo.setValue(seats.get(0));
    }

    
   
    @FXML
    private void handleConfirmBooking(ActionEvent event) {
        FlightSearch selected = flightsTable.getSelectionModel().getSelectedItem();
        String passenger = passengerField.getText().trim();
        String cls       = classCombo.getValue();
        String seat      = seatCombo.getValue();

        if (selected == null || passenger.isEmpty()
                || cls == null || seat == null) {
            message.setStyle("-fx-text-fill: red;");
            message.setText("⚠ Please fill all fields!"); return;
        }

        // Save to session like user flow does
        Session.selectedFlight  = selected;
        Session.passengerName   = passenger;
        Session.selectedSeat    = seat;
        Session.selectedClass   = cls;
        Session.originalPrice   = originalPrice;
        Session.discountPercent = ADMIN_DISCOUNT;
        Session.finalPrice      = originalPrice * (1 - ADMIN_DISCOUNT / 100);

        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("payment.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
            stage.setTitle("Payment");
        } catch (Exception e) { e.printStackTrace(); }
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