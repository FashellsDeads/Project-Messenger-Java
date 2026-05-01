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

    // Элементы интерфейса из main.fxml
    @FXML private ListView<MessengerServer> serverList;
    @FXML private ListView<Channel> channelsList;
    @FXML private ListView<AbstractMessage> messagesList;
    @FXML private TextField messageInput;
    @FXML private Label currentServerLabel;

    private User currentUser;
    private Channel currentChannel;

    // Списки данных, которые автоматически обновляют UI
    private final ObservableList<String> chatMessages = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Настраиваем отображение серверов (чтобы видеть название, а не адрес объекта)
        serverList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MessengerServer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        // Настраиваем отображение каналов
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

    // Метод для получения данных пользователя при переходе с экрана логина
    public void setUser(User user) {
        this.currentUser = user;
        System.out.println("Чат запущен для: " + user.getUsername());
    }

    @FXML
    private void handleSendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty() || currentChannel == null) return;

        // Создаем сообщение (используя твою модель)
        // Здесь можно использовать текстовое сообщение, наследуемое от AbstractMessage
        // Message msg = new Message(currentUser.getUsername(), text, currentChannel.getId());

        // Отправка через сетевой клиент (раскомментируй, когда подключишь сокеты)
        // JavaFXClientLauncher.networkClient.sendMessage(msg);

        messageInput.clear();

        // Временная визуализация (пока нет сервера)
        Platform.runLater(() -> {
            // В реальной версии мы будем ждать подтверждения от сервера
            System.out.println("Отправлено: " + text);
        });
    }

    private void loadChannelHistory(Channel channel) {
        // Здесь будет запрос к серверу: networkClient.getHistory(channel.getId());
        System.out.println("Загрузка истории для канала: " + channel.getName());
    }

    // --- Реализация NetworkListener ---
    // Все изменения UI должны быть внутри Platform.runLater, так как сетевой поток - не поток JavaFX!

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
        // Создаем простое диалоговое окно для ввода данных
        TextInputDialog dialog = new TextInputDialog("127.0.0.1");
        dialog.setTitle("Подключение к серверу");
        dialog.setHeaderText("Введите адрес нового сервера");
        dialog.setContentText("IP адрес:");

        dialog.showAndWait().ifPresent(ip -> {
            System.out.println("Попытка подключения к: " + ip);

            // В будущем здесь будет вызов сетевого клиента:
            // JavaFXClientLauncher.networkClient.connectToNewServer(ip);

            // Пока просто выведем статус для теста
            currentServerLabel.setText("Подключение к " + ip + "...");
        });
    }
}