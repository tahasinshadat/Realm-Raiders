package main;

import components.CollisionHandler;
import components.Entity;
import components.KeyHandler;
import components.MapCreator;
import components.MouseInteractions;
import components.Room;
import elements.Enemy;
import elements.Player;
import elements.TileManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import objects.GameObject;
import objects.Weapon;

public class GamePanel extends JPanel implements Runnable {
    
    // Tile settings
    final int originalTileSize = 16; // 16 x 16
    final int scale = 3;
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

    public final int maxWorldCol = this.worldSize;
    public final int maxWorldRow = this.worldSize;
    public final int worldWidth = this.tileSize * this.maxWorldCol;
    public final int worldHeight = this.tileSize * this.maxWorldRow;
    public final int FPS = 60; // lowered FPS

    // Game Components
    public TileManager tileManager = new TileManager(this);
    public CollisionHandler collisionHandler = new CollisionHandler(this);
    public KeyHandler keyHandler = new KeyHandler(this);
    public MouseInteractions mouse = new MouseInteractions(this);
    public Thread gameThread;
    public MapCreator mapCreator = new MapCreator(this, true, this.currentPreset);
    public UI gameUI = new UI(this);
    public AssetManager assetManager = new AssetManager(this);
    
    // Entities
    public ArrayList<GameObject> obj = new ArrayList<>();
    public ArrayList<GameObject> objToRemove = new ArrayList<>();
    public Player player = new Player(this, this.keyHandler, this.mouse);
    public ArrayList<Entity> enemies = new ArrayList<>();
    public ArrayList<Entity> enemiesToRemove = new ArrayList<>();

    // Game States
    public static final int TITLE_STATE = 0;
    public static final int PLAYING_STATE = 1;
    public static final int PAUSE_STATE = 2;
    public static final int END_STATE = 3;
    public static final int MENU_SCREEN_STATE = 4;
    public static final int LOAD_STATE = 5;
    public int gameState = GamePanel.TITLE_STATE;
    public boolean paused = false;

    // Game Progress Checking:
    public int levelEnhancer = 3;
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
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Rendered in the background, and then shown
        this.setBackground(Color.decode("#010b19"));
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        this.mapCreator.setWorldSize(this.sections, this.sectionSize);
        this.worldSize = this.mapCreator.getWorldSize();
        this.mapCreator.setEnvironment();
        this.tileManager.mapTileNum = mapCreator.getWorldMap();

        // Add some enemies to the map for testing
        // enemies.add(new Enemy(this, (int) this.player.worldX, (int) this.player.worldY - 100, 1, "Goblin", false));
        // enemies.add(new Enemy(this, (int) this.player.worldX, (int) this.player.worldY - 100, 2, "BOSS", true));
        // enemies.add(new Enemy(this, (int) this.player.worldX, (int) this.player.worldY - 100, 1, "Orc", false));
        // enemies.add(new Enemy(this, (int) this.player.worldX, (int) this.player.worldY - 100, 2, "Archer", false));
        // enemies.add(new Enemy(this, (int) this.player.worldX, (int) this.player.worldY - 100, 1, "Orc", false));
    }

    public void newGame() {
        // this.endGameThread(); // DO NOT UNCOMMENT
        this.resetProperties();
        this.cleanup();
        this.generateNewLevel();
        this.setupGame();
        this.gameState = GamePanel.PLAYING_STATE;
        // this.startGameThread(); // DO NOT UNCOMMENT
        this.requestFocus();
        // enemies.add(new Enemy(this, (int) this.player.worldX, (int) this.player.worldY - 100, 1, "Goblin", false));
    }

    public void cleanup() {
        // Reinstantiate assets
        this.assetManager = new AssetManager(this);
        this.assetManager.reset();
        this.gameUI.removeButtons();
        
        // // Add initial enemies or any other initial setup
        // enemies.add(new Enemy(this, (int) player.worldX, (int) player.worldY - 100, 1, "Goblin", false));
    }
    
    public void resetProperties() {
        this.player = new Player(this, keyHandler, mouse);
        this.currentLevel = 0;
        this.score = 0;
        this.levelEnhancer = 3;
    }

    public void setupGame() {
        // add test weapon for testing
        this.testWeapon = new Weapon(this, this.keyHandler, this.mouse, this.player);
        this.testWeapon.initializeAsRandomWeapon();
        this.testWeapon.worldX = (int) this.player.worldX;
        this.testWeapon.worldY = (int) this.player.worldY;
        this.obj.add(testWeapon);
        // System.out.println("Added test weapon!");
        // System.out.println(this.obj);

        this.assetManager.setObjects();
        this.assetManager.setEnemies();
    }

    public void generateNewLevel() { // Passed Previous Level So Load New One
        this.currentLevel++;
        this.gameDifficulty += 0.1;

        if (this.currentLevel % this.levelEnhancer == 0) {
            this.sections += 2;
            this.currentPreset = (1 + (int) (Math.random() * 3)); // Choose Random Preset
            this.mapCreator.preset(this.currentPreset);
        }

        this.assetManager.reset();

        this.mapCreator.setWorldSize(this.sections, this.sectionSize);
        this.worldSize = this.mapCreator.getWorldSize();
        this.mapCreator.setEnvironment();
        this.tileManager.mapTileNum = mapCreator.getWorldMap();
    }

    public void startGameThread() {
        this.gameThread = new Thread(this); // Pass in itself (the game panel) to instantiate the thread
        this.gameThread.start();
    }

    public void endGameThread() {
        if (this.gameThread != null && gameThread.isAlive()) {
            this.gameThread.interrupt();
            this.gameThread = null;
        }
    }

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

    // public Room test = new Room(this, 22, 2, 1);

    public void update() {
        if (this.gameState == GamePanel.PLAYING_STATE) {
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

            // test.update();
        }
    }

    @Override
    public void paintComponent(Graphics g) { // what gets drawn last is on top
        super.paintComponent(g); // Reference the parent class of this class (JPanel) - It's JPanel's Method
        Graphics2D g2 = (Graphics2D) g;

        if (
            this.gameState == GamePanel.TITLE_STATE || 
            this.gameState == GamePanel.PAUSE_STATE || 
            this.gameState == GamePanel.LOAD_STATE ||
            this.gameState == GamePanel.MENU_SCREEN_STATE || 
            this.gameState == GamePanel.END_STATE
        ) {
            gameUI.draw(g2); // Draw GUI
        } else {
            this.tileManager.draw(g2); // Draw Tiles

            for (GameObject object : obj) { // draw game objects
                // System.out.println("Drawing " + object + " On ground? " + object.onGround);
                object.draw(g2);
            }

            this.player.draw(g2); // Draw the Player

            for (Entity enemy : enemies) { // Draw Enemies
                ((Enemy) enemy).draw(g2);
            }

            this.gameUI.draw(g2); // Draw in game UI
        }

        g2.dispose();
    }

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
