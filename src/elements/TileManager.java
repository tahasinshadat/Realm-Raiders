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
    Tile[] tile;
    int mapTileNum[][];
    DataHandler dataHandler;

    public int presetNum;

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        this.tile = new Tile[10];
        this.mapTileNum = new int[this.gamePanel.maxScreenCol][this.gamePanel.maxScreenRow];
        this.dataHandler = new DataHandler(this.gamePanel, this.mapTileNum);
        this.mapTileNum = this.dataHandler.readMapData("../data/map01.txt");
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

                this.tile[4] = new Tile();
                this.tile[4].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/wallType2/tile2.png"));

            }

            else if (preset == 3) {
                // Floors
                this.tile[0] = new Tile();
                this.tile[0].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/floorType3/tile.png"));

                // Walls
                this.tile[1] = new Tile();
                this.tile[1].image = ImageIO.read(getClass().getResourceAsStream("../assets/tiles/wallType3/tile.png"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        // g2.drawImage(this.tile[0].image, 0, 0, this.gamePanel.tileSize, this.gamePanel.tileSize, null);

        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while (col < this.gamePanel.maxScreenCol && row < this.gamePanel.maxScreenRow) {

            int tileNum = mapTileNum[col][row];

            g2.drawImage(this.tile[tileNum].image, x, y, this.gamePanel.tileSize, this.gamePanel.tileSize, null);
            col++;
            x += this.gamePanel.tileSize;

            if (col == this.gamePanel.maxScreenCol) {
                x = 0;
                y += this.gamePanel.tileSize;
                col = 0;
                row++;
            }
        }

    }
}
