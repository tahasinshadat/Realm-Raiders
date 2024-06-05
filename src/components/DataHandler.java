package components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import main.GamePanel;

public class DataHandler {

    public GamePanel gamePanel;
    private String saveFilePath = "../realm_raiders_save_data/saveFile.txt";
    private int[][] mapTileNum;

    public DataHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void saveProgress() {
        try {
            // Create the data directory if it doesn't exist
            File directory = new File("realm_raiders_save_data");
            directory.mkdirs();

            // Create a new save file in the data directory
            String saveFilePath = "realm_raiders_save_data/saveFile.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFilePath));

            writer.write("CurrentPreset: " + this.gamePanel.currentPreset); writer.newLine();

            writer.write("WorldSize: " + this.gamePanel.worldSize); writer.newLine();

            writer.write("LevelEnhancer: " + this.gamePanel.levelEnhancer); writer.newLine();

            writer.write("Score: " + this.gamePanel.score); writer.newLine();

            writer.write("CurrentLevel: " + this.gamePanel.currentLevel); writer.newLine();

            writer.write("GameDifficulty: " + this.gamePanel.gameDifficulty); writer.newLine();

            writer.write("PlayerPositionX: " + this.gamePanel.player.worldX); writer.newLine();
            writer.write("PlayerPositionY: " + this.gamePanel.player.worldY); writer.newLine();

            // Write world map
            for (int row = 0; row < this.gamePanel.maxWorldRow; row++) {
                for (int col = 0; col < this.gamePanel.maxWorldCol; col++) {
                    writer.write(this.gamePanel.tileManager.mapTileNum[col][row] + " ");
                }
                writer.newLine();
            }

            writer.close();

            System.out.println("Game progress saved successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProgress() {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Score: ")) {
                    this.gamePanel.score = Integer.parseInt(line.substring(7));
                } else if (line.startsWith("Level: ")) {
                    this.gamePanel.currentLevel = Integer.parseInt(line.substring(7));
                } else if (line.startsWith("PlayerPositionX: ")) {
                    this.gamePanel.player.worldX = Integer.parseInt(line.substring(17));
                } else if (line.startsWith("PlayerPositionY: ")) {
                    this.gamePanel.player.worldY = Integer.parseInt(line.substring(17));
                } else {
                    
                }
            }
            System.out.println("Game progress loaded successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
