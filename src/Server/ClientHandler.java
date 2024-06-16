package Server;

import gameEntities.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler implements Runnable, ClientCallback {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private Player player;
    private GameWorld gameWorld;

    public ClientHandler(Socket clientSocket, GameWorld gameWorld) {
        this.clientSocket = clientSocket;
        this.gameWorld = gameWorld;
    }

    public void run() {
        ServerGUI.log("New Client thread");
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while (clientSocket.isConnected()) {
                if ((inputLine = in.readLine()) != null) {
                    handleInput(inputLine);
                }
            }

            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleInput(String inputLine) {
        // Process input from client, such as player movements or actions
        ServerGUI.log(clientSocket.getInetAddress().getHostName() + ": " + inputLine);

        // Example: update player position based on input
        String[] tokens = inputLine.split(" ");
        switch (tokens[0]) {
            case "MOVE":
                double x = Double.parseDouble(tokens[1]);
                double y = Double.parseDouble(tokens[2]);
                player.getBody().;
                break;
            // Add more cases for different actions
        }

        // Broadcast updated game state to all clients
        gameWorld.broadcastState();
    }

    @Override
    public void rawCommand(String msg) {
        if (clientSocket.isConnected()) {
            out.println(msg);
        }
    }

    @Override
    public void stop() {
        ServerGUI.log("Stopped client: " + clientSocket.getInetAddress().getHostName());
        try {
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendUpdate(String update) {
        out.println(update);
    }
}
