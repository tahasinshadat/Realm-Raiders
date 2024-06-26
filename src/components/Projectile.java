package components;

import elements.Enemy;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;
import main.GamePanel;
import objects.Weapon;

public class Projectile extends Entity {
    double angle;
    GamePanel gamePanel;

    private double screenX;
    private double screenY;
    private int ownerCenterOffset;
    private int prevTileSize;
    private int width;
    private int height;
    public BufferedImage image;

    private Weapon originalWeapon;
    private Entity owner;
    public boolean active = true;

    public Projectile(GamePanel gamePanel, double angle, Weapon originalWeapon, Entity owner) {
        this.gamePanel = gamePanel;
        this.angle = angle; // in degrees, 0 degrees is right, counter clockwise is positive
        
        this.originalWeapon = originalWeapon;
        this.owner = owner;
        this.prevTileSize = this.gamePanel.tileSize;
        
        this.speed = this.originalWeapon.weaponProjectileSpeed;

        this.ownerCenterOffset = this.gamePanel.tileSize/2;
        this.worldX = this.owner.worldX + ((this.originalWeapon.width + this.ownerCenterOffset) * Math.cos(Math.toRadians(angle)));
        this.worldY = this.owner.worldY + ((this.originalWeapon.width + this.ownerCenterOffset) * -Math.sin(Math.toRadians(angle)));


        this.width = this.gamePanel.tileSize/10;
        this.height = this.gamePanel.tileSize/10;

        this.hitbox = new Rectangle();

        this.setImage();
        this.updateValuesOnZoom(); // set hitbox values

        this.setDirection();
    }

    public Projectile(GamePanel gamePanel, double angle, Weapon originalWeapon, Entity owner, BufferedImage image) {
        this.gamePanel = gamePanel;
        this.angle = angle; // in degrees, 0 degrees is right, counter clockwise is positive
        
        this.originalWeapon = originalWeapon;
        this.owner = owner;
        this.prevTileSize = this.gamePanel.tileSize;
        
        this.speed = this.originalWeapon.weaponProjectileSpeed;

        this.ownerCenterOffset = this.gamePanel.tileSize/2;
        this.worldX = this.owner.worldX + ((this.originalWeapon.width + this.ownerCenterOffset) * Math.cos(Math.toRadians(angle)));
        this.worldY = this.owner.worldY + ((this.originalWeapon.width + this.ownerCenterOffset) * -Math.sin(Math.toRadians(angle)));


        this.width = this.gamePanel.tileSize/10;
        this.height = this.gamePanel.tileSize/10;

        this.hitbox = new Rectangle();

        this.setImage(image);
        this.updateValuesOnZoom(); // set hitbox values

        this.setDirection();
    }

    public void setDirection() {
        double threshold = 45.0/2;
        String[] directions = {"right", "up-right", "up", "up-left", "left", "down-left", "down", "down-right"};
        for (int a = 0; a < 360; a += 45) {
            if (angle > a - threshold && angle < a + threshold) {
                this.direction = directions[a/45];
                return;
            }
        }
        this.direction = directions[7];
    }

    public void deactivate() {
        this.active = false;
    }

