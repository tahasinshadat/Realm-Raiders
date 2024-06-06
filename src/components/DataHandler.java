package components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import main.GamePanel;
import objects.Weapon;

public class DataHandler {

    public GamePanel gamePanel;
    private String saveFilePath = "../realm_raiders_save_data/saveFile.txt";

    public DataHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    // SAVING FUNCTIONS
    public void savePlayerData(BufferedWriter writer) { // save player data (location, health, shield, mana, current weapon)
        try {
            writer.write(this.gamePanel.player.getPlayerProperties());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveWeaponData(BufferedWriter writer) { // save weapon data (speed, damage, rarity)
        try {
            for (Weapon weapon : this.gamePanel.player.weaponInv) {
                writer.write(weapon.getWeaponProperties()); 
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveWorldData(BufferedWriter writer) { // save room positions and states + current objects on map + miniMap state + score, current level, current preset, gameDifficulty, etc
        try {
            // Save game panel properties
            writer.write(this.gamePanel.getGamePanelProperties());
            writer.newLine();
            
            // Save mapTileNum 2D array
            int[][] worldMap = this.gamePanel.mapCreator.getWorldMap();
            writer.write("worldMap:");
            writer.newLine();
            for (int[] row : worldMap) {
                for (int tile : row) {
                    writer.write(tile + " ");
                }
                writer.newLine();
            }
            
            // Save room properties
            writer.write("roomProperties:");
            writer.newLine();
            for (Room room : this.gamePanel.mapCreator.rooms) {
                writer.write(room.getRoomKey());
                writer.newLine();
                writer.write(room.getRoomProperties());
                writer.newLine();
            }
            
            // Save minimap sectionMap 2D array
            int[][] sectionMap = this.gamePanel.minimap.sectionMap;
            writer.write("sectionMap:");
            writer.newLine();
            for (int[] row : sectionMap) {
                for (int section : row) {
                    writer.write(section + " ");
                }
                writer.newLine();
            }
            
            // Save minimap discoveredRooms set
            Set<String> discoveredRooms = this.gamePanel.minimap.discoveredRooms;
            writer.write("discoveredRooms:");
            writer.newLine();
            for (String room : discoveredRooms) {
                writer.write(room);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveProgress() {
        try {
            // Create the data directory if it doesn't exist
            File directory = new File("realm_raiders_save_data");
            directory.mkdirs();

            // Create a new save file in the data directory
            String saveFilePath = "realm_raiders_save_data/saveFile.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFilePath));

            this.savePlayerData(writer);
            this.saveWeaponData(writer);
            this.saveWorldData(writer);
            
            writer.close();
            System.out.println("Game progress saved successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // LOADING FUNCTIONS
    public void loadPlayerData(BufferedReader reader) { // load player with all data from players save data
        
    }

    public void loadWeaponData(BufferedReader reader) { // load players weapons with all data from weapons save data
        
    }

    public void loadWorldData(BufferedReader reader) { // load world, minimap, and game objects in previous game state from world save data
        
    }

    public void loadProgress() {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFilePath))) {
            this.loadWorldData(reader);
            this.loadPlayerData(reader);
            this.loadWeaponData(reader);
            System.out.println("Game progress loaded successfully.");
        } catch (IOException e) {
            System.out.println("Save file not found!");
            e.printStackTrace();
        }
    }
}

