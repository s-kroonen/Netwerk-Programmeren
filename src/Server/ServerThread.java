package Server;

import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    private ServerSocket serverSocket;
    private int port = 8888;
    private GameWorld gameWorld;

    public ServerThread() {
        this.gameWorld = new GameWorld();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            Platform.runLater(() -> ServerGUI.log("Server running on port: " + port));

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, gameWorld);
                gameWorld.addClient(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            Platform.runLater(() -> ServerGUI.log("Server stopped"));
        }
    }

    public void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
