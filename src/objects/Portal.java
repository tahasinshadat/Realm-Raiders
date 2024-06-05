package objects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import main.GamePanel;

public class Portal extends GameObject {

    public boolean utilizedPortal = false;

    public Portal(GamePanel gamePanel, int worldX, int worldY) {
        this.gamePanel = gamePanel;
        this.worldX = worldX;
        this.worldY = worldY;
        this.name = "Portal";
        this.collision = false;
        this.image = this.gamePanel.assetManager.loadImage("../assets/portals/blue_portal.png");
        this.width = this.gamePanel.tileSize * 2;
        this.height = this.gamePanel.tileSize * 3;
    }

    public void update() {
        updateValuesOnZoom();
        // keyhandler.interactionButtonPressed always false for some reason
        // if (this.gamePanel.keyHandler.interactionButtonPressed && this.canInteract(this.gamePanel.player.worldX, this.gamePanel.player.worldY) && !this.utilizedPortal) {
        //     this.gamePanel.gameState = GamePanel.LOAD_STATE;
        //     this.gamePanel.player.clearProjectiles();
        //     this.utilizedPortal = true;
        // }
    }

    @Override
    public void interact() {
        if (!this.utilizedPortal) {
            this.gamePanel.gameState = GamePanel.LOAD_STATE;
            this.gamePanel.player.clearProjectiles();
            this.utilizedPortal = true;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!this.utilizedPortal) super.draw(g2);
    }

    @Override
    public void updateValuesOnZoom() {
        double multiplier = ((double)this.gamePanel.tileSize / this.prevTileSize);
        this.prevTileSize = this.gamePanel.tileSize;

        this.worldX *= multiplier;
        this.worldY *= multiplier;
        this.width = this.gamePanel.tileSize * 2;
        this.height = this.gamePanel.tileSize * 3;
    }
}
