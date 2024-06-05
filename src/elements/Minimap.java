package elements;

import java.awt.Color;
import java.awt.Graphics2D;

import components.Room;
import main.GamePanel;

public class Minimap {
    private GamePanel gamePanel;
    private int minimapTileSize;
    private int padding;
    private int[][] sectionMap;
    private Room currentRoom; // Player Section tracking

    public Minimap(GamePanel gamePanel, int minimapTileSize) {
        this.gamePanel = gamePanel;
        this.minimapTileSize = minimapTileSize;
        this.padding = this.minimapTileSize / 4;  // Define this.padding between rooms
        this.sectionMap = new int[this.gamePanel.sections][this.gamePanel.sections];
        this.initializeSectionMap();
    }

    private void initializeSectionMap() {
        for (Room room : this.gamePanel.mapCreator.rooms) {
            if (room.isBossRoom) this.sectionMap[room.sectionX][room.sectionY] = 4;
            else if (room.isLootRoom) this.sectionMap[room.sectionX][room.sectionY] = 3;
            else if (room.isStartRoom) { this.sectionMap[room.sectionX][room.sectionY] = 2; this.updateCurrentRoom(room); } // also set the players currentRoom to the starting room
            else this.sectionMap[room.sectionX][room.sectionY] = 1;
        }
    }

    public void draw(Graphics2D g2) {
        int minimapWidth = sectionMap.length * (minimapTileSize + this.padding) - this.padding;
        int minimapHeight = sectionMap[0].length * (minimapTileSize + this.padding) - this.padding;
        int offsetX = gamePanel.screenWidth - minimapWidth - 40;
        int offsetY = 20;

        // System.out.println("Drawing minimap at offsetX: " + offsetX + ", offsetY: " + offsetY);

        // semi-transpareny!!!!!!
        g2.setColor(new Color(0, 0, 0, 150));  // Semi-transparent black
        g2.fillRect(offsetX - 10, offsetY - 10, minimapWidth + 20, minimapHeight + 20);

        // Draw rooms
        for (int x = 0; x < sectionMap.length; x++) {
            for (int y = 0; y < sectionMap[0].length; y++) {
                int roomType = sectionMap[x][y];
                if (roomType == 0) continue;

                int screenX = offsetX + x * (this.minimapTileSize + this.padding);
                int screenY = offsetY + y * (this.minimapTileSize + this.padding);

                if (roomType == 8) g2.setColor(Color.LIGHT_GRAY);  // Cleared room
                else if (roomType == 1) g2.setColor(Color.GRAY);  // Normal room
                else if (roomType == 2) g2.setColor(Color.GREEN);  // Start room
                else if (roomType == 3) g2.setColor(Color.YELLOW);  // Loot room
                else if (roomType == 4) g2.setColor(Color.RED);  // Boss room
                
                g2.fillRect(screenX, screenY, this.minimapTileSize, this.minimapTileSize);

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
                if (sectionMap[x][y] != 0) {
                    int screenX = offsetX + x * (this.minimapTileSize + this.padding);
                    int screenY = offsetY + y * (this.minimapTileSize + this.padding);

                    // Check for adjacent rooms and draw hallways
                    if (x > 0 && sectionMap[x - 1][y] != 0) {
                        // Draw hallway to the left
                        g2.fillRect(screenX - this.padding, screenY + this.minimapTileSize / 2 - this.padding / 2, this.padding, this.padding);
                    }
                    if (x < sectionMap.length - 1 && sectionMap[x + 1][y] != 0) {
                        // Draw hallway to the right
                        g2.fillRect(screenX + this.minimapTileSize, screenY + this.minimapTileSize / 2 - this.padding / 2, this.padding, this.padding);
                    }
                    if (y > 0 && sectionMap[x][y - 1] != 0) {
                        // Draw hallway upwards
                        g2.fillRect(screenX + this.minimapTileSize / 2 - this.padding / 2, screenY - this.padding, this.padding, this.padding);
                    }
                    if (y < sectionMap[0].length - 1 && sectionMap[x][y + 1] != 0) {
                        // Draw hallway downwards
                        g2.fillRect(screenX + this.minimapTileSize / 2 - this.padding / 2, screenY + this.minimapTileSize, this.padding, this.padding);
                    }
                }
            }
        }
    }

    public void clearRoom(int sectionX, int sectionY) {
        this.sectionMap[sectionX][sectionY] = 8;  // Mark room as cleared
    }

    public void updateCurrentRoom(Room room) {
        this.currentRoom = room;
    }
}
