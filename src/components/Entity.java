package components;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {

    public double worldX, worldY;
    public double speed, diagnolSpeed; 

    public BufferedImage up1, up2, down1, down2, right1, right2, left1, left2;
    public String direction;

    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Rectangle hitbox;
    public boolean collisionEnabled = false;
}
