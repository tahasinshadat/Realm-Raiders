package components;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.GamePanel;

public class DataHandler {

    public GamePanel gamePanel;
    public int mapTileNum[][];

    InputStream is;
    BufferedReader br;

    private String file;

    public DataHandler(GamePanel gamePanel, int[][] mapTileNum) {
        this.gamePanel = gamePanel;
        this.mapTileNum = mapTileNum;
    }

    public void setFile(String file) {
        this.file = file;
        this.is = getClass().getResourceAsStream(this.file);
        this.br = new BufferedReader(new InputStreamReader(this.is));
    }

    public int[][] readFileData() {
        try {
            int col = 0;
            int row = 0;

            while (col < this.gamePanel.maxWorldCol && row < this.gamePanel.maxWorldRow) {
                String line = this.br.readLine(); // Read a single line from the txt file as a String

                while (col < this.gamePanel.maxWorldCol) {

                    String numbers[] = line.split(" "); // Split the string into an array via the spaces

                    int num = Integer.parseInt(numbers[col]); // Convert the number to an int

                    this.mapTileNum[col][row] = num;
                    col++;
                }
                if (col == this.gamePanel.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }

            this.br.close(); // Close the current BufferedReader

            return this.mapTileNum;

        } catch (Exception e) {
            e.setStackTrace(null);
        }
        return this.mapTileNum;
    }

    public int getFileCols() {
        try {
            this.is = getClass().getResourceAsStream(this.file);
            this.br = new BufferedReader(new InputStreamReader(this.is));
    
            // Read the first line to determine the number of columns
            String firstLine = this.br.readLine();
            String[] numbers = firstLine.split(" ");
            int cols = numbers.length;

            return cols;
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getFileRows() {
        try {
            this.is = getClass().getResourceAsStream(this.file);
            this.br = new BufferedReader(new InputStreamReader(this.is));
    
            // Count the number of lines to determine the number of rows
            int rows = 0;
            while (this.br.readLine() != null) {
                rows++;
            }
    
            return rows;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    

}
