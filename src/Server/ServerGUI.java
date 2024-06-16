package Server;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ServerGUI {
    private static boolean isServerRunning = false;
    private static ServerThread serverThread;
    private static TextArea logArea;

    public static BorderPane getComponent() {
        BorderPane mainPane = new BorderPane();

        Button startServer = new Button("Start Server");
        startServer.setOnAction(e -> {
            if (!isServerRunning) {
                serverThread = new ServerThread();
                serverThread.start();
                isServerRunning = true;
                startServer.setText("Stop Server");
            } else {
                serverThread.stopServer();
                isServerRunning = false;
                startServer.setText("Start Server");
            }
        });

        Button deletePlayers = new Button("Delete all players");
        deletePlayers.setOnAction(e -> {
            log("Deleted all players");
            // Implement player deletion logic
        });

        logArea = new TextArea();
        logArea.setMaxWidth(250);
        logArea.setEditable(false);

        HBox topContainer = new HBox(10);
        topContainer.setPadding(new Insets(10));
        topContainer.getChildren().addAll(startServer, deletePlayers);

        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(10));
        topSection.getChildren().addAll(new Label("Server Controls"), topContainer);

        VBox logSection = new VBox(10);
        logSection.setPadding(new Insets(10));
        logSection.getChildren().addAll(new Label("Server Log"), logArea);

        mainPane.setTop(topSection);
        mainPane.setLeft(logSection);

        return mainPane;
    }

    public static void log(String message) {
        System.out.println(message);
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }
}
