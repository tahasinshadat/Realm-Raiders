package main;

import elements.Player;

public class AssetManager {
    
    public GamePanel gamePanel;

    public AssetManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setObjects() {
        
    }

    public void setEnemies() {

    }

    public void reset() {
        // Clear All Entities
        this.gamePanel.obj.clear();
        this.gamePanel.objToRemove.clear();
        this.gamePanel.enemies.clear();
        this.gamePanel.enemiesToRemove.clear();

        // Reset Player Fully
        this.gamePanel.player = new Player(this.gamePanel, this.gamePanel.keyHandler, this.gamePanel.mouse);
    }

}
