package main;

import components.CollisionHandler;
import components.Entity;
import components.KeyHandler;
import components.MapCreator;
import components.MouseInteractions;
import elements.Player;
import elements.TileManager;
import elements.Weapon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import objects.GameObject;

public class GamePanel extends JPanel implements Runnable {
    
    // Tile settings
    final int originalTileSize = 16; // 16 x 16
    final int scale = 3;
    public int tileSize = originalTileSize * scale; // 48 x 48

    // 22 : 13 Aspect Ratio
    public int maxScreenCol = 15 * 3/2; // 16 * 2
    public int maxScreenRow = 9 * 3/2; // 9 * 2
    public int screenWidth = tileSize * maxScreenCol; // 1536 px
    public int screenHeight = tileSize * maxScreenRow; // 864 px

    // World Settings (Room Sizes must be even)
    public final int enemyRoomSize = 20;
    public final int startingRoomSize = 18;
    public final int lootRoomSize = 14;
    public final int portalRoomSize = 12;
    public final int corridorLength = 22;
    public final int corridorHeight = 6;
    public int currentPreset = 1;

    // !!! determines world size !!!
    public int sectionSize = 50; 
    public int sections = 7;
    public int worldSize = this.sectionSize * this.sections;

    public final int maxWorldCol = this.worldSize;
    public final int maxWorldRow = this.worldSize;
    public final int worldWidth = this.tileSize * this.maxWorldCol;
    public final int worldHeight = this.tileSize * this.maxWorldRow;

    final int FPS = 60;

    // Game Components
    public TileManager tileManager = new TileManager(this);
    public CollisionHandler collisionHandler = new CollisionHandler(this);
    KeyHandler keyHandler = new KeyHandler(this);
    MouseInteractions mouse = new MouseInteractions(this);
    Thread gameThread;
    MapCreator mapCreator = new MapCreator(this, true, this.currentPreset);
    UI gameUI = new UI(this);
    
    // Entities
    public Player player = new Player(this, this.keyHandler, this.mouse);
    public ArrayList<GameObject> obj = new ArrayList<>();
    public ArrayList<Entity> enemies = new ArrayList<>();

    public GamePanel() {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Redered in the background, and then shown
        this.setBackground(Color.decode("#010b19"));
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        mapCreator.setWorldSize(sections, sectionSize);
        this.worldSize = mapCreator.getWorldSize();
        mapCreator.setEnvironment(20);
        tileManager.mapTileNum = mapCreator.getWorldMap();
    }

    public void startGameThread() {
        this.gameThread = new Thread(this); // Pass in itself (the gamepanel) to instanciate the thread
        this.gameThread.start();
    }

    @Override
    public void run() { // The GAME LOOP (the core of the game) - Automatically called when we create a thread

        double drawInterval = 1000000000 / FPS; 
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        // int frameCount = 0;

        while (gameThread != null) {
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
        this.player.update();
    }

    @Override
    public void paintComponent(Graphics g) { // what gets drawn last is ontop
        super.paintComponent(g); // Reference the parent class of this class (JPanel) - It's JPanels Method
        Graphics2D g2 = (Graphics2D)g;

        // Draw Tiles
        this.tileManager.draw(g2);

        // Draw Objects

        // Draw the Player
        this.player.draw(g2);

        // Draw UI
        this.gameUI.draw(g2);

        g2.dispose(); 
        // System.out.println(this.tileSize * this.maxWorldCol);
    }

    public void zoom(int zoomAmt) {

        int oldWorldWidth = this.tileSize * this.maxWorldCol; // 10080
        tileSize += zoomAmt;
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
