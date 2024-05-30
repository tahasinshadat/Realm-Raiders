package main;

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

        // Reset Players Position
        this.gamePanel.player.setDefaults();
    }

}
