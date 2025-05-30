package main;

import components.CollisionHandler;
import components.DataHandler;
import components.Entity;
import components.KeyHandler;
import components.MapCreator;
import components.MouseInteractions;
import components.Room;
import elements.Enemy;
import elements.Minimap;
import elements.Player;
import elements.TileManager;
import elements.User;
import main.GamePanel.GameState;
import ui.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import objects.GameObject;
import objects.Weapon;

import network.NetworkManager;
import network.Server;
import network.Client;
import network.MessageHandler;

public class GamePanel extends JPanel implements Runnable {
    
    // Tile settings
    final int originalTileSize = 16; // 16 x 16
    final int scale = 3;
    public final int originalScaledTileSize = originalTileSize * scale;
    public int tileSize = originalTileSize * scale; // 48 x 48

    // 22 : 13 Aspect Ratio
    public int maxScreenCol = 15 * 3 / 2; // 16 * 2
    public int maxScreenRow = 9 * 3 / 2; // 9 * 2
    public int screenWidth = tileSize * maxScreenCol; // 1536 px
    public int screenHeight = tileSize * maxScreenRow; // 864 px

    // World Settings (Room Sizes must be even)
    public final int enemyRoomSize = 22;
    public final int startingRoomSize = 20;
    public final int lootRoomSize = 14;
    public final int endRoomSize = 34;
    public final int corridorLength = 22;
    public final int corridorHeight = 6;
    public int currentPreset = 1 + (int)(Math.random() * 3);

    // !!! determines world size !!!
    public int sectionSize = 50; 
    public int sections = 3;
    public int worldSize = this.sectionSize * this.sections;

    public int maxWorldCol = this.worldSize;
    public int maxWorldRow = this.worldSize;
    public int worldWidth = this.originalScaledTileSize * this.maxWorldCol;
    public int worldHeight = this.originalScaledTileSize * this.maxWorldRow;
    public final int FPS = 60; // lowered FPS
    
    // Database
    public DatabaseManager dbManager;
    
    // Game Components
    public TileManager tileManager;
    public CollisionHandler collisionHandler;
    public KeyHandler keyHandler;
    public MouseInteractions mouse;
    public Thread gameThread;
    public MapCreator mapCreator;
    public AssetManager assetManager;
    public UI gameUI;
    public Minimap minimap;
    public DataHandler dataHandler;
    
    // Entities
    public ArrayList<GameObject> obj = new ArrayList<>();
    public ArrayList<GameObject> objToRemove = new ArrayList<>();
    public ArrayList<Entity> enemies = new ArrayList<>();
    public ArrayList<Entity> enemiesToRemove = new ArrayList<>();
    public Player player;

    //
    //// GAME STATES
    //
    public enum GameState {
        TITLE,
        PLAYING,
        PAUSE,
        END,
        MAIN_MENU,
        LOAD,
        LOGIN,
        SIGNUP,
        MULTIPLAYER_MENU,
        HOST_LOBBY,
        JOIN_LOBBY,
        SAVE_SLOT_SELECTION
    }
    public GameState gameState = GameState.TITLE;
    // public static final int TITLE_STATE = 0;
    // public static final int PLAYING_STATE = 1;
    // public static final int PAUSE_STATE = 2;
    // public static final int END_STATE = 3;
    // public static final int MENU_SCREEN_STATE = 4;
    // public static final int LOAD_STATE = 5;
    // public static final int LOGIN_STATE = 6;
    // public static final int SIGNUP_STATE = 7;
    // public static final int MULTIPLAYER_MENU_STATE = 8;
    // public static final int HOST_LOBBY_STATE = 9;
    // public static final int JOIN_LOBBY_STATE = 10;
    // public static final int SAVE_SLOT_SELECTION_STATE = 11;

    //
    //// Auth
    //
    public User user = null;
    public boolean paused = false;
    public int currentSlot = -1;
    public boolean loggedIn = false;

    //
    //// Network Classes
    //
    public Server networkServer;
    public Client networkClient;
    public String sessionCode;
    public final List<LobbyClient> lobbyClients = Collections.synchronizedList(new java.util.ArrayList<>());
    public volatile boolean localReady = false;

