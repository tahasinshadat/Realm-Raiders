package elements;

import components.Entity;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import main.GamePanel;

public class Enemy extends Entity {
    GamePanel gamePanel;
    private double screenX;
    private double screenY;
    private int prevTileSize;
    private int width;
    private int height;

    public int startX, startY;
    public int type;
    public String name;
    public int health, maxHealth, defense;
    public int damage;
    public int speed, attackSpeed;
    public int size;
    public boolean isBoss;

    public Enemy(GamePanel gamePanel, int startX, int startY, int enemyType, String enemyName, boolean isBoss) {
        this.gamePanel = gamePanel;
        this.worldX = startX;
        this.worldY = startY;
        this.type = enemyType;
        this.name = enemyName;
        this.isBoss = isBoss;
        this.setDefaults();
        this.prevTileSize = this.gamePanel.tileSize;

        this.width = this.gamePanel.tileSize;
        this.height = this.gamePanel.tileSize;

        this.hitbox = new Rectangle();
        this.updateValuesOnZoom();
    }

    public void setDefaults() {
        if (this.isBoss) {
            this.size = 100;
            this.health = 300;
            this.maxHealth = 300;
            this.damage = 15;
            this.defense = 30;
            this.speed = 8;
            this.attackSpeed = 5;
        } else {
            this.size = 50;
            if (this.type == 1) { // melee unit
                this.health = 100;
                this.maxHealth = 100;
                this.damage = 7;
                this.defense = 20;
                this.speed = 7;
                this.attackSpeed = 10;
            } else if (this.type == 2) { // ranged unit
                this.health = 50;
                this.maxHealth = 50;
                this.damage = 20;
                this.defense = 10;
                this.speed = 5;
                this.attackSpeed = 8;
            }
        }
    }

    public void update() {
        this.updateValuesOnZoom();

        // Update direction based on angle for collisons - ngl idk how it works
        this.setDirection();
    
        int playerAngle = getPlayerAngle();
        this.gamePanel.collisionHandler.checkTile(this);
    
        // If there is no collision, move
        if (!collisionEnabled) {
            // Calculate movement based on player angle
            double deltaX = this.speed * Math.cos(Math.toRadians(playerAngle));
            double deltaY = this.speed * Math.sin(Math.toRadians(playerAngle));
    
            this.worldX += deltaX;
            this.worldY += deltaY;
            
        }
    }

    public void setDirection() {
        double threshold = 45.0 / 2;
        String[] directions = {"right", "up-right", "up", "up-left", "left", "down-left", "down", "down-right"};
        double angle = getPlayerAngle();
        for (int a = 0; a < 360; a += 45) {
            if (angle > a - threshold && angle < a + threshold) {
                this.direction = directions[a / 45];
                return;
            }
        }
        this.direction = directions[7];
    }
    
    public void draw(Graphics2D g2) {
        int tileSize = this.gamePanel.tileSize;
        int playerCenterOffset = tileSize / 2;

        // screen pos = difference in pos in world + player origin offset + player center offset
        this.screenX = (this.worldX - this.gamePanel.player.worldX) + this.gamePanel.player.screenX + playerCenterOffset;
        this.screenY = (this.worldY - this.gamePanel.player.worldY) + this.gamePanel.player.screenY + playerCenterOffset;

        // Draw the enemy
        g2.setColor(isBoss ? Color.RED : Color.GREEN);
        g2.fillRect((int) screenX, (int) screenY, tileSize, tileSize);
    }

    public void updateValuesOnZoom() {
        // Update the enemy's world position and size based on zoom
        double multiplier = ((double) this.gamePanel.tileSize / this.prevTileSize);
        this.worldX *= (multiplier != 0) ? multiplier : 1;
        this.worldY *= (multiplier != 0) ? multiplier : 1;

        // Update enemy speed on zoom similar to player
        int newWorldWidth = this.gamePanel.tileSize * this.gamePanel.maxWorldCol;
        this.speed = newWorldWidth / (this.gamePanel.worldWidth / this.speed);

        // Update hitbox
        this.width = this.gamePanel.tileSize;
        this.height = this.gamePanel.tileSize;
        this.hitbox.width = this.width;
        this.hitbox.height = this.height;
        this.prevTileSize = this.gamePanel.tileSize;
    }

    public int getPlayerAngle() {
        double dx = gamePanel.player.worldX - this.worldX;
        double dy = gamePanel.player.worldY - this.worldY;
        return (int) Math.toDegrees(Math.atan2(dy, dx));
    }

    public void doRandomAction() {
        
    }
}
