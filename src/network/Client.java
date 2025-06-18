package network;

import java.io.*;
import java.net.*;

import main.GamePanel;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GamePanel gamePanel;

    public Client(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void connectToSession(String code) throws IOException {
        String ip = NetworkManager.resolveIP(code); // resolve IP using code
        connect(ip, NetworkManager.getPort());      // connect using the resolved IP
    }

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        setupStreams();
    }

    private void setupStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void send(String msg) {
        out.println(msg);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    public void receiveChunk(StringBuilder sb) {
        for (String s : in.lines().toList()) sb.append(s);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void close() throws IOException {
        if (socket != null) {
            out.close();
            in.close();
            socket.close();
        }
    }

    public void handleRemoteServerMessage(String msg) {
        // apply game updates from server
        System.out.println("Server: " + msg);
        if (msg.startsWith("LOBBY_UPDATE:")) {
            handleLobbyMessage(msg.substring(13));
        }
    }

    public void handleLobbyMessage(String msg) {
        // System.out.println(msg);
        if (msg.startsWith("PLAYER_JOINED:")) {
            String[] contents = msg.substring("PLAYER_JOINED:".length()).split(":");
            // System.out.println(contents[0]);
            gamePanel.addOrUpdateLobbyClient(contents[0], contents[1], false);
        }
        else if (msg.startsWith("CLIENTS:")) {
            String[] clients = msg.substring("CLIENTS:".length()).split(";");
            for (String client : clients) {
                String[] clientInfo = client.split(":");
                gamePanel.addOrUpdateLobbyClient(clientInfo[0], clientInfo[1], Boolean.parseBoolean(clientInfo[2]));
            }
        } 
        else if (msg.startsWith("PLAYER_LEFT:")) {
            String[] contents = msg.substring("PLAYER_LEFT:".length()).split(":");
            gamePanel.lobbyClients.removeIf(lc -> lc.username.equals(contents[0]));
        }
        else { // ready state
            String[] contents = msg.split(":");
            for (GamePanel.LobbyClient c : gamePanel.lobbyClients) {
                if (c.username.equals(contents[0])) {
                    c.ready = Boolean.parseBoolean(contents[1]);
                }
            }
        }
    }

    public void sendReadyState(boolean ready) {
        send("READY:" + ready);
    }

    public String getLocalIp() { 
        return socket.getLocalAddress().getHostAddress(); 
    }

}
