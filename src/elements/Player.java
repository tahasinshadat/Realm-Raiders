package elements;

import components.Entity;
import components.KeyHandler;
import components.MouseInteractions;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import main.GamePanel;

public class Player extends Entity {
    
    GamePanel gamePanel;
    KeyHandler keyHandler;
    MouseInteractions mouse;

    public BufferedImage idleR1, idleR2, idleL1, idleL2;
    private boolean wasRight;
    private String drawDirection;

    public final double screenX;
    public final double screenY;
    public final int trueSpeed = 10;

    public ArrayList<Weapon> weaponInv = new ArrayList<Weapon>();
    public Weapon equippedWeapon;

    public final int maxShield = 300;
    public final int maxHealth = 300;

    public int shield = this.maxShield;
    public int health = this.maxHealth;

    public int frameCount;
    public boolean dead = false;

    public Player(GamePanel gamePanel, KeyHandler keyHandler, MouseInteractions mouse) {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;
        this.mouse = mouse;

        this.screenX = this.gamePanel.screenWidth / 2 - this.gamePanel.tileSize / 2;
        this.screenY = this.gamePanel.screenHeight / 2 - this.gamePanel.tileSize / 2;

        this.equippedWeapon = new Weapon(this.gamePanel, this.keyHandler, this.mouse, this);
        this.equippedWeapon.setData(5, 10, 20);
        
        weaponInv.add(this.equippedWeapon);
        weaponInv.add(new Weapon(gamePanel, keyHandler, mouse, this));
        weaponInv.get(1).setData(100, 10, 10);

        hitbox = new Rectangle();
        hitbox.x = 8;
        hitbox.y = 16;
        hitbox.width = 32;
        hitbox.height = 32;

        this.setDefaults();
        this.getPlayerImage();
    }

    public void setDefaults() {
        this.worldX = this.gamePanel.worldWidth / 2;
        this.worldY = this.gamePanel.worldHeight / 2;
        // Keep the speed relative to the zoom factor (keep the speed at 4 relative to mapSize)
        this.speed = this.gamePanel.worldWidth / (this.gamePanel.worldWidth / this.trueSpeed);
        this.diagnolSpeed = calculateDiagnolSpeed(this.speed);
        this.direction = "right";
        this.drawDirection = this.direction;
        this.wasRight = true;
    }

    // Keeps the speed the same even when moving diagnallay
    public double calculateDiagnolSpeed(double hypotenuse) {
        return hypotenuse / Math.sqrt(2);
    }

