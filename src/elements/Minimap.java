package elements;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import components.Room;
import main.GamePanel;

public class Minimap {
    private GamePanel gamePanel;
    private int minimapTileSize;
    private int padding;
    public int[][] sectionMap;
    private Room currentRoom; // Player Section tracking
    public Set<String> discoveredRooms; // Track discovered rooms
    private BufferedImage skullIcon;
    private BufferedImage chestIcon;

    public Minimap(GamePanel gamePanel, int minimapTileSize) {
        this.gamePanel = gamePanel;
        this.minimapTileSize = minimapTileSize;
        this.padding = this.minimapTileSize / 4; 
        this.sectionMap = new int[this.gamePanel.sections][this.gamePanel.sections];
        this.discoveredRooms = new HashSet<>(); // discovered rooms set
        this.initializeSectionMap();
        this.loadIcons();
    }

    private void initializeSectionMap() {
        for (Room room : this.gamePanel.mapCreator.rooms) {
            if (room.isBossRoom) this.sectionMap[room.sectionX][room.sectionY] = 4;
            else if (room.isLootRoom) this.sectionMap[room.sectionX][room.sectionY] = 3;
            else if (room.isStartRoom) { 
                this.sectionMap[room.sectionX][room.sectionY] = 2; 
                this.updateCurrentRoom(room); 
                this.discoveredRooms.add(room.getRoomKey()); // Discover the start room
            }
            else this.sectionMap[room.sectionX][room.sectionY] = 1;
        }
        this.discoverConnectedHallways(this.currentRoom); // Discover connected hallways for startRoom
    }

    private void loadIcons() {
        try {
            this.skullIcon = ImageIO.read(getClass().getResourceAsStream("../assets/icons/skull.png"));
            this.chestIcon = ImageIO.read(getClass().getResourceAsStream("../assets/icons/chest.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void discoverConnectedHallways(Room room) {
        int x = room.sectionX;
        int y = room.sectionY;
        
        // Discover adjacent rooms (hallways)
        if (x > 0 && sectionMap[x - 1][y] != 0) discoveredRooms.add((x - 1) + "," + y);
        if (x < sectionMap.length - 1 && sectionMap[x + 1][y] != 0) discoveredRooms.add((x + 1) + "," + y);
        if (y > 0 && sectionMap[x][y - 1] != 0) discoveredRooms.add(x + "," + (y - 1));
        if (y < sectionMap[0].length - 1 && sectionMap[x][y + 1] != 0) discoveredRooms.add(x + "," + (y + 1));
    }

    public void draw(Graphics2D g2) {
        int minimapWidth = sectionMap.length * (minimapTileSize + this.padding) - this.padding;
        int minimapHeight = sectionMap[0].length * (minimapTileSize + this.padding) - this.padding;
        int offsetX = gamePanel.screenWidth - minimapWidth - 40;
        int offsetY = 20;

        // System.out.println("Drawing minimap at offsetX: " + offsetX + ", offsetY: " + offsetY);

        // semi-transpareny!!!!!!
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(offsetX - 10, offsetY - 10, minimapWidth + 20, minimapHeight + 20);

        // Draw rooms
        for (int x = 0; x < sectionMap.length; x++) {
            for (int y = 0; y < sectionMap[0].length; y++) {
                int roomType = sectionMap[x][y];
                if (roomType == 0 || !discoveredRooms.contains(x + "," + y)) continue;

                int screenX = offsetX + x * (this.minimapTileSize + this.padding);
                int screenY = offsetY + y * (this.minimapTileSize + this.padding);

                if (roomType == 8) g2.setColor(new Color(180, 180, 180));  // Cleared room
                else if (roomType == 1) g2.setColor(new Color(105, 105, 105));  // Normal room
                else if (roomType == 2) g2.setColor(new Color(34, 160, 34, 200));  // Start room
                else if (roomType == 3) g2.setColor(new Color(218, 165, 32, 200));  // Loot room 
                else if (roomType == 4) g2.setColor(new Color(178, 34, 34, 200));  // Boss room 
                
                g2.fillRect(screenX, screenY, this.minimapTileSize, this.minimapTileSize);
                
                int skullIconSize = (int) (this.minimapTileSize / 1.25);
                int chestIconSize = (int) (this.minimapTileSize / 1.25);
                
                if (roomType == 3) g2.drawImage(this.chestIcon, screenX + (this.minimapTileSize - chestIconSize) / 2, screenY + 2, chestIconSize, chestIconSize, null);
                if (roomType == 4) g2.drawImage(this.skullIcon, screenX + (this.minimapTileSize - skullIconSize) / 2, screenY, skullIconSize, skullIconSize, null);

                // white outline for the current room
                if (this.currentRoom != null && x == this.currentRoom.sectionX && y == this.currentRoom.sectionY) {
                    g2.setColor(Color.WHITE);
                    g2.drawRect(screenX - 1, screenY - 1, minimapTileSize + 1, minimapTileSize + 1);
                }
            }
        }

        // Draw hallways
        g2.setColor(Color.DARK_GRAY);
        for (int x = 0; x < sectionMap.length; x++) {
            for (int y = 0; y < sectionMap[0].length; y++) {
                if (sectionMap[x][y] != 0 && discoveredRooms.contains(x + "," + y)) {
                    int screenX = offsetX + x * (this.minimapTileSize + this.padding);
                    int screenY = offsetY + y * (this.minimapTileSize + this.padding);

                    // Check for adjacent rooms and draw hallways
                    if (x > 0 && sectionMap[x - 1][y] != 0 && discoveredRooms.contains((x - 1) + "," + y)) {
                        // Draw hallway to the left
                        g2.fillRect(screenX - this.padding, screenY + this.minimapTileSize / 2 - this.padding / 2, this.padding, this.padding);
                    }
                    if (x < sectionMap.length - 1 && sectionMap[x + 1][y] != 0 && discoveredRooms.contains((x + 1) + "," + y)) {
                        // Draw hallway to the right
                        g2.fillRect(screenX + this.minimapTileSize, screenY + this.minimapTileSize / 2 - this.padding / 2, this.padding, this.padding);
                    }
                    if (y > 0 && sectionMap[x][y - 1] != 0 && discoveredRooms.contains(x + "," + (y - 1))) {
                        // Draw hallway upwards
                        g2.fillRect(screenX + this.minimapTileSize / 2 - this.padding / 2, screenY - this.padding, this.padding, this.padding);
                    }
                    if (y < sectionMap[0].length - 1 && sectionMap[x][y + 1] != 0 && discoveredRooms.contains(x + "," + (y + 1))) {
                        // Draw hallway downwards
                        g2.fillRect(screenX + this.minimapTileSize / 2 - this.padding / 2, screenY + this.minimapTileSize, this.padding, this.padding);
                    }
                }
            }
        }
    }

    public void clearRoom(int sectionX, int sectionY) {
        this.sectionMap[sectionX][sectionY] = 8;  // Mark room as cleared
        this.discoveredRooms.add(sectionX + "," + sectionY); // Add room to discovered rooms
        this.discoverConnectedHallways(new Room(this.gamePanel, 0, sectionX, sectionY)); // Discover connected hallways
    }

    public void updateCurrentRoom(Room room) {
        if (!room.isBossRoom) this.currentRoom = room;
    }
}
