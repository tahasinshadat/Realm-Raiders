package elements;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import components.KeyHandler;
import components.MouseInteractions;
import main.GamePanel;

public class Weapon {
    
    GamePanel gamePanel;
    KeyHandler keyHandler;
    MouseInteractions mouse;
    
    public final double screenX;
    public final double screenY;

    private final int width = 60;
    private final int height = 10;
    private boolean flipped = false;

    public Weapon(GamePanel gamePanel, KeyHandler keyHandler, MouseInteractions mouse) {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;
        this.mouse = mouse;
        
        // x and y of player on screen
        this.screenX = this.gamePanel.screenWidth / 2;
        this.screenY = this.gamePanel.screenHeight / 2;
    }

    public double getAngleToMouse() {
        double deltaX = mouse.getMouseX() - this.screenX;
        double deltaY = this.screenY - mouse.getMouseY();

        double radians = Math.atan(deltaY / deltaX);
        double degrees = Math.toDegrees(radians);
        
        // System.out.println("deltaX: " + deltaX + ", deltaY: " + deltaY);

        if (deltaX < 0) {
            // offsets to give positive angle where initial side is to the right (0 degrees)
            // Quadrant 2 - degrees returns -90 to 0, this offsets negative
            // Quadrant 3 - degrees returns 0 to 90. this offsets 180 degrees
            degrees += 180; 
        } else if (deltaY < 0) {
            // offsets to give positive angle where initial side is to the right (0 degrees)
            // Quadrant 4 - degrees returns -90 to 0, this offsets negative and 270 degrees
            degrees = 360 + degrees; // (degrees is negative)
        }

        // System.out.println(degrees + " degrees");
        return degrees;
    }

    public void draw(Graphics2D g2) {
        double angle = getAngleToMouse();
        double deltaX = mouse.getMouseX() - this.screenX;

        if (deltaX > 0) { // Keep image upright
            flipped = false;
            g2.setColor(Color.WHITE);
        } else {
            flipped = true;
            g2.setColor(Color.RED);
        }

        // rectangle with center that is colinear to y=0 and left side is at x=0 so that it orbits player when rotating
        Rectangle rect2 = new Rectangle(this.gamePanel.tileSize/2, -height/2, width, height); 
        g2.translate(screenX, screenY); // translates origin and therefore weapon (rect atm) origin to center of player
        g2.rotate(-Math.toRadians(angle)); // rotated so y=0 aims at mouse
        g2.draw(rect2);
        g2.fill(rect2);
    }
}
