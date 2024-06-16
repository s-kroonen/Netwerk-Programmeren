package Server;

public interface ClientCallback {
    void stop();
    void rawCommand(String msg);
}
