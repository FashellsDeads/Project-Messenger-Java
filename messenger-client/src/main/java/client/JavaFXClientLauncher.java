package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXClientLauncher extends Application{
    public static NetworkClient networkClient;

    @Override
    public void start(Stage primaryStage) throws Exception {
        networkClient = new NetworkClient();

        // Загружаем твой FXML (который ты нарисуешь в Scene Builder)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/style.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        primaryStage.setTitle("JavaFX Messenger");
        primaryStage.setScene(new Scene(root, 1000, 700));

        primaryStage.setOnCloseRequest(event -> {
            networkClient.disconnect();
            System.exit(0);
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
