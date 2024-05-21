package components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import elements.Weapon;
import main.GamePanel;

public class Projectile extends Entity {
    double angle;
    GamePanel gamePanel;

    private double screenX;
    private double screenY;
    private double worldX;
    private double worldY;
    private int originalTileSize;

    private Weapon originalWeapon;
    private Entity owner;

    public Projectile(GamePanel gamePanel, double angle, Weapon originalWeapon, Entity owner) {
        this.gamePanel = gamePanel;
        this.angle = angle; // in degrees, 0 degrees is right, counter clockwise is positive
        
        this.originalWeapon = originalWeapon;
        this.owner = owner;
        this.originalTileSize = this.gamePanel.tileSize;
        // this.screenX = originalWeapon.screenX + (originalWeapon.width * Math.cos(Math.toRadians(angle)));
        // this.screenY = originalWeapon.screenY + (originalWeapon.width * -Math.sin(Math.toRadians(angle))); // down is positive

        this.worldX = this.owner.worldX + (this.originalWeapon.width * Math.cos(Math.toRadians(angle)));
        this.worldY = this.owner.worldY + (this.originalWeapon.width * -Math.sin(Math.toRadians(angle)));
    }

    public void delete() {

    }

    public void update() {

    }

    public void draw(Graphics2D g2) {
        int tileSize = this.gamePanel.tileSize;
        int playerCenterOffset = tileSize/2;

        // player worldX and worldY update on zoom, this updates projectile worldX worldY as well
        updatePositionOnZoom();

        // screen pos = difference in pos in world + player origin offset + player center offset
        this.screenX = (this.worldX - this.gamePanel.player.worldX) + this.gamePanel.player.screenX + playerCenterOffset;
        this.screenY = (this.worldY - this.gamePanel.player.worldY) + this.gamePanel.player.screenY + playerCenterOffset;

        Rectangle rect2 = new Rectangle(-tileSize/2, -tileSize/2, tileSize, tileSize); 
        g2.setColor(Color.WHITE);
        
        g2.translate(screenX, screenY); 
        g2.rotate(-Math.toRadians(this.angle));
        g2.draw(rect2);
        g2.fill(rect2);
        g2.rotate(Math.toRadians(this.angle)); // rotate back
        g2.translate(-screenX, -screenY); // translate back
    }

    public void updatePositionOnZoom() {
        // see gamePanel zoom method
        // updating projectile worldX and worldY on zoom similar to player
        double multiplier = ((double)this.gamePanel.tileSize / this.originalTileSize);
        this.worldX *= multiplier;
        this.worldY *= multiplier;
        this.originalTileSize = this.gamePanel.tileSize;
    }
}

/*
TODO: 
[X] Draw rectangle as projectile
[X] Place bullet at end of gun
[X] Make bullet at angle when LMB clicked
[X] Track bullet position in world
    [X] Get initial world position
[ ] Change bullet position in world with speed

*/