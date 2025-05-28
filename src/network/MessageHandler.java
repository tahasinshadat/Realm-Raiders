package network;

@FunctionalInterface
public interface MessageHandler {
    void handle(String message);
}