    //
    //// Game Progress Checking:
    //
    public final int levelEnhancer = 3;
    public int score = 0;
    public int currentLevel = 0;
    public double gameDifficulty = 1.0;
    public final int[] waveRange = {2, 4};
    public final int[] enemyAmtRange = {3, 6};
    public final int[] spawnTimeRange = {2, 5};

    // add test weapon for testing
    public Weapon testWeapon;

    // Styling
    public Color backgroundColor = new Color(0, 0, 128/2);

    public GamePanel() {
        this.setPreferredSize(new Dimension(this.screenWidth, this.screenHeight));
        this.setBackground(backgroundColor);
        this.setDoubleBuffered(true); // Rendered in the background, and then shown
        this.setFocusable(true);
        
        // Initialize core components
        this.dbManager = new DatabaseManager("realm_raiders_data.db");
        if (this.dbManager.connect()) {
            this.dbManager.initializeTables();
        } else {
            this.gameUI.logError("Error Connecting to Database");
        }
        
        this.keyHandler = new KeyHandler(this);
        this.mouse = new MouseInteractions(this);
        this.addKeyListener(keyHandler);
        this.addMouseListener(mouse);
        this.addMouseWheelListener(mouse);
        
        // Initialize game world and visual components
        this.assetManager = new AssetManager(this); // Load assets first
        this.tileManager = new TileManager(this);
        this.mapCreator = new MapCreator(this, true, this.currentPreset);
        this.mapCreator.setWorldSize(this.sections, this.sectionSize);
        this.worldSize = this.mapCreator.getWorldSize();
        this.updateWorldSize(); 
        this.mapCreator.setEnvironment();
        this.tileManager.mapTileNum = mapCreator.getWorldMap();
        this.minimap = new Minimap(this, 20);
        
        this.player = new Player(this, this.keyHandler, this.mouse); // Initialize player
        this.collisionHandler = new CollisionHandler(this);
        this.dataHandler = new DataHandler(this, this.dbManager);
        
        this.networkServer = new Server();
        this.networkServer.setGamePanel(this);
        this.networkClient = new Client();

        this.gameUI = new UI(this);
        this.gameUI.logInfo("game assets loaded.");
        this.requestFocusInWindow();
    }

    //
    //// Game Cleaning
    //
    public void cleanup() {
        // Reinstantiate assets
        this.assetManager = new AssetManager(this);
        this.assetManager.reset();
        this.keyHandler = new KeyHandler(this);
        this.mouse = new MouseInteractions(this);
        this.addKeyListener(keyHandler);
        this.addMouseListener(mouse);
        this.addMouseWheelListener(mouse);
        
        // // Add initial enemies or any other initial setup
        // enemies.add(new Enemy(this, (int) player.worldX, (int) player.worldY - 100, 1, "Goblin", false));
    }
    
    public void resetProperties() {
        this.player = new Player(this, keyHandler, mouse);
        this.currentLevel = 0;
        this.score = 0;
    }

    public void setupGame() {
        // add test weapon for testing
        // this.testWeapon = new Weapon(this, this.keyHandler, this.mouse, this.player);
        // this.testWeapon.initializeAsRandomWeapon();
        // this.testWeapon.worldX = (int) this.player.worldX;
        // this.testWeapon.worldY = (int) this.player.worldY;
        // this.obj.add(testWeapon);
        // System.out.println("Added test weapon!");
        // System.out.println(this.obj);
    }



    //
    ////  Game Instansitaion
    //
    
    public void newGame() {
        this.gameUI.logInfo("newGame");
        this.resetProperties();
        this.cleanup();
        this.generateNewLevel();
        this.setupGame();
        this.setGameState(GameState.PLAYING);
        this.requestFocus();

        if (this.gameThread == null || !this.gameThread.isAlive()) {
            this.startGameThread();
        }
    }

