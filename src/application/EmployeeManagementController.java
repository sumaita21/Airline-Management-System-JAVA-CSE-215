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
import java.util.Optional;
import java.util.ResourceBundle;

public class EmployeeManagementController implements Initializable {

    @FXML private TextField empUsernameField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label formMessage;
    @FXML private TableView<ObservableList<String>> employeesTable;
    @FXML private TableColumn<ObservableList<String>,String> colEmpId,
                              colEmpName, colEmpUser, colEmpRole;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleCombo.setItems(FXCollections.observableArrayList("staff", "admin"));
        colEmpId  .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        colEmpName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colEmpUser.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        colEmpRole.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));
        loadEmployees();
    }

    private void loadEmployees() {
        ObservableList<ObservableList<String>> list =
            FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT e.employee_id, u.full_name, u.username, e.role " +
                "FROM employees e JOIN users u ON e.user_id = u.id");
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rs.getString("employee_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("username"));
                row.add(rs.getString("role"));
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        employeesTable.setItems(list);
    }

    @FXML
    private void handleAddEmployee(ActionEvent e) {
        String username = empUsernameField.getText().trim();
        String role     = roleCombo.getValue();
        if (username.isEmpty() || role == null) {
            setMsg("⚠ Fill all fields!", "red"); return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            // Get user id
            PreparedStatement ps1 = conn.prepareStatement(
                "SELECT id FROM users WHERE username=?");
            ps1.setString(1, username);
            ResultSet rs = ps1.executeQuery();
            if (!rs.next()) {
                setMsg("❌ User not found!", "red"); return;
            }
            int userId = rs.getInt("id");
            String empId = "EMP-" + role.toUpperCase() + "-"
                         + System.currentTimeMillis() % 1000;

            PreparedStatement ps2 = conn.prepareStatement(
                "INSERT INTO employees (employee_id, user_id, role) VALUES (?,?,?)");
            ps2.setString(1, empId);
            ps2.setInt(2, userId);
            ps2.setString(3, role);
            ps2.executeUpdate();

            // Update user role
            PreparedStatement ps3 = conn.prepareStatement(
                "UPDATE users SET role=? WHERE id=?");
            ps3.setString(1, role);
            ps3.setInt(2, userId);
            ps3.executeUpdate();

            setMsg("✅ Employee added! ID: " + empId, "green");
            empUsernameField.clear();
            loadEmployees();
        } catch (Exception ex) {
            setMsg("❌ Error: " + ex.getMessage(), "red");
        }
    }

    @FXML
    private void handleRemoveEmployee(ActionEvent event) {
        ObservableList<String> selected =
            employeesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setMsg("⚠ Select an employee first!", "red"); return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.initOwner(((Node) event.getSource()).getScene().getWindow());
        confirm.setContentText("Remove employee " + selected.get(0) + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM employees WHERE employee_id=?");
                ps.setString(1, selected.get(0));
                ps.executeUpdate();
                setMsg("✅ Employee removed!", "green");
                loadEmployees();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void setMsg(String msg, String color) {
        formMessage.setStyle("-fx-text-fill: " + color + ";");
        formMessage.setText(msg);
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