package components;

import main.GamePanel;

public class CollisionHandler {
    GamePanel gamePanel;

    public CollisionHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void checkTile(Entity entity) {
        double entityLeft = entity.worldX + entity.hitbox.x;
        double entityRight = entity.worldX + entity.hitbox.x + entity.hitbox.width;
        double entityTop = entity.worldY + entity.hitbox.y;
        double entityBottom = entity.worldY + entity.hitbox.y + entity.hitbox.height;
        
        // Get tiles around the player
        int entityLeftCol = (int) (entityLeft / this.gamePanel.tileSize);
        int entityRightCol = (int) (entityRight / this.gamePanel.tileSize);
        int entityTopRow = (int) (entityTop / this.gamePanel.tileSize);
        int entityBottomRow = (int) (entityBottom / this.gamePanel.tileSize);
        
        int tileNum1, tileNum2;

        switch(entity.direction) {

            case "up" -> {
                entityTopRow = (int) (entityTop - entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityTopRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
            }

            case "down" -> {
                entityBottomRow = (int) (entityBottom + entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
            }

            case "left" -> {
                entityLeftCol = (int) (entityLeft - entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
            }

            case "right" -> {
                entityRightCol = (int) (entityRight + entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
            }
            
            case "up-left" -> {
                entityTopRow = (int) (entityTop - entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityTopRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
                entityLeftCol = (int) (entityLeft - entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
            }

            case "up-right" -> {
                entityTopRow = (int) (entityTop - entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityTopRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
                entityRightCol = (int) (entityRight + entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
            }

            case "down-left" -> {
                entityBottomRow = (int) (entityBottom + entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
                entityLeftCol = (int) (entityLeft - entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
            }

            case "down-right" -> {
                entityBottomRow = (int) (entityBottom + entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
                entityRightCol = (int) (entityRight + entity.speed) / this.gamePanel.tileSize;
                tileNum1 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = this.gamePanel.tileManager.mapTileNum[entityRightCol][entityBottomRow];
                if ( this.gamePanel.tileManager.tile[tileNum1].collision || this.gamePanel.tileManager.tile[tileNum2].collision ) {
                    entity.collisionEnabled = true;
                }
            }

            case "idle" -> entity.collisionEnabled = false;

            default -> {
            }

        }
    }
}
