package network;

import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

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
            in.close();
            out.close();
            socket.close();
        }
    }

    public void handleRemoteServerMessage(String msg) {
        // apply game updates from server
        System.out.println("Server: " + msg);
    }

    public void sendReadyState(boolean ready) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("READY:" + ready); // simple text protocol
        out.flush();
    }

    public String getLocalIp() { 
        return socket.getLocalAddress().getHostAddress(); 
    }

}
