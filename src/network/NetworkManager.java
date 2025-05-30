package network;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NetworkManager {
    private static final String PASTE_SERVICE = "https://paste.rs";  // Free and anonymous pastebin
    private static final int PORT = 5005;

    public static String generateSessionCode() {
        String pool = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(6);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < 6; i++)
            sb.append(pool.charAt(rnd.nextInt(pool.length())));
        return sb.toString();
    }


    public static String getPublicIP() throws IOException {
        URL url = URI.create("http://checkip.amazonaws.com").toURL(); // free & public API that returns your public IP address as a plain string
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

        String code = generateSessionCode();
        String publicIP = getPublicIP();
        registerSession(code, publicIP);
        System.out.println("Session Code: " + code);

        /* Start the server's listening loop in its own thread.
           The server.startListening(PORT) method itself now handles
           accepting clients and spawning individual ClientHandler threads for receiving. */
        new Thread(() -> {
            try {
                server.startListening(PORT); // This now handles accepting and managing client connections
            } catch (IOException e) {
                System.err.println("Server stopped: " + e.getMessage());
            }
        }, "Host-Accept").start();

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
                    String msg = connection instanceof Client // Only expect Client here now
                        ? ((Client) connection).receive()
                        : null; // Server side no longer uses this method for receiving

                    if (msg != null) handler.handle(msg);
                }
            } catch (IOException e) {
                System.err.println("Connection closed or failed: " + e.getMessage());
            }
        }).start();
    }
}