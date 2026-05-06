package application;

import javafx.beans.property.SimpleIntegerProperty;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Label message;
    @FXML private TableView<ObservableList<String>> usersTable;
    @FXML private TableColumn<ObservableList<String>,String> colId, colName,
                              colEmail, colUsername, colRole, colCreated;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumns();
        loadUsers("");
    }

    private void setupColumns() {
        colId      .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        colName    .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colEmail   .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        colUsername.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));
        colRole    .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(4)));
        colCreated .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(5)));
    }

    private void loadUsers(String filter) {
        ObservableList<ObservableList<String>> list =
            FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, full_name, email, username, role, " +
                         "created_at FROM users WHERE role='user'" + filter;
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("id")));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("username"));
                row.add(rs.getString("role"));
                row.add(rs.getString("created_at"));
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        usersTable.setItems(list);
    }

    @FXML
    private void handleSearch(ActionEvent e) {
        String s = searchField.getText().trim();
        if (!s.isEmpty())
            loadUsers(" AND (username LIKE '%" + s + "%' " +
                      "OR email LIKE '%" + s + "%')");
    }

    @FXML private void handleShowAll(ActionEvent e) {
        searchField.clear(); loadUsers("");
    }

    @FXML
    private void handleDeleteUser(ActionEvent event) {
        ObservableList<String> selected =
            usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            message.setStyle("-fx-text-fill: red;");
            message.setText("⚠ Select a user first!"); return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.initOwner(((Node) event.getSource()).getScene().getWindow());
        confirm.setContentText("Delete user " + selected.get(3) + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM users WHERE id=?");
                ps.setInt(1, Integer.parseInt(selected.get(0)));
                ps.executeUpdate();
                message.setStyle("-fx-text-fill: green;");
                message.setText("✅ User deleted!");
                loadUsers("");
            } catch (Exception e) {
                message.setStyle("-fx-text-fill: red;");
                message.setText("❌ Cannot delete — has bookings!");
            }
        }
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