package network;

import java.io.*;
import java.net.*;
import java.util.Random;

public class NetworkManager {
    private static final String PASTE_SERVICE = "https://paste.rs";  // Free and anonymous pastebin
    private static final int PORT = 5005;

    public static String generateCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) code.append(chars.charAt(rand.nextInt(chars.length())));
        return code.toString();
    }

    public static String getPublicIP() throws IOException {
        URL url = URI.create("https://api.ipify.org").toURL(); // free & public API that returns your public IP address as a plain string
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        return in.readLine().trim();
    }

    public static void registerSession(String code, String publicIP) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("curl", "-X", "POST", "--data", publicIP, PASTE_SERVICE + "/" + code);
        pb.redirectErrorStream(true);
        pb.start();
    }

    public static String resolveIP(String code) throws IOException {
        URL url = URI.create(PASTE_SERVICE + "/" + code).toURL();
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        return in.readLine().trim();
    }

    public static int getPort() {
        return PORT;
    }

    // ==== Server Side ====
    public static String startMultiplayerSession(Server server) throws IOException {
        String code = generateCode();
        String publicIP = getPublicIP();

        registerSession(code, publicIP);
        System.out.println("Session Code: " + code);

        server.startListening(PORT);
        startReceivingThread(server, server::handleClientMessage);

        return code;
    }


    // ==== Client Side ====
    public static void joinMultiplayerSession(Client client, String code) throws IOException {
        String ip = resolveIP(code);
        if (ip == null || ip.isEmpty()) throw new IOException("Invalid session code.");

        client.connect(ip, PORT);
        startReceivingThread(client, client::handleRemoteServerMessage);
    }


    // Server AND Client Side Functions:
    private static void startReceivingThread(Object connection, MessageHandler handler) {
        new Thread(() -> {
            try {
                while (true) {
                    String msg = connection instanceof Server
                        ? ((Server) connection).receive()
                        : ((Client) connection).receive();

                    if (msg != null) handler.handle(msg);
                }
            } catch (IOException e) {
                System.err.println("Connection closed or failed: " + e.getMessage());
            }
        }).start();
    }
}