    public void update() {
        this.updateValuesOnZoom();

        // Check For tile collisions
        this.collisionEnabled = false;
        this.gamePanel.collisionHandler.checkTile(this);

        // If there is no collision, move
        if (collisionEnabled == false) {
            this.worldX += this.speed * Math.cos(Math.toRadians(angle));
            this.worldY += this.speed * -Math.sin(Math.toRadians(angle));
        } else {
            this.deactivate();
        }

        Rectangle worldHitbox = new Rectangle((int)this.worldX+this.hitbox.x, (int)this.worldY+this.hitbox.y, this.hitbox.width, this.hitbox.height);
        if (this.gamePanel.player == this.owner) { // player owner
            for (int i = 0; i < this.gamePanel.enemies.size(); i++) {
                if (((Enemy) this.gamePanel.enemies.get(i)).getHitBox().intersects(worldHitbox)) {
                    ((Enemy)this.gamePanel.enemies.get(i)).takeDamage( (int) this.originalWeapon.weaponDamage);
                    this.deactivate();
                }
            }
        } else { // enemy owner
            Rectangle playerHitbox = this.gamePanel.player.hitbox;
            Rectangle playerWorldHitbox = new Rectangle((int)this.gamePanel.player.worldX+playerHitbox.x, 
                                                        (int)this.gamePanel.player.worldY+playerHitbox.y, 
                                                        playerHitbox.width, 
                                                        playerHitbox.height);
            if (worldHitbox.intersects(playerWorldHitbox)) {
                this.gamePanel.player.takeDamage((int)this.originalWeapon.weaponDamage);
                this.deactivate();
            }
        }
    }

    public void setImage() {
        try {
            if (this.gamePanel.player == this.owner) 
                this.image = ImageIO.read(getClass().getResourceAsStream("../assets/weapons/bullet.png"));
            else 
                this.image = ImageIO.read(getClass().getResourceAsStream("../assets/weapons/enemy_bullet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void draw(Graphics2D g2) {
        // screen pos = difference in pos in world + player origin offset + player center offset
        this.screenX = (this.worldX - this.gamePanel.player.worldX) + this.gamePanel.player.screenX + this.ownerCenterOffset;
        this.screenY = (this.worldY - this.gamePanel.player.worldY) + this.gamePanel.player.screenY + this.ownerCenterOffset;

        // Rectangle rect2 = new Rectangle(-this.width/2, -this.height/2, this.width, this.height); 
        // g2.setColor(Color.YELLOW);a
        
        g2.translate(screenX, screenY); 
        g2.rotate(-Math.toRadians(this.angle));
        // g2.draw(rect2);
        // g2.fill(rect2);

        g2.drawImage(this.image, 
                    -this.width/2, -this.height/2, 
                     this.width, this.height, 
                     null);
        
        // HITBOX
        // g2.setColor(Color.RED);
        // g2.fillRect(this.hitbox.x - ownerCenterOffset, this.hitbox.y - ownerCenterOffset, this.hitbox.width, this.hitbox.height);

        g2.rotate(Math.toRadians(this.angle)); // rotate back
        g2.translate(-screenX, -screenY); // translate back

    }

    public void updateValuesOnZoom() {
        // see gamePanel zoom method
        // updating projectile worldX and worldY on zoom similar to player
        double multiplier = ((double)this.gamePanel.tileSize / this.prevTileSize);
        this.worldX *= multiplier;
        this.worldY *= multiplier;

        // updating projectile speed on zoom similar to player
        int newWorldWidth = this.gamePanel.tileSize * this.gamePanel.maxWorldCol;
        this.speed = newWorldWidth / (this.gamePanel.worldWidth / this.originalWeapon.weaponProjectileSpeed);


        // HITBOX

        // worldX and worldY is top left, does not account for ownerCenterOffset
        // this corrects hitbox location
        this.ownerCenterOffset = this.gamePanel.tileSize/2;

        this.hitbox.x = this.ownerCenterOffset; 
        this.hitbox.y = this.ownerCenterOffset;

        // 48 is original tileSize
        this.width = this.gamePanel.tileSize/4;
        this.height = this.gamePanel.tileSize/4;

        this.hitbox.width = this.width;
        this.hitbox.height = this.height;

        this.prevTileSize = this.gamePanel.tileSize;
    }
}

/*
TODO: 
[X] Draw rectangle as projectile
[X] Place bullet at end of gun
[X] Make bullet at angle when LMB clicked
[X] Track bullet position in world
    [X] Get initial world position
[X] Change bullet position in world with speed
[X] Detect collisions
[X] Delete projectile on collision
*/