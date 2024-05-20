package components;

import java.awt.Graphics2D;

import main.GamePanel;

public class Projectile extends Entity {
    int angle;
    GamePanel gamePanel;
    private double screenX;
    private double screenY;


    public Projectile(GamePanel gamePanel, int angle) {
        this.gamePanel = gamePanel;
        this.angle = angle; // in degrees, 0 degrees is right, counter clockwise is positive
    }

    public void delete() {

    }

    public void update() {

    }

    public void draw(Graphics2D g2) {
        int tileSize = this.gamePanel.tileSize;

        // x and y of player on screen
        this.screenX = this.gamePanel.player.screenX + tileSize/2;
        this.screenY = this.gamePanel.player.screenY + tileSize/2;
        
        double angle = getAngleToMouse();
        double deltaX = mouse.getMouseX() - this.screenX;

        if (deltaX > 0) { // Keep image upright
            g2.setColor(Color.WHITE);
        } else {
            g2.setColor(Color.RED);
        }

        // rectangle with center that is colinear to y=0 and left side is at x=0 so that it orbits player when rotating
        Rectangle rect2 = new Rectangle(tileSize/2, -tileSize/4, tileSize*2, tileSize/2); 
        g2.translate(screenX, screenY); // translates origin and therefore weapon (rect atm) origin to center of player
        g2.rotate(-Math.toRadians(angle)); // rotated so y=0 aims at mouse
        g2.draw(rect2);
        g2.fill(rect2);
    }
}

/*
TODO: 
[ ] Draw rectangle as projectile
[ ] Place bullet at end of gun
[ ] Make bullet at angle when LMB clicked
[ ] Track bullet position in world
[ ] Change bullet position in world with speed

*/