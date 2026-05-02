package client;

import com.messenger.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ChatController implements NetworkListener {

    @FXML private ListView<AbstractMessage> messagesList;
    @FXML private TextField messageInput;
    @FXML private Label currentServerLabel;
    @FXML private ListView<ChatInfo> chatsList;

    private ChatInfo currentChat;
    private User currentUser;

    // ===== ЕДИНЫЙ ИСТОЧНИК ДАННЫХ =====
    private final ObservableList<AbstractMessage> messages = FXCollections.observableArrayList();
    private final ObservableList<ChatInfo> chats = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // привязываем один раз
        messagesList.setItems(messages);
        chatsList.setItems(chats);

        chatsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ChatInfo item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatChat(item));
            }
        });

        messagesList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(AbstractMessage item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatMessage(item));
            }
        });

        chatsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentChat = newVal;
                loadChatHistory(newVal);
            }
        });
    }

    private String formatMessage(AbstractMessage msg) {
        return msg.getSenderUsername() + ": " + msg.getDisplayContent();
    }

    private String formatChat(ChatInfo chat) {
        return switch (chat.getType()) {
            case "PRIVATE" -> "💬 " + chat.getName();
            case "CHANNEL" -> "# " + chat.getName();
            case "SELF" -> "🧍 Self";
            default -> chat.getName();
        };
    }

    private void loadChatHistory(ChatInfo chat) {
        JavaFXClientLauncher.networkClient
                .commands()
                .getHistory(chat.getId());
    }

    public void Init() {
        System.out.println("INITED");
        JavaFXClientLauncher.networkClient.commands().getMyChats();
    }

    public void setUser(User user) {
        this.currentUser = user;
        System.out.println("Чат запущен для: " + user.getUsername());
    }

    // ===== SEND MESSAGE =====
    @FXML
    private void handleSendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty() || currentChat == null) return;

        AbstractMessage msg = new TextMessage(
                currentChat.getId(),
                currentUser.getId(),
                currentUser.getUsername(),
                text
        );

        JavaFXClientLauncher.networkClient.sendMessage(msg);

        messageInput.clear();
    }

    // ===== NETWORK EVENTS =====

    @Override
    public void onMessageReceived(AbstractMessage message) {
        System.out.println("onMessageReceived: " + message);

        Platform.runLater(() -> {
            messages.add(message);
            messagesList.scrollTo(message);
        });
    }

    @Override
    public void onChannelHistoryReceived(List<AbstractMessage> history) {
        Platform.runLater(() -> {
            messages.setAll(history); // 🔥 вместо setItems()
        });
    }

    @Override
    public void onCommandResponse(CommandResponse payload) {

        System.out.println(payload.getMessage());

        if (!payload.isSuccess()) return;

        Object data = payload.getPayload();

        if (data instanceof List<?> list) {

            if (list.isEmpty()) {
                Platform.runLater(messages::clear);
                return;
            }

            Object first = list.get(0);

            if (first instanceof ChatInfo) {

                List<ChatInfo> result = (List<ChatInfo>) list;

                Platform.runLater(() -> chats.setAll(result));
            }

            else if (first instanceof AbstractMessage) {

                List<AbstractMessage> history = (List<AbstractMessage>) list;

                Platform.runLater(() -> messages.setAll(history));
            }

            else {
                System.out.println("Unknown type: " + first.getClass());
            }
        }
    }

    @Override
    public void onError(String msg) {
        Platform.runLater(() ->
                new Alert(Alert.AlertType.ERROR, msg).showAndWait()
        );
    }

    @Override public void onChannelsListReceived(List<Channel> channels) {}
    @Override public void onServersListReceived(List<MessengerServer> servers) {}
    @Override public void onLoginSuccess(User user) {}
    @Override public void onDisconnected(String reason) {
        Platform.runLater(() -> System.out.println("Отключено: " + reason));
    }

    // ===== CREATE CHAT =====
    @FXML
    private void handleCreateChat() {

        ChoiceDialog<String> typeDialog =
                new ChoiceDialog<>("PRIVATE", "PRIVATE", "CHANNEL");

        typeDialog.setTitle("Создание");
        typeDialog.setHeaderText("Выберите тип чата");

        typeDialog.showAndWait().ifPresent(type -> {

            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("Создание " + type);

            inputDialog.setHeaderText(type.equals("PRIVATE")
                    ? "Введите username пользователя"
                    : "Введите имя канала");

            inputDialog.showAndWait().ifPresent(input -> {
                if (input.isBlank()) return;

                if (type.equals("PRIVATE")) {
                    JavaFXClientLauncher.networkClient.commands().createPrivateChat(input);
                } else {
                    JavaFXClientLauncher.networkClient.commands().createChannel(input);
                }
            });
        });
    }
}