package client;

import com.messenger.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class MainController implements NetworkListener {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField; // Новое поле
    @FXML private Label statusLabel;
    @FXML private Button mainActionBtn;
    @FXML private Button switchModeBtn;

    private boolean isRegistrationMode = false;

    @FXML
    public void initialize() {
        if (emailField != null) {
            emailField.setVisible(false);
            emailField.setManaged(false);
        }
    }

    @FXML
    private void handleMainAction() {
        String login = loginField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();

        if (login.isEmpty() || password.isEmpty() || (isRegistrationMode && email.isEmpty())) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Ошибка: заполните все поля!");
            return;
        }

        if (isRegistrationMode) {
            // Используем твой конструктор: User(String username, String email, String passwordHash)
            User newUser = new User(login, email, password);

            statusLabel.setText("Регистрация...");
            // ЗАКОММЕНТИРОВАНО:
            // JavaFXClientLauncher.networkClient.register(newUser);

            // Имитация успеха:
            onLoginSuccess(newUser);
        } else {
            statusLabel.setText("Вход...");
            // ЗАКОММЕНТИРОВАНО:
            // JavaFXClientLauncher.networkClient.login(login, password);

            // Имитация успеха (для теста введи admin):
            if (login.equalsIgnoreCase("admin")) {
                onLoginSuccess(new User(1, login, "admin@messenger.com"));
            } else {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Пользователь не найден");
            }
        }
    }

    @FXML
    private void toggleMode() {
        isRegistrationMode = !isRegistrationMode;

        // Включаем/выключаем видимость и резервирование места для поля почты
        emailField.setVisible(isRegistrationMode);
        emailField.setManaged(isRegistrationMode);

        if (isRegistrationMode) {
            mainActionBtn.setText("Создать аккаунт");
            switchModeBtn.setText("Уже есть аккаунт? Войти");
            statusLabel.setText("Регистрация нового пользователя");
        } else {
            mainActionBtn.setText("Войти");
            switchModeBtn.setText("Нет аккаунта? Регистрация");
            statusLabel.setText("");
        }

        // Автоматическая подгонка размера окна под новое содержимое (опционально)
        mainActionBtn.getScene().getWindow().sizeToScene();
    }

    @Override
    public void onLoginSuccess(User user) {
        Platform.runLater(() -> {
            System.out.println("Вход выполнен: " + user.getUsername());
            navigateToMainChat(user);
        });
    }

    private void navigateToMainChat(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();

            // Переход на главную сцену
            Stage stage = (Stage) mainActionBtn.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);

            stage.setScene(scene);
            stage.setTitle("Messenger - " + user.getUsername());
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            if (statusLabel != null){
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Ошибка перехода: " + e.getMessage());
         }
        }
    }

    // Методы интерфейса (пока пустые)
    @Override public void onError(String msg) { Platform.runLater(() -> statusLabel.setText(msg)); }
    @Override public void onMessageReceived(AbstractMessage m) {}
    @Override public void onChannelHistoryReceived(List<AbstractMessage> h) {}
    @Override public void onServersListReceived(List<MessengerServer> s) {}
    @Override public void onChannelsListReceived(List<Channel> c) {}
    @Override public void onDisconnected(String r) {}
}
