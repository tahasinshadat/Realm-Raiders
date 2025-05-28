package network;

import java.io.*;
import java.net.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public String startSession() throws IOException {
        String code = NetworkManager.generateCode();
        String publicIP = NetworkManager.getPublicIP();

        NetworkManager.registerSession(code, publicIP);
        System.out.println("Session Code: " + code);

        this.serverSocket = new ServerSocket(NetworkManager.getPort());
        this.socket = serverSocket.accept();
        this.setupStreams();
        System.out.println("Client connected!");

        return code;
    }

    private void setupStreams() throws IOException {
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void send(String msg) {
        this.out.println(msg);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
        serverSocket.close();
    }

    public void startListening(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        setupStreams();
        System.out.println("Client connected.");
    }

    public void handleClientMessage(String msg) {
        System.out.println("Client: " + msg);
    }
}