    public void setGameState(GameState newState) {
        if (this.gameState == newState) return;

        this.gameState = newState;

        if (gameUI != null) gameUI.rebuild();

        // If moving away from a network lobby, clean up:
        if (newState != GameState.HOST_LOBBY && networkServer != null && !networkServer.isConnected()) { // Server needs isSocketClosed()
            // stopHostingMultiplayer(); // Or handle cleanup somehow idk yet
        }
        if (newState != GameState.JOIN_LOBBY && networkClient != null && !networkClient.isConnected()) { // Client needs isSocketClosed() or similar
            // disconnectClient(); // Or handle cleanup
        }
        this.repaint();
    }

    public void generateNewLevel() { // Passed Previous Level So Load New One
        
        this.cleanup();
        this.tileManager = new TileManager(this);

        this.currentLevel++;
        this.gameDifficulty += 0.1;

        if (this.currentLevel % this.levelEnhancer == 0) {
            this.sections += 2;
            this.currentPreset = (1 + (int) (Math.random() * 3)); // Choose Random Preset
            this.tileManager.initPreset(currentPreset);
            this.mapCreator.preset(this.currentPreset);
            this.updateWorldSize();
        }

        this.assetManager.reset();

        this.mapCreator.setWorldSize(this.sections, this.sectionSize);
        this.worldSize = this.mapCreator.getWorldSize();
        this.mapCreator.setEnvironment();
        this.tileManager.mapTileNum = mapCreator.getWorldMap();
        this.minimap = new Minimap(this, 20);
        this.player.resetPosition();
    }

    public void updateWorldSize() {
        this.worldSize = this.sectionSize * this.sections;

        this.maxWorldCol = this.worldSize;
        this.maxWorldRow = this.worldSize;
        this.worldWidth = this.originalScaledTileSize * this.maxWorldCol;
        this.worldHeight = this.originalScaledTileSize * this.maxWorldRow;
    }



    //
    //// Threading 
    //
    public void startGameThread() {
        this.gameUI.logInfo("thread started");
        this.gameThread = new Thread(this); // Pass in itself (the game panel) to instantiate the thread
        this.gameThread.start();
    }

    public void endGameThread() {
        if (this.gameThread != null && gameThread.isAlive()) {
            // this.dbManager.disconnect();
            this.gameThread.interrupt();
            this.gameThread = null;
        }
    }



    //
    //// NETWORK: Server & Client Functions
    //
    public void prepareToHostMultiplayer() {
        leaveHostLobby(); // Clean up any previous host state
        networkServer = new Server();
        networkServer.setGamePanel(this); // Ensure the server has a reference to this GamePanel
        try {
            sessionCode = NetworkManager.startMultiplayerSession(networkServer);
            addOrUpdateLobbyClient(user == null ? "Host" : user.username, NetworkManager.getPublicIP(), false);
            setGameState(GameState.HOST_LOBBY);
        } catch (IOException ex) {
            gameUI.logError("Could not host: " + ex.getMessage());
        }
    }


    public void stopHostingMultiplayer() {
        if (networkServer != null) {
            networkServer.close();
            networkServer = null;
        }
        this.sessionCode = null;
        lobbyClients.clear(); // Clear lobby clients when host stops
        if (gameUI != null) {
            gameUI.rebuild(); // Refresh UI
        }
    }

    public boolean joinMultiplayerGame(String code) {
        if (networkClient == null) networkClient = new Client();
        try {
            NetworkManager.joinMultiplayerSession(networkClient, code);
            if (user != null) {
                networkClient.send("JOIN:" + user.username);
            } else {
                networkClient.send("JOIN:Player");
            }
            return true;
        } catch (IOException e) {
            gameUI.logError("Join failed: " + e.getMessage());
            return false;
        }
    }

    // Disconnect a client that is in a lobby
    public void leaveJoinLobby() {
        try {
            if (networkClient != null) networkClient.close();   // implement close() in Client if missing
        } catch (Exception ignored) {}
        networkClient = null;
        lobbyClients.clear();
        setGameState(GameState.MULTIPLAYER_MENU);
    }

