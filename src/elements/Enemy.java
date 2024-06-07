package elements;

import components.Entity;
import components.Projectile;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.GamePanel;
import objects.Weapon;

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
    public int speed;
    public double attackSpeed;
    public int size;
    public int trueSpeed;
    public int frameCounter = 0;
    public boolean chasing = true;
    double angle = 0;
    private boolean gotNewAngle = false;
    private int cooldown;

    public int barWidth = this.size;
    public int barHeight = this.size / 10;
    public boolean isDead = false;

    private Weapon weapon;
    
    private String drawDirection;
    private boolean wasRight;
    public static BufferedImage bulletImage;

    public static BufferedImage bossLeft;
    public static BufferedImage bossRight;
    public static BufferedImage meleeLeft1;
    public static BufferedImage meleeLeft2;
    public static BufferedImage meleeRight1;
    public static BufferedImage meleeRight2;
    
    public static BufferedImage rangedLeft1;
    public static BufferedImage rangedLeft2;
    public static BufferedImage rangedRight1;
    public static BufferedImage rangedRight2;
    public static BufferedImage towerImage;

    // Enemy Info
    public static int enemyTypes = 3;
    public static String[][] enemyNames = {
        { // Final Boss Names
            "Doombringer",
            "Shadowfiend",
            "Chaos Lord",
            "Nightmare King",
            "Deathbringer",
            "Hellfire Demon",
            "Void Reaper",
            "Abyssal Overlord",
            "Eternal Warden",
            "Apocalypse"
        },
        { // Melee Enemies
            "Goblin",
            "Orc",
            "Troll",
            "Bandit",
            "Barbarian",
            "Warrior",
            "Knight",
            "Berserker",
            "Swordsman",
            "Brute"
        },
        { // Ranged Enemies
            "Archer",
            "Crossbowman",
            "Hunter",
            "Marksman",
            "Sniper",
            "Bowman",
            "Ranger",
            "Sharpshooter",
            "Slinger",
            "Javelineer"
        },
        { // Stationary Enemies
            "Watchtower",
            "Turret",
            "Catapult",
            "Ballista",
            "Siege Tower",
            "Guard Post",
            "Cannon",
            "Arrow Trap",
            "Magic Tower",
            "Artillery"
        }
    };

    public Enemy(GamePanel gamePanel, int startX, int startY, int enemyType) {
        this.gamePanel = gamePanel;
        this.worldX = startX;
        this.worldY = startY;
        this.type = enemyType;
        this.name = enemyNames[this.type][this.randomNum(0, enemyNames[this.type].length-1)];
        this.setDefaults();
        this.prevTileSize = this.gamePanel.tileSize;

        this.width = this.gamePanel.tileSize;
        this.height = this.gamePanel.tileSize;

        this.hitbox = new Rectangle();
        this.updateValuesOnZoom();
    }

    public Enemy(GamePanel gamePanel, int startX, int startY, int enemyType, String enemyName) {
        this.gamePanel = gamePanel;
        this.worldX = startX;
        this.worldY = startY;
        this.type = enemyType;
        this.name = enemyName;
        this.setDefaults();
        this.prevTileSize = this.gamePanel.tileSize;

        this.width = this.gamePanel.tileSize;
        this.height = this.gamePanel.tileSize;

        this.hitbox = new Rectangle();
        this.updateValuesOnZoom();
    }

    public void setDefaults() {

        switch (this.type) {
            case 0 -> { // boss
                this.size = this.gamePanel.tileSize*2;
                this.health = 300;
                this.maxHealth = 300;
                this.damage = 10;
                this.defense = 30;
                this.speed = 8;
                this.trueSpeed = this.speed;
                this.attackSpeed = 5;
                this.right1 = bossRight;
                this.right2 = bossRight;
                this.left1 = bossLeft;
                this.left2 = bossLeft;
            }
            case 1 -> { // melee unit
                this.health = 100;
                this.maxHealth = 100;
                this.damage = 7;
                this.defense = 20;
                this.speed = 6;
                this.trueSpeed = this.speed;
                this.attackSpeed = 10;
            }
            case 2 -> { // ranged unit
                this.health = 50;
                this.maxHealth = 50;
                this.damage = 4;
                this.defense = 10;
                this.speed = 4;
                this.trueSpeed = this.speed;
                this.attackSpeed = 1;
            }
            case 3 -> { // no movement AOE unit
                this.health = 50;
                this.maxHealth = 50;
                this.damage = 4;
                this.defense = 10;
                this.speed = 0;
                this.trueSpeed = this.speed;
                this.attackSpeed = 0.33;
            }
            default -> {}
        }

        this.weapon = new Weapon(gamePanel, this.gamePanel.keyHandler, this.gamePanel.mouse, this);
        this.weapon.setData(3, 3, this.damage*5);

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
        if (this.type == 1 || this.type == 0) { // Only for melee units
            Rectangle enemyHitbox = new Rectangle((int) this.worldX, (int) this.worldY, this.hitbox.width, this.hitbox.height);
            Rectangle playerHitbox = new Rectangle((int) this.gamePanel.player.worldX, (int) this.gamePanel.player.worldY, this.gamePanel.player.hitbox.width, this.gamePanel.player.hitbox.height);
            if (enemyHitbox.intersects(playerHitbox) && this.canAttack()) {
                this.gamePanel.player.takeDamage(this.damage);
                cooldown = 0;
            }

        } 
        if (this.type == 0 || this.type == 2 || this.type == 3) { // ranged units
            // update projectiles
            this.weapon.update();
            this.weapon.angle = this.getPlayerAngle();

            if (this.canAttack()) {
                this.weapon.shoot(bulletImage);
                cooldown = 0;
            }
        }

        
        cooldown++;
    
        // If there is no collision, move
        if (!this.collisionEnabled) {
            this.chasing = true; // true until overriden after 5 secs

            if (this.frameCounter > this.gamePanel.FPS* ((this.type == 1 || this.type == 0) ? 5 : 0)) { 
                // after 5 seconds of chasing, stop (only for melee units)
                this.chasing = false;
            }

            if (this.frameCounter > this.gamePanel.FPS*10) {
                // after not chasing for 5 more seconds, reset
                this.gotNewAngle = false; // reset for next no chase time
                this.frameCounter = 0;
                this.chasing = true;
            }
            
            if (this.chasing) {
                // Calculate movement based on player angle
                this.angle = playerAngle;
                double deltaX = this.speed * Math.cos(Math.toRadians(this.angle));
                double deltaY = this.speed * -Math.sin(Math.toRadians(this.angle));
        
                this.worldX += deltaX;
                this.worldY += deltaY;
            } else { // not chasing mode
                if (!this.gotNewAngle) { // get new angle only once
                    this.angle = this.getRandomAngle();
                    this.gotNewAngle = true;
                }
                // move in new angle
                double deltaX = this.speed * Math.cos(Math.toRadians(this.angle));
                double deltaY = this.speed * -Math.sin(Math.toRadians(this.angle));
        
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

    public boolean canAttack() {
        return this.cooldown > this.gamePanel.FPS / this.attackSpeed;
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

    public BufferedImage getImage() {
        if (this.direction.equals("up-left")) {
            this.drawDirection = "left";
            this.wasRight = false;

        } else if (this.direction.equals("up-right")) {
            this.drawDirection = "right";
            this.wasRight = true;

        } else if (this.direction.equals("down-left")) {
            this.drawDirection = "left";
            this.wasRight = false;

        } else if (this.direction.equals("down-right")) {
            this.drawDirection = "right";
            this.wasRight = true;

        } else if (this.direction.equals("up") ){
            if (this.wasRight) this.drawDirection = "right";
            else this.drawDirection = "left";
            
        } else if (this.direction.equals("down") ){
            if (this.wasRight) this.drawDirection = "right";
            else this.drawDirection = "left";

        } else if (this.direction.equals("left") ){
            this.drawDirection = "left";
            this.wasRight = false;

        } else if (this.direction.equals("right") ){
            this.drawDirection = "right";
            this.wasRight = true;
        }
        return getImageFromDirection(drawDirection);
    }

    public BufferedImage getImageFromDirection(String drawDirection) {
        if (this.type == 0) {
            if (drawDirection.equals("left")) return bossLeft;
            else return bossRight;
        } else if (this.type == 1) {
            if (drawDirection.equals("left")) {
                if (spriteNum == 1) return meleeLeft1;
                else return meleeLeft2;
            } else {
                if (spriteNum == 1) return meleeRight1;
                else return meleeRight2;
            }
        } else if (this.type == 2) {
            if (drawDirection.equals("left")) {
                if (spriteNum == 1) return rangedLeft1;
                else return rangedLeft2;
            } else {
                if (spriteNum == 1) return rangedRight1;
                else return rangedRight2;
            }
        } else {
            return towerImage;
        }
    }
    
    public void draw(Graphics2D g2) {
        int playerCenterOffset = this.size / 2;
        int gap = 7; // gap between health bar and enemy hitbox

        // screen pos = difference in pos in world + player origin offset + player center offset
        this.screenX = (this.worldX - this.gamePanel.player.worldX) + this.gamePanel.player.screenX + playerCenterOffset;
        this.screenY = (this.worldY - this.gamePanel.player.worldY) + this.gamePanel.player.screenY + playerCenterOffset;

        // Draw the enemy
        // g2.setColor(this.type == 0 ? Color.GREEN : Color.BLUE);
        // g2.fillRect((int) screenX - this.size / 2, (int) screenY - this.size / 2, this.size, this.size);
        g2.drawImage(getImage(), (int) screenX - this.size / 2, (int) screenY - this.size / 2, this.size, this.size, null);
        
        this.spriteCounter++;
        if (spriteCounter > 10) {
            if (spriteNum == 1) {
                spriteNum++;
            } else {
                spriteNum = 1;
            }
            this.spriteCounter = 0;
        }

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

        // draw projectiles
        if (this.type == 0 || this.type == 2 || this.type == 3) {
            this.weapon.drawProjectiles(g2);
        }
    }

    public void updateValuesOnZoom() {
        // Update the enemy's world position and size based on zoom
        double multiplier = ((double) this.gamePanel.tileSize / this.prevTileSize);
        this.worldX *= (multiplier != 0) ? multiplier : 1;
        this.worldY *= (multiplier != 0) ? multiplier : 1;

        // Update enemy speed on zoom similar to player
        int newWorldWidth = this.gamePanel.tileSize * this.gamePanel.maxWorldCol;
        if (this.trueSpeed != 0)
            this.speed = newWorldWidth / (this.gamePanel.worldWidth / this.trueSpeed);
        else 
            this.speed = 0;

        // Update hitbox
        this.size = (this.type == 0) ? this.gamePanel.tileSize * 2 : this.gamePanel.tileSize; 

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

    private int randomNum(int min, int max) { // Inclusive
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

}
