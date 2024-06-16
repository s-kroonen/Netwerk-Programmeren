package Server;

import org.dyn4j.dynamics.World;

import java.util.ArrayList;
import java.util.List;

public class GameWorld {
    private List<ClientHandler> clients = new ArrayList<>();
    private World world;

    public GameWorld() {
        this.world = new World();
        // Initialize world and entities
    }

    public synchronized void addClient(ClientHandler clientHandler) {
        clients.add(clientHandler);
        // Add player to the world
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        // Remove player from the world
    }

    public synchronized void update(double deltaTime) {
        world.update(deltaTime);
        // Update all entities and handle game logic
        broadcastState();
    }

    public synchronized void broadcastState() {
        String state = getWorldState();
        for (ClientHandler client : clients) {
            client.sendUpdate(state);
        }
    }

    private String getWorldState() {
        // Serialize world state to a string
        return world.toString();
    }
}
