package components;

import elements.Enemy;
import elements.Weapon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.GamePanel;
import objects.GameObject;

public class Room {
    GamePanel gamePanel;
    public boolean isCleared;
    public boolean isLootRoom = false;
    public boolean isBossRoom = false;
    public boolean isStartRoom = false;
    public int size;
    public int sectionX, sectionY;
    public int sectionsTopLeftX, sectionsTopLeftY;
    public int enemiesInRoom;
    public boolean roomInitialized = false;
    public boolean roomCleared = false;
    
    // Room boundaries
    public int roomTop, roomBottom, roomLeft, roomRight;

    // Entities
    public ArrayList<GameObject> roomObjects = new ArrayList<>();
    public ArrayList<Entity> roomEnemies = new ArrayList<>();

    // Waves
    private boolean waveActive = false;
    private int totalWaves;
    private int enemiesPerWave;
    private int currentWave;
    private int enemiesSpawned;
    private int frameCounter;
    private int framesBetweenSpawns;

    public Room(GamePanel gamePanel, int size, int x, int y) {
        this.gamePanel = gamePanel;

        this.size = size;
        if (this.size == this.gamePanel.lootRoomSize) this.isLootRoom = true;
        else if (this.size == this.gamePanel.endRoomSize) this.isBossRoom = true;
        else if (this.size == this.gamePanel.startingRoomSize) this.isStartRoom = true;

        this.isCleared = false;

        this.sectionX = x;
        this.sectionY = y;

        this.sectionsTopLeftX = (sectionX <= 0) ? 0 : (sectionX * this.gamePanel.sectionSize) - 1;
        this.sectionsTopLeftY = (sectionY <= 0) ? 0 : (sectionY * this.gamePanel.sectionSize) - 1; 

        this.roomLeft = this.sectionsTopLeftX + ( (this.gamePanel.sectionSize - size) / 2);
        this.roomRight = this.roomLeft + size - 1;
        this.roomTop = this.sectionsTopLeftY + ( (this.gamePanel.sectionSize - size) / 2);
        this.roomBottom = this.roomTop + size - 1;
    }

    public void update() {
        // System.out.println(this.isPlayerInRoom( (int) (this.gamePanel.player.worldX / this.gamePanel.tileSize), (int) (this.gamePanel.player.worldY / this.gamePanel.tileSize) ));
        if (!this.isBossRoom && !this.roomInitialized && !this.isCleared && !this.isLootRoom && !this.isStartRoom &&
            this.isPlayerInRoom((int) this.gamePanel.player.worldX, (int) this.gamePanel.player.worldY)) {
            
            this.initiateRoom();
            this.roomInitialized = true;
        }

        if (this.isBossRoom && !this.roomInitialized && !this.isCleared && 
            this.isPlayerInRoom( (int) this.gamePanel.player.worldX, (int) this.gamePanel.player.worldY)) {

            this.initiateBossRoom();
            this.roomInitialized = true;
        }


        if (this.waveActive) {
            if (this.frameCounter % this.framesBetweenSpawns == 0) {
                this.spawnNextWave();
                this.gamePanel.enemies.addAll(this.roomEnemies);
                this.roomEnemies = new ArrayList<>();
            }
            this.frameCounter++;
        } else if (this.gamePanel.enemies.isEmpty() && this.roomInitialized) {
            this.isCleared = true;
        }

        if (this.isCleared) this.roomCleared();
    }

    public void generateEnemies(int totalWaves, int enemiesPerWave, int framesBetweenSpawns) { // sets flag to true, and specifies enemy amount and time between spawns
        this.totalWaves = totalWaves;
        this.enemiesPerWave = enemiesPerWave;
        this.framesBetweenSpawns = framesBetweenSpawns;
        this.currentWave = 0;
        this.enemiesSpawned = 0;
        this.frameCounter = 0;
        this.waveActive = true;
    }

    private void spawnNextWave() { // spawns enemies in waves
        try {
            BufferedImage bulletImage = ImageIO.read(getClass().getResourceAsStream("../assets/weapons/enemy_bullet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.currentWave < this.totalWaves) {

            for (int i = 0; i < this.enemiesPerWave; i++) {
                roomEnemies.add(
                    new Enemy(
                        this.gamePanel, 
                        this.randomNum(this.roomLeft + 1, this.roomRight - 1) * this.gamePanel.tileSize, 
                        this.randomNum(this.roomTop + 1, this.roomBottom - 1) * this.gamePanel.tileSize, 
                        this.randomNum(1, Enemy.enemyTypes)
                    )     
                );
            }
            this.currentWave++;
            
        } else {
            this.waveActive = false;
        }
    }


    public void initiateRoom() { // closes the gates and spawns the enemies
        this.closeGates();
        this.generateEnemies(
            this.randomNum(this.gamePanel.waveRange[0], this.gamePanel.waveRange[1]), 
            this.randomNum(this.gamePanel.enemyAmtRange[0], this.gamePanel.enemyAmtRange[1]), 
            this.randomNum(this.gamePanel.spawnTimeRange[0] * this.gamePanel.FPS, this.gamePanel.spawnTimeRange[1] * this.gamePanel.FPS)
        );
    }

    public void initiateBossRoom() {
        this.closeGates();
    }

    public String getRoomKey() {
        return this.sectionX + "," + this.sectionY;
    }

    public void roomCleared() { // opens the gates because the player has cleared the room / killed all the enemies
        if (!this.roomCleared) {
            this.openGates();
            this.gamePanel.score++;
        }
        this.roomCleared = true;
    }

    public void openGates() {
        for (int x = this.sectionsTopLeftX; x <= this.sectionsTopLeftX + this.gamePanel.sectionSize; x++) {
            for (int y = this.sectionsTopLeftY; y <= this.sectionsTopLeftY+this.gamePanel.sectionSize; y++) {

                if (this.gamePanel.tileManager.mapTileNum[x][y] == 8) {
                    this.gamePanel.tileManager.mapTileNum[x][y] = 7;
                }
                
            }
        }
    }

    public void closeGates() {
        for (int x = this.sectionsTopLeftX; x <= this.sectionsTopLeftX+this.gamePanel.sectionSize; x++) {
            for (int y = this.sectionsTopLeftY; y <= this.sectionsTopLeftY+this.gamePanel.sectionSize; y++) {
                // System.out.print("Checking tile at: " + x + ", " + y + ": ");
                // System.out.println(this.gamePanel.tileManager.mapTileNum[y][x]);
                
                if (this.gamePanel.tileManager.mapTileNum[x][y] == 7) {
                    // System.out.println("gate coords: " + x + ", " + y);
                    this.gamePanel.tileManager.mapTileNum[x][y] = 8;
                }
                
            }
        }
    }

    public boolean isPlayerInRoom(int playerWorldX, int playerWorldY) {
        return playerWorldX / this.gamePanel.tileSize > this.roomLeft + 1 && 
               playerWorldX / this.gamePanel.tileSize < this.roomRight - 1 &&

               playerWorldY / this.gamePanel.tileSize > this.roomTop + 1 && 
               playerWorldY / this.gamePanel.tileSize < this.roomBottom - 1;
    }

    private int randomNum(int min, int max) { // Inclusive
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }
    
    @Override
    public String toString() {
        return this.getRoomKey();
    }
}
