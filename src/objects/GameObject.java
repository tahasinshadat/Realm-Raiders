package objects;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import main.GamePanel;

public class GameObject {
    public GamePanel gamePanel;

    public BufferedImage image; // TO BE SET IN CHILD
    public String name;
    public boolean collision = false;
    public int worldX, worldY;
    public double screenX, screenY;
    public int prevTileSize = 48;

    public int width, height;

    public boolean onGround = true;

    public void interact() {
        // to be overriden
        System.out.println("Interacted with " + this);
    }

    public GameObject pickup() {
        this.onGround = false;
        this.gamePanel.obj.remove(this);
        return this;
    }

    public GameObject drop(int x, int y) {
        this.onGround = true;
        this.worldX = x;
        this.worldY = y;
        this.gamePanel.obj.add(this);
        return this;
    }

    public boolean canInteract(double x, double y) {
        return (this.inRange(x, worldX, 1*this.gamePanel.tileSize) && this.inRange(y, worldY, (int) (1.5*this.gamePanel.tileSize)));
    }

    public boolean inRange(double val, int target, int range) {
        return (Math.abs(target - val) <= range);
    }

    public void draw(Graphics2D g2) {
        // System.out.println("Drawing " + image + " (" + width + ", " + height + ")\n" + 
        //                    " at " + worldX + ", " + worldY + 
        //                    "\nPlayer at " + this.gamePanel.player.worldX + ", " + this.gamePanel.player.worldY);

        this.updateValuesOnZoom();
        // screen pos = difference in pos in world + player origin offset + player center offset
        this.screenX = (this.worldX - this.gamePanel.player.worldX) + this.gamePanel.player.screenX + this.gamePanel.tileSize/2;
        this.screenY = (this.worldY - this.gamePanel.player.worldY) + this.gamePanel.player.screenY + this.gamePanel.tileSize/2;
        
        g2.translate(screenX, screenY);
        
        g2.drawImage(this.image, 
                    -this.width/2, -this.height/2, 
                     this.width, this.height, 
                     null);

        g2.translate(-screenX, -screenY); // translate back
                     
    }

    public void drawName(Graphics2D g2) {
        this.updateValuesOnZoom();
        // screen pos = difference in pos in world + player origin offset + player center offset
        this.screenX = (this.worldX - this.gamePanel.player.worldX) + this.gamePanel.player.screenX + this.gamePanel.tileSize/2;
        this.screenY = (this.worldY - this.gamePanel.player.worldY) + this.gamePanel.player.screenY + this.gamePanel.tileSize/2;

        g2.translate(screenX, screenY);

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics metrics = g2.getFontMetrics(g2.getFont());
        int strWidth = metrics.stringWidth(this.toString());

        g2.drawString(this.toString(), -strWidth/2, -15);

        g2.translate(-screenX, -screenY); // translate back
    }

    public void updateValuesOnZoom() {
        double multiplier = ((double)this.gamePanel.tileSize / this.prevTileSize);
        this.prevTileSize = this.gamePanel.tileSize;

        this.worldX *= multiplier;
        this.worldY *= multiplier;
        this.height = this.gamePanel.tileSize/2;
        this.width = this.height * (this.image.getWidth() / this.image.getHeight());
    }
}
