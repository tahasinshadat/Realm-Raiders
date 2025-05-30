package components;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import elements.Minimap;
import elements.Player;
import elements.TileManager;
import main.DatabaseManager;
import main.GamePanel;
import objects.Weapon;

public class DataHandler {

    public GamePanel gamePanel;
    private DatabaseManager dbManager; 
    private String gameData = null;
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
        
        sb.append("START PLAYERS\n");

        sb.append(this.gamePanel.player.getPlayerProperties());
        sb.append("\n");

        sb.append("\nEND PLAYERS\n\n");
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
        
        sb.append("START WEAPONS\n");

        for (Weapon weapon : this.gamePanel.player.weaponInv) {
            sb.append(weapon.getWeaponProperties());
            sb.append("\n");
        }
        
        sb.append("END WEAPONS\n\n");
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
        sb.append("worldMap:\n");
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

    public void saveProgress(int userId, int slot) {
        if (this.dbManager == null) {
            System.err.println("DatabaseManager not initialized in DataHandler. Cannot save to DB.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        this.storePlayerData(sb);
        this.storeWeaponData(sb);
        this.storeWorldData(sb);
        String gameStateData = sb.toString();

        boolean success = this.dbManager.saveGameToSlot(userId, slot, gameStateData);
        // System.out.println(gameStateData);

        if (!success) {
            System.err.println("Failed to save game progress");
        }
    }


    //
    //// LOADING FUNCTIONS
    //

    private void fetchGameData(int saveId) {
        this.gameData = this.dbManager.loadGameData(saveId);
    }

    public void loadPlayerData(BufferedReader reader) throws IOException { // load player with all data from players save data
        StringBuilder playerPropertiesBlock = new StringBuilder();
        String line;

        // Player data is the first block and ends with a blank line
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            playerPropertiesBlock.append(line).append("\n");
        }

        if (playerPropertiesBlock.length() > 0) {
            if (this.gamePanel.player == null) {
                this.gamePanel.player = new Player(this.gamePanel, this.gamePanel.keyHandler, this.gamePanel.mouse);
            }
            this.gamePanel.player.setPlayerPropertiesFromString(playerPropertiesBlock.toString().trim());
            System.out.println("Player data loaded successfully.");
        } else {
            System.err.println("Player data block is empty or not found.");
            if (this.gamePanel.player == null) { // Initialize a default player if data is missing/corrupt
                this.gamePanel.player = new Player(this.gamePanel, this.gamePanel.keyHandler, this.gamePanel.mouse);
                this.gamePanel.player.setDefaults();
            }
        }
    }

    public void loadWeaponData(BufferedReader reader) throws IOException { // load players weapons with all data from weapons save data
        StringBuilder weaponPropertiesBlock = new StringBuilder();
        String line;
        
        reader.mark(100000);
        reader.reset();

        if (this.gamePanel.player == null) {
            this.gamePanel.player = new Player(this.gamePanel, this.gamePanel.keyHandler, this.gamePanel.mouse);
        }
        this.gamePanel.player.weaponInv.clear();

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("START WEAPONS")) break;
        }
        
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("END WEAPONS")) break;
            if (line.trim().isEmpty()) {
                Weapon newWeapon = new Weapon(this.gamePanel, this.gamePanel.keyHandler, this.gamePanel.mouse, this.gamePanel.player);
                newWeapon.setWeaponPropertiesFromString(weaponPropertiesBlock.toString().trim());
                newWeapon.onGround = false;
                this.gamePanel.player.weaponInv.add(newWeapon);
                this.gamePanel.player.equippedWeapon = this.gamePanel.player.weaponInv.get(0);
                
                weaponPropertiesBlock.setLength(0);
            } else {
                weaponPropertiesBlock.append(line).append("\n");
            }
        }
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
                            this.gamePanel.mapCreator.worldMap[i][col] = Integer.parseInt(row[col]);
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProgress(int saveId) {
        this.fetchGameData(saveId);
        if (this.gameData == null) {
            System.err.println("No game data returned for saveId: " + saveId);
            return;
        }
        BufferedReader reader = new BufferedReader(new StringReader(this.gameData));
        try {
            this.loadPlayerData(reader);
            this.loadWeaponData(reader);
            this.loadWorldData(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