    // Host uses this to dissolve his lobby and kick clients
    public void leaveHostLobby() {
        try {
            if (networkServer != null) networkServer.close();   // close sockets, etc.
        } catch (Exception ignored) {}
        networkServer = null;
        sessionCode = null;
        lobbyClients.clear();
        setGameState(GameState.MULTIPLAYER_MENU);
    }



    //
    //// NETWORK: UI & Classes
    //
    public static final class LobbyClient {
        public final String username;
        public String ip;
        public volatile boolean ready;

        public LobbyClient(String username, String ip) {
            this.username = username;
            this.ip = ip;
            this.ready = false;
        }
    }

    public void addOrUpdateLobbyClient(String user, String ip, boolean ready) {
        synchronized (lobbyClients) {
            for (LobbyClient c : lobbyClients) {
                if (c.username.equals(user)) {                     // update
                    c.ip = ip;
                    c.ready = ready;
                    if (gameUI != null) SwingUtilities.invokeLater(() -> gameUI.rebuild());                   
                    return;
                }
            }
            lobbyClients.add(new LobbyClient(user, ip)); // new client
            if (gameUI != null) SwingUtilities.invokeLater(() -> gameUI.rebuild());  
        }
    }

    public boolean allClientsReady() {
        synchronized (lobbyClients) {
            if (lobbyClients.isEmpty()) return false; // host alone -> not ready
            for (LobbyClient c : lobbyClients) if (!c.ready) return false;
            return true;
        }
    }

    public boolean isHost() { 
        return networkServer != null && sessionCode != null; 
    }

    public boolean isConnected() { 
        return networkClient != null && networkClient.isConnected(); 
    }

    public String  getSessionCode() { 
        return sessionCode != null ? sessionCode : "------"; 
    }

    public void setOwnReady(boolean ready) throws IOException {
        this.localReady = ready;
        // inform host / server
        if (networkClient != null) networkClient.sendReadyState(ready);
        addOrUpdateLobbyClient(
            user == null ? "Player" : user.username,
            networkClient != null ? networkClient.getLocalIp() : NetworkManager.getPublicIP(),
            ready
        );
    }

    public void startMultiplayerGame() { // todo: send “game-start” to all clients and move to PLAYING but for now just switch state locally
        if (isHost() && networkServer != null) {
            networkServer.send("GAME_START:");
        }
        setGameState(GameState.PLAYING);
    }

    public void leaveMultiplayerLobby() {  // called by back button in host lobby
        if (this.isHost()) {
            this.leaveHostLobby();
        }
        this.leaveJoinLobby();
    }



    //
    //// NETWORK: Server & Client Communication 
    //
    public void sendMapDataToClients(Server server) {
        StringBuilder sb = new StringBuilder();
        this.dataHandler.storeWorldData(sb);
        server.send(sb.toString().trim());
    }

    public void collectMapDataClient(Client client) {
        StringBuilder sb = new StringBuilder();
        client.receiveChunk(sb);
        BufferedReader reader = new BufferedReader(new StringReader(sb.toString()));
        this.dataHandler.loadWorldData(reader);
    }



    //
    //// Game Saving & Loading
    //
    public void saveProgress(int slot) {
        this.dataHandler.saveProgress(this.user.userId, slot);
    }

    public void loadProgress(int slot) {
        // this.cleanup();
        this.dataHandler.loadProgress(this.dbManager.getUserSaveSlots(this.user.userId)[slot-1]); 
        this.requestFocus();
    }


