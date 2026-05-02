package client;

import com.messenger.model.*;
import com.messenger.protocol.Packet;
import com.messenger.protocol.PacketType;
import com.messenger.protocol.RegisterRequest;
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
    @FXML private TextField emailField;
    @FXML private Label statusLabel;
    @FXML private Label emailLabel;
    @FXML private Button mainActionBtn;
    @FXML private Button switchModeBtn;

    private boolean isRegistrationMode = false;

    @FXML
    public void initialize() {
        JavaFXClientLauncher.networkClient.setListener(this);
        if (emailField != null) {
            emailLabel.setVisible(false);
            emailLabel.setManaged(false);
            emailField.setVisible(false);
            emailField.setManaged(false);
        }
    }

    @FXML
    private void handleMainAction() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String email = isRegistrationMode ? emailField.getText().trim() : "";

        if (login.isEmpty() || password.isEmpty() || (isRegistrationMode && email.isEmpty())) {
            showStatus("Ошибка: заполните все поля!", true);
            return;
        }

        mainActionBtn.setDisable(true);
        showStatus(isRegistrationMode ? "Регистрация..." : "Вход...", false);

        if (isRegistrationMode) {
            RegisterRequest regReq = new RegisterRequest(login, email, password);
            JavaFXClientLauncher.networkClient.sendPacket(new Packet<>(PacketType.REGISTER_REQUEST, regReq));
        } else {
            JavaFXClientLauncher.networkClient.login(login, password);
        }
    }

    @FXML
    private void toggleMode() {
        isRegistrationMode = !isRegistrationMode;
        emailField.setVisible(isRegistrationMode);
        emailField.setManaged(isRegistrationMode);
        emailLabel.setVisible(isRegistrationMode);
        emailLabel.setManaged(isRegistrationMode);

        if (isRegistrationMode) {
            mainActionBtn.setText("Создать аккаунт");
            switchModeBtn.setText("Уже есть аккаунт? Войти");
            loginField.setPromptText("Имя пользователя");
            emailField.setPromptText("Email");
        } else {
            mainActionBtn.setText("Войти");
            switchModeBtn.setText("Нет аккаунта? Регистрация");
            loginField.setPromptText("Email");
        }
        statusLabel.setText("");
        mainActionBtn.getScene().getWindow().sizeToScene();
    }

    @Override
    public void onLoginSuccess(User user) {
        System.out.println("Авторизация успешна: " + user.getUsername());
        navigateToMainChat(user);
    }

    @Override
    public void onError(String msg) {
        mainActionBtn.setDisable(false);
        showStatus("Ошибка: " + msg, true);
    }

    @Override
    public void onDisconnected(String reason) {
        mainActionBtn.setDisable(false);
        showStatus("Отключено: " + reason, true);
    }

    @Override
    public void onCommandResponse(CommandResponse payload) {

    }

    private void showStatus(String text, boolean isError) {
        statusLabel.setText(text);
        statusLabel.setStyle(isError ? "-fx-text-fill: #ff5555;" : "-fx-text-fill: white;");
    }

    private void navigateToMainChat(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();

            ChatController chatController = loader.getController();
            chatController.setUser(user);

            Stage stage = (Stage) mainActionBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setTitle("Messenger - " + user.getUsername());
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Критическая ошибка UI: " + e.getMessage(), true);
        }
    }

    @Override public void onMessageReceived(AbstractMessage m) {}
    @Override public void onChannelHistoryReceived(List<AbstractMessage> h) {}
    @Override public void onServersListReceived(List<MessengerServer> s) {}
    @Override public void onChannelsListReceived(List<Channel> c) {}
}