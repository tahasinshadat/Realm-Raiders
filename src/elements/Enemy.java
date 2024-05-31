package elements;

import components.Entity;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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
    public int trueSpeed;
    public int frameCounter = 0;
    public boolean chasing = true;
    double angle = 0;
    private boolean gotNewAngle = false;

    public int barWidth = this.size;
    public int barHeight = this.size / 10;
    public boolean isDead = false;

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
            this.size = this.gamePanel.tileSize*2;
            this.health = 300;
            this.maxHealth = 300;
            this.damage = 10;
            this.defense = 30;
            this.speed = 8;
            this.trueSpeed = this.speed;
            this.attackSpeed = 5;
        } else {
            this.size = this.gamePanel.tileSize;
            if (this.type == 1) { // melee unit
                this.health = 100;
                this.maxHealth = 100;
                this.damage = 7;
                this.defense = 20;
                this.speed = 7;
                this.trueSpeed = this.speed;
                this.attackSpeed = 10;
            } else if (this.type == 2) { // ranged unit
                this.health = 50;
                this.maxHealth = 50;
                this.damage = 4;
                this.defense = 10;
                this.speed = 5;
                this.trueSpeed = this.speed;
                this.attackSpeed = 8;
            }
        }
        this.barWidth = this.size;
        this.barHeight = this.size / 10;
    }

    public void update() {
        this.updateValuesOnZoom();

        if (this.health <= 0) this.isDead = true;

        // Update direction based on angle for collisons - ngl idk how it works
        this.setDirection();
    
        int playerAngle = getPlayerAngle();
        this.collisionEnabled = false;
        this.gamePanel.collisionHandler.checkTile(this);

        // Check for collision with player
        Rectangle enemyHitbox = new Rectangle((int) this.worldX, (int) this.worldY, this.hitbox.width, this.hitbox.height);
        Rectangle playerHitbox = new Rectangle((int) this.gamePanel.player.worldX, (int) this.gamePanel.player.worldY, this.gamePanel.player.hitbox.width, this.gamePanel.player.hitbox.height);
        if (enemyHitbox.intersects(playerHitbox)) {
            this.gamePanel.player.takeDamage(this.damage);
        }
    
        // If there is no collision, move
        if (!collisionEnabled) {
            this.chasing = true; // true until overriden after 5 secs

            if (this.frameCounter > this.gamePanel.FPS*5) {
                // after 5 seconds of chasing, stop
                this.chasing = false;
            }

            if (this.frameCounter > this.gamePanel.FPS*10) {
                // after not chasing for 5 more seconds, reset
                this.gotNewAngle = false; // reset for next no chase time
                this.frameCounter = 0;
            }
            
            if (this.chasing) {
                // Calculate movement based on player angle
                this.angle = playerAngle;
                double deltaX = this.speed * Math.cos(Math.toRadians(angle));
                double deltaY = this.speed * -Math.sin(Math.toRadians(angle));
        
                this.worldX += deltaX;
                this.worldY += deltaY;
            } else { // not chasing mode
                if (!this.gotNewAngle) { // get new angle only once
                    this.angle = this.getRandomAngle();
                    this.gotNewAngle = true;
                }
                // move in new angle
                double deltaX = this.speed * Math.cos(Math.toRadians(angle));
                double deltaY = this.speed * -Math.sin(Math.toRadians(angle));
        
                this.worldX += deltaX;
                this.worldY += deltaY;
            }
            
        } else { // collided
            while (collisionEnabled == true) {
                // get new angle that does not cause collision
                // this.angle = (this.angle + 10) % 360;
                this.angle = this.getRandomAngle();
                this.setDirection();
            
                this.collisionEnabled = false;
                this.gamePanel.collisionHandler.checkTile(this);
            }

            // travel in this new direction for a frame to not get stuck on wall
            double deltaX = this.speed * Math.cos(Math.toRadians(angle));
            double deltaY = this.speed * -Math.sin(Math.toRadians(angle));
    
            this.worldX += deltaX;
            this.worldY += deltaY;
        }
        this.frameCounter++;
    }

    public void setDirection() {
        double threshold = 45.0 / 2;
        String[] directions = {"right", "up-right", "up", "up-left", "left", "down-left", "down", "down-right"};
        double angle = this.angle;
        for (int a = 0; a < 360; a += 45) {
            if (angle > a - threshold && angle < a + threshold) {
                this.direction = directions[a / 45];
                return;
            }
        }
        this.direction = directions[7];
    }
    
    public void draw(Graphics2D g2) {
        int playerCenterOffset = this.size / 2;
        int gap = 7; // gap between health bar and enemy hitbox

        // screen pos = difference in pos in world + player origin offset + player center offset
        this.screenX = (this.worldX - this.gamePanel.player.worldX) + this.gamePanel.player.screenX + playerCenterOffset;
        this.screenY = (this.worldY - this.gamePanel.player.worldY) + this.gamePanel.player.screenY + playerCenterOffset;

        // Draw the enemy
        g2.setColor(isBoss ? Color.GREEN : Color.BLUE);
        g2.fillRect((int) screenX - this.size / 2, (int) screenY - this.size / 2, this.size, this.size);

        // Draw the enemy name
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics metrics = g2.getFontMetrics(g2.getFont());
        int nameWidth = metrics.stringWidth(this.name);
        g2.setColor(Color.WHITE);
        g2.drawString(this.name, (int) (screenX - nameWidth / 2), (int) (screenY - this.size / 2 - 10));

        // Draw the health bar background
        g2.setColor(Color.GRAY);
        g2.fillRect((int) screenX - this.size / 2, (int) (screenY - this.size / 2 - gap), this.barWidth, this.barHeight);

        // Draw the health bar
        int healthBarWidth = (int) ((double) this.health / this.maxHealth * barWidth);
        g2.setColor(Color.RED);
        g2.fillRect((int) screenX - this.size / 2, (int) (screenY - this.size / 2 - gap), healthBarWidth, this.barHeight);
    }

    public void updateValuesOnZoom() {
        // Update the enemy's world position and size based on zoom
        double multiplier = ((double) this.gamePanel.tileSize / this.prevTileSize);
        this.worldX *= (multiplier != 0) ? multiplier : 1;
        this.worldY *= (multiplier != 0) ? multiplier : 1;

        // Update enemy speed on zoom similar to player
        int newWorldWidth = this.gamePanel.tileSize * this.gamePanel.maxWorldCol;
        this.speed = newWorldWidth / (this.gamePanel.worldWidth / this.trueSpeed);

        // Update hitbox
        this.size = (this.isBoss) ? this.gamePanel.tileSize * 2 : this.gamePanel.tileSize; 

        this.width = this.size;
        this.height = this.size;
        this.hitbox.width = this.width;
        this.hitbox.height = this.height;
        this.prevTileSize = this.gamePanel.tileSize;
        this.barWidth = this.size;
        this.barHeight = this.size / 10;
    }

    public int getPlayerAngle() {
        double dx = gamePanel.player.worldX - this.worldX;
        double dy = this.worldY - gamePanel.player.worldY; // down is positive for y

        double radians = Math.atan(dy / dx);
        double degrees = Math.toDegrees(radians);
        
        // System.out.println("deltaX: " + dx + ", deltaY: " + dy);

        if (dx < 0) {
            // offsets to give positive angle where initial side is to the right (0 degrees)
            // Quadrant 2 - degrees returns -90 to 0, this offsets negative
            // Quadrant 3 - degrees returns 0 to 90. this offsets 180 degrees
            degrees += 180; 
        } else if (dy < 0) {
            // offsets to give positive angle where initial side is to the right (0 degrees)
            // Quadrant 4 - degrees returns -90 to 0, this offsets negative and 270 degrees
            degrees = 360 + degrees; // (degrees is negative)
        }

        return (int) degrees;
    }

    public Rectangle getHitBox() {
        return new Rectangle((int) this.worldX, (int) this.worldY, this.hitbox.width, this.hitbox.height);
    }

    public void takeDamage(int damage) {
        this.health -= damage - this.defense/10;
    }

    // public void doRandomAction() {
    //     if (this.angle == this.getPlayerAngle()) {
    //         this.angle = getRandomAngle();
    //     }
          
    //     double deltaX = this.speed * Math.cos(Math.toRadians(angle));
    //     double deltaY = this.speed * -Math.sin(Math.toRadians(angle));

    //     this.worldX += deltaX;
    //     this.worldY += deltaY;
    // }

    public double getRandomAngle() {
        return (Math.random() * 360);
    }

}
