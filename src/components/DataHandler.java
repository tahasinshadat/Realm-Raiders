package components;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.GamePanel;

public class DataHandler {

    public GamePanel gamePanel;
    public int mapTileNum[][];

    public DataHandler(GamePanel gamePanel, int[][] mapTileNum) {
        this.gamePanel = gamePanel;
        this.mapTileNum = mapTileNum;
    }

    public int[][] readMapData(String file) {
        try {
            InputStream is = getClass().getResourceAsStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (col < this.gamePanel.maxScreenCol && row < this.gamePanel.maxScreenRow) {
                String line = br.readLine(); // Read a single line from the txt file as a String

                while (col < this.gamePanel.maxScreenCol) {

                    String numbers[] = line.split(" "); // Split the string into an array via the spaces

                    int num = Integer.parseInt(numbers[col]); // Convert the number to an int

                    this.mapTileNum[col][row] = num;
                    col++;
                }
                if (col == this.gamePanel.maxScreenCol) {
                    col = 0;
                    row++;
                }
            }

            return this.mapTileNum;

        } catch (Exception e) {
            e.setStackTrace(null);
        }
        return this.mapTileNum;
    }

}
