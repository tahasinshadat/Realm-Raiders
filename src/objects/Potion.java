package objects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import main.GamePanel;

public class Potion extends GameObject {
    public Potion(GamePanel gamePanel, int worldX, int worldY) {
        this.gamePanel = gamePanel;
        this.worldX = worldX;
        this.worldY = worldY;

        this.width = this.gamePanel.tileSize/4;
        this.height = this.gamePanel.tileSize/4;

        this.image = this.gamePanel.assetManager.loadImage("../assets/potions/empty.png");
    }

    @Override
    public void interact() {
        this.pickup();
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        super.drawName(g2);
    }

    @Override
    public String toString() {
        return "Empty Potion";
    }
}
