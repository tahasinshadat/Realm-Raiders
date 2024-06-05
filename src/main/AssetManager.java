package main;

import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import elements.Enemy;
import elements.Player;

public class AssetManager {
    
    public GamePanel gamePanel;

    public AssetManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void reset() {
        // Clear All Entities
        this.gamePanel.obj.clear();
        this.gamePanel.objToRemove.clear();
        this.gamePanel.enemies.clear();
        this.gamePanel.enemiesToRemove.clear();

        // Reset Player
        this.gamePanel.player.worldX = this.gamePanel.worldWidth / 2;
        this.gamePanel.player.worldY = this.gamePanel.worldHeight / 2;
        this.gamePanel.player.health = this.gamePanel.player.maxHealth;
        this.gamePanel.player.shield = this.gamePanel.player.maxShield;

        // reset enemy bulletImage
        try {
            Enemy.bulletImage = ImageIO.read(getClass().getResourceAsStream("../assets/weapons/enemy_bullet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load an image
    public BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
