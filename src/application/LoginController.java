package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.sql.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("id");
                String fullName = rs.getString("full_name");

                // Store session info
                Session.userId = userId;
                Session.username = username;
                Session.fullName = fullName;
                Session.role = role;

                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();

                String fxml;
                if (role.equals("admin"))       fxml = "admin_dashboard.fxml";
                else if (role.equals("staff"))  fxml = "staff_dashboard.fxml";
                else                            fxml = "user_dashboard.fxml";

                AnchorPane root = FXMLLoader.load(getClass().getResource(fxml));
                stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
                stage.setTitle("✈ Airline Management - " + role.toUpperCase());

            } else {
                errorLabel.setText("❌ Invalid username or password!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Database error!");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(getClass().getResource("register.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
            stage.setTitle("Register");
        } catch (Exception e) { e.printStackTrace(); }
    }
}