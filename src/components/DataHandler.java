package components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import elements.Minimap;
import elements.TileManager;
import main.DatabaseManager;
import main.GamePanel;
import objects.Weapon;

public class DataHandler {

    public GamePanel gamePanel;
    private DatabaseManager dbManager; 
    // private String saveFilePath = "./realm_raiders_save_data/saveFile.txt"; // off of root

    public DataHandler(GamePanel gamePanel, DatabaseManager dbManager) {
        this.gamePanel = gamePanel;
        this.dbManager = dbManager;
    }
    
    //
    //// SAVING FUNCTIONS
    //
    public void storePlayerData(StringBuilder sb) { // save player data (location, health, shield, mana, current weapon)
        // try {
        //     writer.write(this.gamePanel.player.getPlayerProperties());
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        sb.append(this.gamePanel.player.getPlayerProperties());
        sb.append("\n");
    }

    public void storeWeaponData(StringBuilder sb) { // save weapon data (speed, damage, rarity)
        // try {
        //     for (Weapon weapon : this.gamePanel.player.weaponInv) {
        //         writer.write(weapon.getWeaponProperties()); 
        //         writer.newLine();
        //     }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        for (Weapon weapon : this.gamePanel.player.weaponInv) {
            sb.append(weapon.getWeaponProperties());
            sb.append("\n");
        }
    }

    public void storeWorldData(StringBuilder sb) { // save room positions and states + current objects on map + miniMap state + score, current level, current preset, gameDifficulty, etc
        // try {
        //     // Save game panel properties
        //     writer.write(this.gamePanel.getGamePanelProperties());
        //     writer.newLine();
            
        //     // Save mapTileNum 2D array
        //     int[][] worldMap = this.gamePanel.mapCreator.getWorldMap();
        //     writer.write("worldMap:");
        //     writer.newLine();
        //     for (int[] row : worldMap) {
        //         for (int tile : row) {
        //             writer.write(tile + " ");
        //         }
        //         writer.newLine();
        //     }
        //     writer.write("END OF MAP");
        //     writer.newLine();
            
        //     // Save room properties
        //     writer.write("roomProperties:");
        //     writer.newLine();
        //     for (Room room : this.gamePanel.mapCreator.rooms) {
        //         writer.write(room.getRoomKey());
        //         writer.newLine();
        //         writer.write(room.getRoomProperties());
        //         writer.newLine();
        //     }
        //     writer.write("END roomProperties");
        //     writer.newLine();
            
        //     // Save minimap sectionMap 2D array
        //     int[][] sectionMap = this.gamePanel.minimap.sectionMap;
        //     writer.write("sectionMap:");
        //     writer.newLine();
        //     for (int[] row : sectionMap) {
        //         for (int section : row) {
        //             writer.write(section + " ");
        //         }
        //         writer.newLine();
        //     }
            
        //     // Save minimap discoveredRooms set
        //     Set<String> discoveredRooms = this.gamePanel.minimap.discoveredRooms;
        //     writer.write("discoveredRooms:");
        //     writer.newLine();
        //     for (String room : discoveredRooms) {
        //         writer.write(room);
        //         writer.newLine();
        //     }

        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        
        // Save game panel properties
        sb.append(this.gamePanel.getGamePanelProperties());
        sb.append("\n");

        // Save mapTileNum 2D array
        int[][] worldMap = this.gamePanel.mapCreator.getWorldMap();
        sb.append("worldMap:");
        for (int[] row : worldMap) {
            for (int tile : row) {
                sb.append(tile + " ");
            }
            sb.append("\n");
        }
        sb.append("END OF MAP\n");

        // Save room properties
        sb.append("roomProperties:\n");
        for (Room room : this.gamePanel.mapCreator.rooms) {
            sb.append(room.getRoomKey());
            sb.append("\n");
            sb.append(room.getRoomProperties());
            sb.append("\n");
        }
        sb.append("END roomProperties\n");
        
        // Save minimap sectionMap 2D array
        int[][] sectionMap = this.gamePanel.minimap.sectionMap;
        sb.append("sectionMap:");
        sb.append("\n");
        for (int[] row : sectionMap) {
            for (int section : row) {
                sb.append(section + " ");
            }
            sb.append("\n");
        }
        sb.append("END sectionMap\n");
        
        // Save minimap discoveredRooms set
        Set<String> discoveredRooms = this.gamePanel.minimap.discoveredRooms;
        sb.append("discoveredRooms:");
        sb.append("\n");
        for (String room : discoveredRooms) {
            sb.append(room);
            sb.append("\n");
        }
        sb.append("END discoveredRooms\n");

    }


    public void saveProgress() {
        // try {
        //     // Create the data directory if it doesn't exist
        //     File directory = new File("realm_raiders_save_data");
        //     directory.mkdirs();

        //     // Create a new save file in the data directory
        //     String saveFilePath = "realm_raiders_save_data/saveFile.txt";
        //     BufferedWriter writer = new BufferedWriter(new FileWriter(saveFilePath));

        //     this.savePlayerData(writer);
        //     this.saveWeaponData(writer);
        //     this.saveWorldData(writer);
            
        //     writer.close();
        //     System.out.println("Game progress saved successfully.");

        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        if (this.dbManager == null) {
            System.err.println("DatabaseManager not initialized in DataHandler. Cannot save to DB.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        this.storePlayerData(sb);
        this.storeWeaponData(sb);
        this.storeWorldData(sb);
        String gameStateData = sb.toString();
        boolean success = dbManager.saveGame(this.gamePanel.user.userId, gameStateData);
        // System.out.println(gameStateData);

        if (!success) {
            System.err.println("Failed to save game progress");
        }
    }


    //
    //// LOADING FUNCTIONS
    //
    public void loadPlayerData(BufferedReader reader) { // load player with all data from players save data
        
    }

    public void loadWeaponData(BufferedReader reader) { // load players weapons with all data from weapons save data
        
    }

    public void loadWorldData(BufferedReader reader) { // load world, minimap, and game objects in previous game state from world save data
        String line;
        this.gamePanel.obj.clear();
        try {
            while ((line = reader.readLine()) != null) {
                // System.out.println(line);

                if (line.startsWith("tileSize:")) {
                    // credit to polygenelubricants at https://stackoverflow.com/questions/2338790/get-int-from-string-also-containing-letters-in-java
                    // for the parseInt with characters
                    this.gamePanel.tileSize = Integer.parseInt(line.replaceAll("[\\D]", ""));
                    continue;
                }

                if (line.startsWith("currentPreset")) {
                    this.gamePanel.currentPreset = Integer.parseInt(line.replaceAll("[\\D]", ""));
                    this.gamePanel.tileManager.initPreset(this.gamePanel.currentPreset);
                    this.gamePanel.mapCreator.preset(this.gamePanel.currentPreset);
                    continue;
                }

                if (line.startsWith("sectionSize:")) {
                    this.gamePanel.sectionSize = Integer.parseInt(line.replaceAll("[\\D]", ""));

                    line = reader.readLine(); // sections
                    this.gamePanel.sections = Integer.parseInt(line.replaceAll("[\\D]", ""));
                    this.gamePanel.updateWorldSize();
                    continue;
                }

                if (line.startsWith("score:")) {
                    this.gamePanel.score = Integer.parseInt(line.replaceAll("[\\D]", ""));

                    line = reader.readLine(); // current level
                    this.gamePanel.currentLevel = Integer.parseInt(line.replaceAll("[\\D]", ""));

                    line = reader.readLine(); // gameDifficulty
                    this.gamePanel.gameDifficulty = Double.parseDouble(line.split(": ")[1]);
                    continue;
                }

                if (line.startsWith("worldMap:")) {
                    this.gamePanel.mapCreator.setWorldSize(this.gamePanel.sections,this.gamePanel.sectionSize);
                    this.gamePanel.tileManager = new TileManager(gamePanel);
                    this.gamePanel.tileManager.initPreset(this.gamePanel.currentPreset);
                    
                    line = reader.readLine(); // first row of map
                    for (int i = 0; !line.equals("END OF MAP"); i++) { // while not end

                        String[] row = line.split(" ");

                        for (int col = 0; col < row.length; col++) {
                            this.gamePanel.tileManager.mapTileNum[i][col] = Integer.parseInt(row[col]);
                        }
                        line = reader.readLine();
                    }
                    continue;
                }

                if (line.startsWith("roomProperties:")) {
                    this.gamePanel.mapCreator.rooms.clear();

                    while (!line.equals("END roomProperties")) {
                        line = reader.readLine(); // start room

                        Room room = new Room(gamePanel, 0, 0, 0);
                        String roomData = "";
                        while (!(line = reader.readLine()).equals("")) {
                            // System.out.println(line);
                            roomData += line + "\n";
                        }

                        roomData = roomData.substring(0, roomData.length()-1);
                        // System.out.println(roomData);
                        room.setRoomPropertiesFromString(roomData);
                        this.gamePanel.mapCreator.rooms.add(room);
                        
                        line = reader.readLine(); // next room start
                        continue;
                    }
                    // System.out.println(this.gamePanel.mapCreator.rooms);
                    continue;
                }

                if (line.startsWith("sectionMap:")) {
                    this.gamePanel.minimap = new Minimap(this.gamePanel, 20);
                    for (int row = 0; row < this.gamePanel.sections; row++) {
                        line = reader.readLine();
                        String[] sRow = line.split(" ");
                        for (int col = 0; col < this.gamePanel.sections; col++) {
                            this.gamePanel.minimap.sectionMap[row][col] = Integer.parseInt(sRow[col]);
                        }
                    }

                    line = reader.readLine(); // discoveredRooms
                    this.gamePanel.minimap.discoveredRooms = new HashSet<>();
                    line = reader.readLine(); // first discovered room (guaranteed at least 1 due to start room)
                    while (line != null) {
                        this.gamePanel.minimap.discoveredRooms.add(line);
                        line = reader.readLine(); // get next line
                    }

                    continue;
                }

                this.gamePanel.gameState = this.gamePanel.PLAYING_STATE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProgress() {
        // try (BufferedReader reader = new BufferedReader(new FileReader(saveFilePath))) {
        //     this.loadPlayerData(reader);
        //     this.loadWeaponData(reader);
        //     this.loadWorldData(reader);
        //     System.out.println("Game progress loaded successfully.");
        //     System.out.println(this.gamePanel.obj);
        // } catch (IOException e) {
        //     System.out.println("Save file not found!");
        //     e.printStackTrace();
        // }
    }
}

