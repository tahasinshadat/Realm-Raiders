package main;

import java.io.IOException;

import javax.imageio.ImageIO;

import elements.Enemy;
import elements.Player;

public class AssetManager {
    
    public GamePanel gamePanel;

    public AssetManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setObjects() {
        
    }

    public void setEnemies() {
        try {
            Enemy.bulletImage = ImageIO.read(getClass().getResourceAsStream("../assets/weapons/enemy_bullet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        // Clear All Entities
        this.gamePanel.obj.clear();
        this.gamePanel.objToRemove.clear();
        this.gamePanel.enemies.clear();
        this.gamePanel.enemiesToRemove.clear();

        // Reset Player Fully
        this.gamePanel.player = new Player(this.gamePanel, this.gamePanel.keyHandler, this.gamePanel.mouse);
    }

}
