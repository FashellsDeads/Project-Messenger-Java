package client;

import com.messenger.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.List;

public class ChatController implements NetworkListener {

    @FXML private ListView<MessengerServer> serverList;
    @FXML private ListView<Channel> channelsList;
    @FXML private ListView<AbstractMessage> messagesList;
    @FXML private TextField messageInput;
    @FXML private Label currentServerLabel;

    private User currentUser;
    private Channel currentChannel;

    private final ObservableList<String> chatMessages = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        serverList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MessengerServer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        channelsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Channel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "# " + item.getName());
            }
        });

        // Слушатель выбора канала
        channelsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentChannel = newVal;
                loadChannelHistory(newVal);
            }
        });
    }

    public void setUser(User user) {
        this.currentUser = user;
        System.out.println("Чат запущен для: " + user.getUsername());
    }

    @FXML
    private void handleSendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty() || currentChannel == null) return;

        // Message msg = new Message(currentUser.getUsername(), text, currentChannel.getId());

        // JavaFXClientLauncher.networkClient.sendMessage(msg);

        messageInput.clear();
        Platform.runLater(() -> {
            System.out.println("Отправлено: " + text);
        });
    }

    private void loadChannelHistory(Channel channel) {
        // Здесь будет запрос к серверу: networkClient.getHistory(channel.getId());
        System.out.println("Загрузка истории для канала: " + channel.getName());
    }

    @Override
    public void onMessageReceived(AbstractMessage message) {
        Platform.runLater(() -> {
            // Добавляем сообщение в список
            messagesList.getItems().add(message);
            messagesList.scrollTo(message); // Прокрутка вниз
        });
    }

    @Override
    public void onChannelsListReceived(List<Channel> channels) {
        Platform.runLater(() -> {
            channelsList.setItems(FXCollections.observableArrayList(channels));
        });
    }

    @Override
    public void onServersListReceived(List<MessengerServer> servers) {
        Platform.runLater(() -> {
            serverList.setItems(FXCollections.observableArrayList(servers));
        });
    }

    @Override
    public void onLoginSuccess(User user) { /* Уже обработано в MainController */ }

    @Override
    public void onError(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, msg);
            alert.showAndWait();
        });
    }

    @Override public void onChannelHistoryReceived(List<AbstractMessage> history) {
        Platform.runLater(() -> messagesList.setItems(FXCollections.observableArrayList(history)));
    }

    @Override public void onDisconnected(String reason) {
        Platform.runLater(() -> System.out.println("Отключено: " + reason));
    }

    //РОМА ТУТ НОВЫЙ МЕТОД НАДО ИМПЛЕМЕНТИРОВАТЬ КАРОЧЕ РАБОТАй
    @Override
    public void onCommandResponse(CommandResponse payload) {
        throw new UnsupportedOperationException(
                "COMMAND_RESPONSE received but no handler implemented"
        );
    }

    @FXML
    private void handleAddServer() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Подключение к серверу");
        dialog.setHeaderText("Введите адрес нового сервера");
        dialog.setContentText("IP адрес:");

        dialog.showAndWait().ifPresent(ip -> {
            System.out.println("Попытка подключения к: " + ip);

            // JavaFXClientLauncher.networkClient.connectToNewServer(ip);

            currentServerLabel.setText("Подключение к " + ip + "...");
        });
    }
}