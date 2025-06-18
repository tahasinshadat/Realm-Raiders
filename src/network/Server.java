package network;

import main.GamePanel; // Import GamePanel to allow server to update lobby UI
import main.GamePanel.LobbyClient;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Server {
    private ServerSocket serverSocket;
    // We will no longer have a single 'socket', 'in', or 'out' directly in Server.
    // Instead, each ClientHandler will manage its own.
    private GamePanel gamePanel; // Reference to GamePanel to update lobby UI

    // List to keep track of all connected clients
    private final List<ClientHandler> connectedClients = Collections.synchronizedList(new ArrayList<>());

    public Server() {
        // Constructor, no immediate socket setup
    }

    // Set GamePanel reference after Server is instantiated in GamePanel
    public void setGamePanel(GamePanel gp) {
        this.gamePanel = gp;
    }

    public String startSession() throws IOException {
        String code = NetworkManager.generateSessionCode();
        String publicIP = NetworkManager.getPublicIP();

        NetworkManager.registerSession(code, publicIP);
        System.out.println("Session Code: " + code);

        // Start listening for clients in a non-blocking way for the main thread
        // The actual serverSocket.accept() will now be inside startListening(PORT)
        startListening(NetworkManager.getPort()); 
        
        return code;
    }

    // This method now continuously listens for and accepts new clients
    public void startListening(int port) throws IOException {
        serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
        System.out.println("Server started on port " + port + ". Waiting for clients...");

        // Start a new thread to continuously accept clients
        new Thread(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept(); // Blocks until a client connects
                    System.out.println("Client connected from " + clientSocket.getInetAddress().getHostAddress());
                    ClientHandler handler = new ClientHandler(clientSocket, this, gamePanel);
                    connectedClients.add(handler);
                    new Thread(handler).start(); // Start a new thread for this client
                }
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    System.err.println("Server listening error: " + e.getMessage());
                }
            } finally {
                close(); // Ensure server socket is closed on exit
            }
        }, "Server-Accept-Loop").start();
    }

    // Send a message to all connected clients (broadcast)
    public void send(String msg) {
        synchronized (connectedClients) {
            for (ClientHandler client : connectedClients) {
                client.sendToClient(msg);
            }
        }
    }

    public void send(ClientHandler client, String msg) {
        client.sendToClient(msg);
    }

    // Method to handle messages received from any client
    public void handleClientMessage(String msg, ClientHandler sender) {
        System.out.println("Received from client " + sender.getClientId() + ": " + msg);
        // Implement game logic based on the message
        // For example, update GamePanel.lobbyClients:
        if (msg.startsWith("READY:")) {
            boolean readyState = Boolean.parseBoolean(msg.substring(6));
            if (gamePanel != null && sender.getUsername() != null) {
                 // Update the ready state for the sender in the lobbyClients list
                gamePanel.addOrUpdateLobbyClient(sender.getUsername(), sender.getIpAddress(), readyState);
            }
            // broadcast the ready state change to all other clients
            send("LOBBY_UPDATE:" + sender.getUsername() + ":" + readyState);
        } else if (msg.startsWith("JOIN:")) {
            String username = msg.substring(5);
            sender.setUsername(username); // Set username for this handler
            if (gamePanel != null) {
                gamePanel.addOrUpdateLobbyClient(username, sender.getIpAddress(), false); // Add new client to lobby list
            }
            // Broadcast new client's arrival to existing clients
            String clients = "LOBBY_UPDATE:CLIENTS:";
            for (int i = 0; i < gamePanel.lobbyClients.size() - 1; i++) {
                LobbyClient c = gamePanel.lobbyClients.get(i);
                clients += c.username + ":" + c.ip + ":" + c.ready + ";";
            }
            sender.sendToClient(clients);
            send("LOBBY_UPDATE:PLAYER_JOINED:" + username + ":" + sender.getIpAddress());
        }
    }

    // Method to remove a disconnected client
    public void removeClient(ClientHandler client) {
        connectedClients.remove(client);
        if (gamePanel != null && client.getUsername() != null) {
            // Remove client from lobby UI
            gamePanel.lobbyClients.removeIf(lc -> lc.username.equals(client.getUsername()));
        }
        System.out.println("Client " + client.getClientId() + " disconnected. Remaining clients: " + connectedClients.size());
        // Broadcast client removal to remaining clients
        send("PLAYER_LEFT:" + client.getUsername());
    }

    public boolean isConnected() {
        return serverSocket != null && !serverSocket.isClosed();
    }

    public void close() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server socket closed.");
            }
            // Close all client sockets
            synchronized (connectedClients) {
                for (ClientHandler handler : connectedClients) {
                    handler.close();
                }
                connectedClients.clear();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }

    // --- Inner Class: ClientHandler ---
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private Server server; // Reference to the main server instance
        private GamePanel gamePanel;
        private String clientId; // Unique ID for logging/tracking
        private String username; // Username of the connected client
        private String ipAddress; // IP address of the connected client

        public ClientHandler(Socket socket, Server server, GamePanel gamePanel) throws IOException {
            this.clientSocket = socket;
            this.server = server;
            this.gamePanel = gamePanel;
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.clientId = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
            this.ipAddress = clientSocket.getInetAddress().getHostAddress();
        }

        public String getClientId() { return clientId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getIpAddress() { return ipAddress; }

        public void sendToClient(String msg) {
            if (out != null && !clientSocket.isClosed()) {
                out.println(msg);
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while (!clientSocket.isClosed() && (message = in.readLine()) != null) {
                    server.handleClientMessage(message, this); // Pass message and this handler to server
                }
            } catch (IOException e) {
                if (!clientSocket.isClosed()) {
                    System.err.println("Client handler error for " + clientId + ": " + e.getMessage());
                }
            } finally {
                server.removeClient(this); // Remove client from server's list upon disconnection
                close(); // Close this client's resources
            }
        }

        public void close() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client handler resources for " + clientId + ": " + e.getMessage());
            }
        }
    }
}