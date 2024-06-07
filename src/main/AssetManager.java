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

        // reset enemy images
        try {
            Enemy.bulletImage = ImageIO.read(getClass().getResourceAsStream("../assets/weapons/enemy_bullet.png"));
            Enemy.bossLeft = ImageIO.read(getClass().getResourceAsStream("../assets/enemies/boss/left.png"));
            Enemy.bossRight = ImageIO.read(getClass().getResourceAsStream("../assets/enemies/boss/right.png"));
            Enemy.meleeLeft1 = ImageIO.read(getClass().getResourceAsStream("../assets/enemies/melee/left1.png"));
            Enemy.meleeLeft2 = ImageIO.read(getClass().getResourceAsStream("../assets/enemies/melee/left2.png"));
            Enemy.meleeRight1 = ImageIO.read(getClass().getResourceAsStream("../assets/enemies/melee/right1.png"));
            Enemy.meleeRight2 = ImageIO.read(getClass().getResourceAsStream("../assets/enemies/melee/right2.png"));
            Enemy.rangedLeft1 = ImageIO.read(getClass().getResourceAsStream("../assets/enemies/ranged/left1.png"));
            Enemy.rangedLeft2 = ImageIO.read(getClass().getResourceAsStream("../assets/enemies/ranged/left2.png"));
            Enemy.rangedRight1 = ImageIO.read(getClass().getResourceAsStream("../assets/enemies/ranged/right1.png"));
            Enemy.rangedRight2 = ImageIO.read(getClass().getResourceAsStream("../assets/enemies/ranged/right2.png"));
            Enemy.towerImage = ImageIO.read(getClass().getResourceAsStream("../assets/enemies/ranged/tower.png"));
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
