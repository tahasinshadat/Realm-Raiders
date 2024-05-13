package elements;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import components.DataHandler;
import components.Tile;
import main.GamePanel;

public class TileManager {

    GamePanel gamePanel;
    public Tile[] tile;
    public int mapTileNum[][];
    DataHandler dataHandler;

    public int presetNum;

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        this.tile = new Tile[10];
        this.mapTileNum = new int[this.gamePanel.maxWorldCol][this.gamePanel.maxWorldRow];
        
        // this.dataHandler = new DataHandler(this.gamePanel, this.mapTileNum);
        // this.dataHandler.setFile("../data/map02.txt");
        // this.mapTileNum = this.dataHandler.readFileData();
        this.initPreset(1);
    }

    public void initPreset(int preset) {
        this.presetNum = preset;
        this.getTileImage(this.presetNum);
    }

    public void getTileImage(int preset) {
        try {

            if (preset == 1) {
                // Floors
                this.tile[0] = new Tile();
                this.tile[0].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/floorType1/tile1.png"));

                this.tile[1] = new Tile();
                this.tile[1].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/floorType1/tile2.png"));

                this.tile[2] = new Tile();
                this.tile[2].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/floorType1/tile3.png"));

                this.tile[3] = new Tile();
                this.tile[3].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/floorType1/tile4.png"));

                this.tile[4] = new Tile();
                this.tile[4].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/floorType1/tile5.png"));

                // Walls
                this.tile[5] = new Tile();
                this.tile[5].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/wallType1/tile.png"));
                this.tile[5].collision = true;
            }

            else if (preset == 2) {
                // Floors
                this.tile[0] = new Tile();
                this.tile[0].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/floorType2/tile1.png"));

                this.tile[1] = new Tile();
                this.tile[1].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/floorType2/tile2.png"));

                this.tile[2] = new Tile();
                this.tile[2].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/floorType2/tile3.png"));

                // Walls
                this.tile[3] = new Tile();
                this.tile[3].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/wallType2/tile1.png"));
                this.tile[3].collision = true;

                this.tile[4] = new Tile();
                this.tile[4].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/wallType2/tile2.png"));
                this.tile[4].collision = true;

            }

            else if (preset == 3) {
                // Floors
                this.tile[0] = new Tile();
                this.tile[0].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/floorType3/tile.png"));

                // Walls
                this.tile[1] = new Tile();
                this.tile[1].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/wallType3/tile.png"));
                this.tile[1].collision = true;
            }

            // Void / Empty Space Tiles
            this.tile[9] = new Tile();
            this.tile[9].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/voidTile.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        // g2.drawImage(this.tile[0].image, 0, 0, this.gamePanel.tileSize, this.gamePanel.tileSize, null);

        int worldCol = 0;
        int worldRow = 0;

        // Draw the world, relative to the player
        while (worldCol < this.gamePanel.maxWorldCol && worldRow < this.gamePanel.maxWorldRow) {

            int tileNum = mapTileNum[worldCol][worldRow];

            // Make world draw around player position
            int worldX = worldCol * this.gamePanel.tileSize;
            int worldY = worldRow * this.gamePanel.tileSize;

            // Where on the screen to draw the tiles
            int screenX = (int) (worldX - this.gamePanel.player.worldX + this.gamePanel.player.screenX);
            int screenY = (int) (worldY - this.gamePanel.player.worldY + this.gamePanel.player.screenY);

            // Create a Boundary to render only whats around the player to increase performance
            if (
                worldX + this.gamePanel.tileSize > this.gamePanel.player.worldX - this.gamePanel.player.screenX &&
                worldX - this.gamePanel.tileSize < this.gamePanel.player.worldX + this.gamePanel.player.screenX &&
                worldY + this.gamePanel.tileSize > this.gamePanel.player.worldY - this.gamePanel.player.screenY &&
                worldY - this.gamePanel.tileSize < this.gamePanel.player.worldY + this.gamePanel.player.screenY
            ) {
                g2.drawImage(this.tile[tileNum].image, screenX, screenY, this.gamePanel.tileSize, this.gamePanel.tileSize, null);
            }

            worldCol++;

            if (worldCol == this.gamePanel.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }

    }
}
