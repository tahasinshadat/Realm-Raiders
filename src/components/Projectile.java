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

    private Weapon originalWeapon;

    public Projectile(GamePanel gamePanel, double angle, Weapon originalWeapon) {
        this.gamePanel = gamePanel;
        this.angle = angle; // in degrees, 0 degrees is right, counter clockwise is positive
        
        this.originalWeapon = originalWeapon;
        this.screenX = originalWeapon.screenX + (originalWeapon.width * Math.cos(Math.toRadians(angle)));
        this.screenY = originalWeapon.screenY + (originalWeapon.width * -Math.sin(Math.toRadians(angle))); // 0, 0 is top left
    }

    public void delete() {

    }

    public void update() {

    }

    public void draw(Graphics2D g2) {
        int tileSize = this.gamePanel.tileSize;
        
        Rectangle rect2 = new Rectangle(-tileSize/2, -tileSize/2, tileSize, tileSize); 
        g2.setColor(Color.WHITE);
        
        g2.translate(screenX, screenY); 
        g2.rotate(-Math.toRadians(this.angle));
        g2.draw(rect2);
        g2.fill(rect2);
        g2.rotate(Math.toRadians(this.angle)); // rotate back
        g2.translate(-screenX, -screenY); // translate back
    }
}

/*
TODO: 
[X] Draw rectangle as projectile
[X] Place bullet at end of gun
[X] Make bullet at angle when LMB clicked
[ ] Track bullet position in world
[ ] Change bullet position in world with speed

*/