    //
    //// Game Rendering & Updating
    //
    @Override
    public void run() { // The GAME LOOP (the core of the game) - Automatically called when we create a thread

        double drawInterval = 1000000000 / FPS; 
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        // int frameCount = 0;

        while (this.gameThread != null) {
            // System.out.println("running");

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta > 1) {
                this.update();
                this.repaint(); // Calls the paintComponent method
                delta--;
                // frameCount++;
            }

            if (timer >= 1000000000) {
                // System.out.println("FPS: " + frameCount);
                // frameCount = 0;
                timer = 0;
            }

            this.mouse.getMousePosition();
        }

    }

    public void update() {
        // if (gameUI != null) gameUI.rebuild();
        if (this.gameState == GameState.PLAYING) {
            this.player.update();
            for (Entity enemy : enemies) {
                ((Enemy) enemy).update();
                if (((Enemy) enemy).isDead) {
                    enemiesToRemove.add(enemy);
                }
            }
            enemies.removeAll(enemiesToRemove);

            for (Room room : this.mapCreator.rooms) {
                room.update();
            }

            this.addGameObjectsInQueue();

        }
    }

    @Override
    public void paintComponent(Graphics g) { // what gets drawn last is on top
        super.paintComponent(g); // Reference the parent class of this class (JPanel) - It's JPanel's Method
        Graphics2D g2 = (Graphics2D) g.create();

        if (
            this.gameState == GameState.TITLE || 
            this.gameState == GameState.LOAD ||
            this.gameState == GameState.MAIN_MENU || 
            this.gameState == GameState.END || 
            this.gameState == GameState.LOGIN ||
            this.gameState == GameState.SIGNUP ||
            this.gameState == GameState.MULTIPLAYER_MENU ||
            this.gameState == GameState.HOST_LOBBY ||
            this.gameState == GameState.JOIN_LOBBY ||
            this.gameState == GameState.SAVE_SLOT_SELECTION
        ) {
            this.gameUI.draw(g2); // Draw GUI
        } else {
            
            this.drawGameFrame(g2); // Draw GUI + Game
            this.gameUI.draw(g2); // Draw in game UI 
        }

        g2.dispose();
    }

    public void drawGameFrame(Graphics2D g2) {
        this.tileManager.draw(g2); // Draw Tiles

        for (int i = 0; i < this.obj.size(); i++) { // draw game objects
            // System.out.println("Drawing " + object + " On ground? " + object.onGround);
            obj.get(i).draw(g2);
        }

        this.player.draw(g2); // Draw the Player

        for (Entity enemy : enemies) { // Draw Enemies
            ((Enemy) enemy).draw(g2);
        }

        this.minimap.draw(g2); // Draw the minimap
    }



    //
    //// Object Management
    //
    private ArrayList<GameObject> gameObjectsToAdd = new ArrayList<>();

    public boolean addObjectAfterFrame(GameObject object) {
        return gameObjectsToAdd.add(object);
    }

    private void addGameObjectsInQueue() {
        this.obj.addAll(gameObjectsToAdd);
        gameObjectsToAdd.clear();
    }

    public String getGamePanelProperties() {
        StringBuilder properties = new StringBuilder();
        properties.append("tileSize: ").append(this.tileSize).append("\n");
        properties.append("currentPreset: ").append(this.currentPreset).append("\n");
        properties.append("sectionSize: ").append(this.sectionSize).append("\n");
        properties.append("sections: ").append(this.sections).append("\n");
        properties.append("worldSize: ").append(this.worldSize).append("\n");
        properties.append("gameState: ").append(this.gameState).append("\n");
        properties.append("score: ").append(this.score).append("\n");
        properties.append("currentLevel: ").append(this.currentLevel).append("\n");
        properties.append("gameDifficulty: ").append(this.gameDifficulty).append("\n");
        return properties.toString();
    }
    

    
    //
    //// Miscellenious
    //
    public void zoom(int zoomAmt) {
        int oldWorldWidth = this.tileSize * this.maxWorldCol; // 10080
        this.tileSize += zoomAmt;
        int newWorldWidth = this.tileSize * this.maxWorldCol;

        this.player.speed = newWorldWidth / (this.worldWidth / this.player.trueSpeed);
        this.player.diagnolSpeed = this.player.calculateDiagnolSpeed(this.player.speed);

        // Multiplier keeps track of how much the player zoomed in and out
        double multiplier = (double) newWorldWidth / oldWorldWidth;

        // Use multiplier to make the players position stay in the center
        double newPlayerWorldX = this.player.worldX * multiplier;
        double newPlayerWorldY = this.player.worldY * multiplier;

        this.player.worldX = newPlayerWorldX;
        this.player.worldY = newPlayerWorldY;
    }
    
}
