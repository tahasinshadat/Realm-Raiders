package elements;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import components.Entity;
import components.KeyHandler;
import main.GamePanel;

public class Player extends Entity {
    
    GamePanel gamePanel;
    KeyHandler keyHandler;
    public BufferedImage idleR1, idleR2, idleL1, idleL2;
    private boolean wasRight;

    public Player(GamePanel gamePanel, KeyHandler keyHandler) {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;
        setDefaults();
        getPlayerImage();
    }

    public void setDefaults() {
        this.x = 100;
        this.y = 100;
        this.speed = 4 ;
        this.diagnolSpeed = 2.83;
        this.direction = "right";
        this.wasRight = true;
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
            this.y -= this.diagnolSpeed;
            this.x -= this.diagnolSpeed;
            this.direction = "left";
            this.wasRight = false;

        } else if (keyHandler.upPressed && keyHandler.rightPressed) {
            this.y -= this.diagnolSpeed;
            this.x += this.diagnolSpeed;
            this.direction = "right";
            this.wasRight = true;

        } else if (keyHandler.downPressed && keyHandler.leftPressed) {
            this.y += this.diagnolSpeed;
            this.x -= this.diagnolSpeed;
            this.direction = "left";
            this.wasRight = false;

        } else if (keyHandler.downPressed && keyHandler.rightPressed) {
            this.y += this.diagnolSpeed;
            this.x += this.diagnolSpeed;
            this.direction = "right";
            this.wasRight = true;

        } else if (keyHandler.upPressed) {
            this.y -= this.speed;
            if (this.wasRight) this.direction = "right";
            else this.direction = "left";

        } else if (keyHandler.downPressed) {
            this.y += this.speed;
            if (this.wasRight) this.direction = "right";
            else this.direction = "left";

        } else if (keyHandler.leftPressed) {
            this.x -= this.speed;
            this.direction = "left";
            this.wasRight = false;

        } else if (keyHandler.rightPressed) {
            this.x += this.speed;
            this.direction = "right";
            this.wasRight = true;

        } else {
            this.direction = "idle";
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
    }

    public void draw(Graphics2D g2) {
        // g2.setColor(Color.white);
        // g2.fillRect((int) this.x, (int) this.y, this.gamePanel.tileSize, this.gamePanel.tileSize);

        BufferedImage image = null;

        switch (direction) {
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

        g2.drawImage(image, (int) this.x, (int) this.y, this.gamePanel.tileSize, this.gamePanel.tileSize, null);
    }

}