    public void getPlayerImage() {
        try {

            this.right1 = ImageIO.read(getClass().getResourceAsStream("../assets/player/player_walk_1_right.png"));
            this.right2 = ImageIO.read(getClass().getResourceAsStream("../assets/player/player_walk_2_right.png"));

            this.left1 = ImageIO.read(getClass().getResourceAsStream("../assets/player/player_walk_1_left.png"));
            this.left2 = ImageIO.read(getClass().getResourceAsStream("../assets/player/player_walk_2_left.png"));

            this.idleR1 = ImageIO.read(getClass().getResourceAsStream("../assets/player/player_idle_1_right.png"));
            this.idleR2 = ImageIO.read(getClass().getResourceAsStream("../assets/player/player_idle_2_right.png"));

            this.idleL1 = ImageIO.read(getClass().getResourceAsStream("../assets/player/player_idle_1_left.png"));
            this.idleL2 = ImageIO.read(getClass().getResourceAsStream("../assets/player/player_idle_2_left.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (keyHandler.upPressed && keyHandler.leftPressed) {
            this.direction = "up-left";
            this.drawDirection = "left";
            this.wasRight = false;

        } else if (keyHandler.upPressed && keyHandler.rightPressed) {
            this.direction = "up-right";
            this.drawDirection = "right";
            this.wasRight = true;

        } else if (keyHandler.downPressed && keyHandler.leftPressed) {
            this.direction = "down-left";
            this.drawDirection = "left";
            this.wasRight = false;

        } else if (keyHandler.downPressed && keyHandler.rightPressed) {
            this.direction = "down-right";
            this.drawDirection = "right";
            this.wasRight = true;

        } else if (keyHandler.upPressed) {
            this.direction = "up";
            if (this.wasRight) this.drawDirection = "right";
            else this.drawDirection = "left";
            
        } else if (keyHandler.downPressed) {
            this.direction = "down";
            if (this.wasRight) this.drawDirection = "right";
            else this.drawDirection = "left";

        } else if (keyHandler.leftPressed) {
            this.direction = "left";
            this.drawDirection = "left";

            this.wasRight = false;

        } else if (keyHandler.rightPressed) {
            this.direction = "right";
            this.drawDirection = "right";
            this.wasRight = true;

        } else {
            this.drawDirection = "idle";
            this.direction = "idle";
        }

        // Check For tile collisions
        this.collisionEnabled = false;
        this.gamePanel.collisionHandler.checkTile(this);

        // If there is no collision, move
        if (collisionEnabled == false) {

            switch (this.direction) {
                case "up-left":
                    this.worldY -= this.diagnolSpeed;
                    this.worldX -= this.diagnolSpeed;
                    break;
                case "up-right":
                    this.worldY -= this.diagnolSpeed;
                    this.worldX += this.diagnolSpeed;
                    break;
                case "down-left":
                    this.worldY += this.diagnolSpeed;
                    this.worldX -= this.diagnolSpeed;
                    break;
                case "down-right":
                    this.worldY += this.diagnolSpeed;
                    this.worldX += this.diagnolSpeed;
                    break;
                case "up": this.worldY -= this.speed; break;
                case "down": this.worldY += this.speed; break;
                case "right": this.worldX += this.speed; break;
                case "left": this.worldX -= this.speed; break;
                case "idle": break;
                default: break;
            }

        }

        this.spriteCounter++;
        if (spriteCounter > 10) {
            if (spriteNum == 1) {
                spriteNum++;
            } else {
                spriteNum = 1;
            }
            this.spriteCounter = 0;
        }

        for (Weapon weapon : weaponInv) weapon.update(); // update projectiles of all weapons

        if (this.frameCount % 300 == 0 && !this.dead) this.regenerateShield(); // every 5 seconds regenerate shield a little bit
        if (mouse.isLeftMouseClicked()) equippedWeapon.shoot();

        updateEquippedWeapon(); // update equipped weapon on scroll

        this.frameCount++;
    }

    public void updateEquippedWeapon() {
        int weaponIndex = weaponInv.indexOf(equippedWeapon);
        weaponIndex = (weaponIndex + mouse.wheelMoveAmount) % weaponInv.size();
        if (weaponIndex < 0) weaponIndex = weaponInv.size() + weaponIndex;

        this.equippedWeapon = weaponInv.get(weaponIndex);

        mouse.wheelMoveAmount = 0; // reset move amount after using
    }

    public void takeDamage(int damage) {
        // damage to shield first
        if (this.shield >= damage) {
            this.shield -= damage;
        } else {
            // If damage exceeds shield, subtract remaining damage from health
            int remainingDamage = damage - this.shield;
            this.shield = 0;
            this.health -= remainingDamage;
            if (this.health < 0) {
                this.health = 0; // Ensure health does not go below 0
                this.dead = true;
            }
        }
    }

    private void regenerateShield() {
        int shieldRegenRate = 20; // Adjust the regeneration rate as needed
        if (this.shield < this.maxShield) {
            this.shield += shieldRegenRate;
            if (this.shield > this.maxShield) {
                this.shield = this.maxShield; // Ensure shield does not exceed maxShield
            }
        }
    }

    public void draw(Graphics2D g2) {
        // g2.setColor(Color.white);
        // g2.fillRect((int) this.x, (int) this.y, this.gamePanel.tileSize, this.gamePanel.tileSize);

        BufferedImage image = null;

        switch (this.drawDirection) {
            case "idle":
                if (spriteNum == 1) {
                    if (this.wasRight) image = this.idleR1;
                    else image = this.idleL1;
                } else {
                    if (this.wasRight) image = this.idleR2;
                    else image = this.idleL2;
                }
                break;
            case "right":
                if (spriteNum == 1) image = this.right1;
                else image = this.right2;
                break;
            case "left":
                if (spriteNum == 1) image = this.left1;
                else image = this.left2;
                break;
        }
        // System.out.println(this.worldX + " " + this.worldY);
        g2.drawImage(image, (int) this.screenX, (int) this.screenY, this.gamePanel.tileSize, this.gamePanel.tileSize, null);
        this.equippedWeapon.draw(g2);
        for (Weapon weapon : weaponInv) weapon.drawProjectiles(g2); // draw projectiles of all weapons
    }

}
