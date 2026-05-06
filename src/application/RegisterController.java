package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private TextField fullNameField, emailField, usernameField;
    @FXML private PasswordField passwordField, confirmPasswordField, adminKeyField;
    @FXML private RadioButton customerRadio, staffRadio, adminRadio;
    @FXML private RadioButton pilotRadio, crewRadio;
    @FXML private VBox staffCategoryBox, adminKeyBox;
    @FXML private Label messageLabel;

    // Admin secret key — change this to whatever you want
    private static final String ADMIN_SECRET = "ADMIN2024";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Group main role radios
        ToggleGroup roleGroup = new ToggleGroup();
        customerRadio.setToggleGroup(roleGroup);
        staffRadio   .setToggleGroup(roleGroup);
        adminRadio   .setToggleGroup(roleGroup);
        customerRadio.setSelected(true);

        // Group staff category radios
        ToggleGroup staffGroup = new ToggleGroup();
        pilotRadio.setToggleGroup(staffGroup);
        crewRadio .setToggleGroup(staffGroup);
        pilotRadio.setSelected(true);
    }

    @FXML
    private void handleRoleChange(ActionEvent e) {
        // Hide all extra boxes first
        staffCategoryBox.setVisible(false); staffCategoryBox.setManaged(false);
        adminKeyBox     .setVisible(false); adminKeyBox     .setManaged(false);

        if (staffRadio.isSelected()) {
            staffCategoryBox.setVisible(true); staffCategoryBox.setManaged(true);
        } else if (adminRadio.isSelected()) {
            adminKeyBox.setVisible(true); adminKeyBox.setManaged(true);
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String fullName = fullNameField.getText().trim();
        String email    = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm  = confirmPasswordField.getText().trim();

        // Validation
        if (fullName.isEmpty() || email.isEmpty()
                || username.isEmpty() || password.isEmpty()) {
            setMsg("⚠ All fields are required!", "red"); return;
        }
        if (!password.equals(confirm)) {
            setMsg("⚠ Passwords do not match!", "red"); return;
        }
        if (password.length() < 6) {
            setMsg("⚠ Password must be at least 6 characters!", "red"); return;
        }

        // Determine role
        String role = "user";
        String staffCategory = null;

        if (staffRadio.isSelected()) {
            role = "staff";
            staffCategory = pilotRadio.isSelected() ? "pilot" : "crew";
        } else if (adminRadio.isSelected()) {
            // Verify admin key
            String key = adminKeyField.getText().trim();
            if (!key.equals(ADMIN_SECRET)) {
                setMsg("❌ Invalid admin secret key!", "red"); return;
            }
            role = "admin";
        }

        // Save to database
        try (Connection conn = DBConnection.getConnection()) {
            // Insert user
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (full_name, email, username, password, role) " +
                "VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, role);
            ps.executeUpdate();

            // Get generated user ID
            ResultSet keys = ps.getGeneratedKeys();
            int userId = 0;
            if (keys.next()) userId = keys.getInt(1);

            // If staff or admin, add to employees table
            if (role.equals("staff") || role.equals("admin")) {
                String empId = "EMP-" + role.toUpperCase() + "-"
                             + System.currentTimeMillis() % 10000;
                PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO employees (employee_id, user_id, role, " +
                    "staff_category) VALUES (?,?,?,?)");
                ps2.setString(1, empId);
                ps2.setInt   (2, userId);
                ps2.setString(3, role);
                ps2.setString(4, staffCategory); // null for admin
                ps2.executeUpdate();
            }

            setMsg("✅ Registered successfully! Please login.", "green");

        } catch (SQLIntegrityConstraintViolationException e) {
            setMsg("❌ Username or email already exists!", "red");
        } catch (Exception e) {
            e.printStackTrace();
            setMsg("❌ Error: " + e.getMessage(), "red");
        }
    }

    private void setMsg(String msg, String color) {
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setText(msg);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(
                getClass().getResource("login.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        } catch (Exception e) { e.printStackTrace(); }
    }
}