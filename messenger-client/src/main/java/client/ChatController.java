package client;

import com.messenger.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.StageStyle;

import java.util.List;

public class ChatController implements NetworkListener {

    @FXML private ListView<AbstractMessage> messagesList;
    @FXML private TextField messageInput;
    @FXML private Label currentChannelLabel;
    @FXML private ListView<ChatInfo> chatsList;

    private ChatInfo currentChat;
    private User currentUser;

    private final ObservableList<AbstractMessage> messages = FXCollections.observableArrayList();
    private final ObservableList<ChatInfo> chats = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        messagesList.setItems(messages);
        chatsList.setItems(chats);

        chatsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ChatInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(formatChat(item));
                    setStyle("-fx-text-fill: #DCDDDE; -fx-background-color: transparent; -fx-padding: 6 12 6 12;");
                }
            }
        });

        messagesList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(AbstractMessage item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(formatMessage(item));
                    setStyle("-fx-text-fill: #DCDDDE; -fx-background-color: transparent; -fx-padding: 4 16 4 16;");
                }
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
        JavaFXClientLauncher.networkClient.commands().getMyChats();
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

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

    @Override
    public void onMessageReceived(AbstractMessage message) {
        if (currentChat == null || message.getChatId() != currentChat.getId()) return;

        Platform.runLater(() -> {
            messages.add(message);
            messagesList.scrollTo(message);
        });
    }

    @Override
    public void onChannelHistoryReceived(List<AbstractMessage> history) {
        Platform.runLater(() -> messages.setAll(history));
    }

    @Override
    public void onCommandResponse(CommandResponse payload) {
        if (!payload.isSuccess()) {
            onError(payload.getMessage());
            return;
        }

        Object data = payload.getPayload();
        int responseChatId = payload.getChatId();

        if (data instanceof List<?> list) {
            if (list.isEmpty()) {
                if (responseChatId == -1) {
                    Platform.runLater(chats::clear);
                } else {
                    Platform.runLater(messages::clear);
                }
                return;
            }

            Object first = list.get(0);

            if (first instanceof ChatInfo) {
                List<ChatInfo> result = (List<ChatInfo>) list;
                Platform.runLater(() -> chats.setAll(result));
            } else if (first instanceof AbstractMessage) {
                List<AbstractMessage> history = (List<AbstractMessage>) list;
                Platform.runLater(() -> messages.setAll(history));
            }
        } else if (data == null) {
            JavaFXClientLauncher.networkClient.commands().getMyChats();
        }
    }

    @Override
    public void onServerEvent(ServerEvent event) {
        switch (event.getType()) {
            case PRIVATE_CHAT_INVITE -> Platform.runLater(() ->
                    JavaFXClientLauncher.networkClient.commands().getMyChats()
            );
            case USER_JOINED_CHANNEL, USER_ONLINE, USER_OFFLINE -> Platform.runLater(() ->
                    System.out.println("[Event] " + event.getMessage())
            );
        }
    }

    @Override
    public void onError(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg).showAndWait());
    }

    @Override public void onChannelsListReceived(List<Channel> channels) {}
    @Override public void onServersListReceived(List<MessengerServer> servers) {}
    @Override public void onLoginSuccess(User user) {}
    @Override public void onDisconnected(String reason) {
        Platform.runLater(() -> System.out.println("Отключено: " + reason));
    }

    @FXML
    private void handleCreateChat() {
        showCreateChatDialog();
    }

    private void showCreateChatDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Создать");

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #2F3136; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 20, 0, 0, 4);");

        VBox header = new VBox(6);
        header.setPadding(new Insets(24, 24, 16, 24));

        Label title = new Label("Создать");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label subtitle = new Label("Выберите тип: приватный чат или канал");
        subtitle.setStyle("-fx-text-fill: #B9BBBE; -fx-font-size: 13px;");

        header.getChildren().addAll(title, subtitle);

        ToggleGroup typeGroup = new ToggleGroup();

        HBox typeRow = new HBox(12);
        typeRow.setPadding(new Insets(0, 24, 16, 24));

        ToggleButton btnPrivate = buildTypeButton("💬 Приватный чат", typeGroup);
        ToggleButton btnChannel = buildTypeButton("# Канал", typeGroup);
        btnPrivate.setSelected(true);

        typeRow.getChildren().addAll(btnPrivate, btnChannel);

        VBox inputArea = new VBox(8);
        inputArea.setPadding(new Insets(0, 24, 8, 24));

        Label inputLabel = new Label("ИМЯ ПОЛЬЗОВАТЕЛЯ");
        inputLabel.setStyle("-fx-text-fill: #B9BBBE; -fx-font-size: 11px; -fx-font-weight: bold;");

        TextField inputField = new TextField();
        inputField.setPromptText("Введите имя пользователя...");
        inputField.setStyle("-fx-background-color: #202225; -fx-text-fill: white; -fx-prompt-text-fill: #72767D; -fx-background-radius: 4; -fx-padding: 10;");

        inputArea.getChildren().addAll(inputLabel, inputField);

        typeGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == btnPrivate) {
                inputLabel.setText("ИМЯ ПОЛЬЗОВАТЕЛЯ");
                inputField.setPromptText("Введите имя пользователя...");
            } else {
                inputLabel.setText("НАЗВАНИЕ КАНАЛА");
                inputField.setPromptText("Введите название канала...");
            }
        });

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(16, 24, 24, 24));

        Button cancelBtn = new Button("Отмена");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 10 20 10 20;");
        cancelBtn.setOnAction(e -> dialog.close());

        Button createBtn = new Button("Создать");
        createBtn.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 10 20 10 20;");

        createBtn.setOnAction(e -> {
            String input = inputField.getText().trim();
            if (input.isBlank()) return;

            boolean isPrivate = typeGroup.getSelectedToggle() == btnPrivate;

            if (isPrivate) {
                JavaFXClientLauncher.networkClient.commands().createPrivateChat(input);
            } else {
                JavaFXClientLauncher.networkClient.commands().createChannel(input);
            }

            dialog.close();
        });

        inputField.setOnAction(e -> createBtn.fire());

        actions.getChildren().addAll(cancelBtn, createBtn);

        Region divider = new Region();
        divider.setStyle("-fx-background-color: #202225;");
        divider.setPrefHeight(1);

        root.getChildren().addAll(header, typeRow, inputArea, divider, actions);

        Scene scene = new Scene(root, 440, 280);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private ToggleButton buildTypeButton(String label, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(label);
        btn.setToggleGroup(group);
        btn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btn, Priority.ALWAYS);
        btn.setStyle("""
            -fx-background-color: #202225;
            -fx-text-fill: #B9BBBE;
            -fx-font-size: 13px;
            -fx-background-radius: 4;
            -fx-padding: 12 0 12 0;
            -fx-cursor: hand;
            """);

        btn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                btn.setStyle("""
                    -fx-background-color: #5865F2;
                    -fx-text-fill: white;
                    -fx-font-size: 13px;
                    -fx-font-weight: bold;
                    -fx-background-radius: 4;
                    -fx-padding: 12 0 12 0;
                    -fx-cursor: hand;
                    """);
            } else {
                btn.setStyle("""
                    -fx-background-color: #202225;
                    -fx-text-fill: #B9BBBE;
                    -fx-font-size: 13px;
                    -fx-background-radius: 4;
                    -fx-padding: 12 0 12 0;
                    -fx-cursor: hand;
                    """);
            }
        });

        return btn;
    }
}
