package objects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import main.GamePanel;

public class Chest extends GameObject {

    public boolean chestOpened = false;
    private BufferedImage closedChestImage;
    private BufferedImage openedChestImage;
	private GameObject chestItem;

    public Chest(GamePanel gamePanel, int worldX, int worldY) {
        this.gamePanel = gamePanel;
        this.worldX = worldX;
        this.worldY = worldY;
        this.collision = true;

        this.closedChestImage = this.gamePanel.assetManager.loadImage("../assets/chests/chest_closed.png");
        this.openedChestImage = this.gamePanel.assetManager.loadImage("../assets/chests/chest_open.png");

        this.image = this.closedChestImage;
        this.width = this.gamePanel.tileSize;
        this.height = this.gamePanel.tileSize;

        this.chestItem = this.getRandomChestItem();
    }

    public void update() {
        // keyhandler.interactionButtonPressed always false for some reason
        // System.out.println(this.gamePanel.keyHandler.interactionButtonPressed);
        // if (this.gamePanel.keyHandler.interactionButtonPressed && this.canInteract(this.gamePanel.player.worldX, this.gamePanel.player.worldY) && !this.chestOpened) {
        //     this.openChest();
        // }
    }

    @Override
    public void interact() {
        if (!this.chestOpened)
            this.openChest();
    }

    // @Override
    // public void draw(Graphics2D g2) {
        
    // }

    public void openChest() {
        this.chestOpened = true;
        this.image = this.openedChestImage;
        this.revealContents();
    }

    public GameObject getRandomChestItem() {
        if (this.randomNum(0, 100) > 25) {
            this.chestItem = new Weapon(this.gamePanel, this.gamePanel.keyHandler, this.gamePanel.mouse, this.gamePanel.player);
            ((Weapon) this.chestItem).initializeAsRandomWeapon();
            ((Weapon) this.chestItem).worldX = (int) this.worldX;
            ((Weapon) this.chestItem).worldY = (int) this.worldY;
        } else {
            this.chestItem = new Potion(this.gamePanel, (int) this.worldX, (int) this.worldY);
        }
        return this.chestItem;
    }

    public void revealContents() {
        this.gamePanel.addObjectAfterFrame(this.chestItem);
    }

    public void updateValuesOnZoom() {
        double multiplier = ((double)this.gamePanel.tileSize / this.prevTileSize);
        this.prevTileSize = this.gamePanel.tileSize;
        this.worldX *= multiplier;
        this.worldY *= multiplier;
        this.width = this.gamePanel.tileSize;
        this.height = this.gamePanel.tileSize;
    }

    private int randomNum(int min, int max) { // Inclusive
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

}
