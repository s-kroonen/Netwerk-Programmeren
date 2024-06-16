package Server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Server extends Application {
    public static void main(String[] args) {
        launch(Server.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Server GUI");

        Scene scene = new Scene(ServerGUI.getComponent(), 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnHiding(event -> {
            System.out.println("Exiting application...");
            System.exit(0);
        });

    }
}
