package components;

import java.util.ArrayList;
import objects.GameObject;

public class Room {
    public boolean isExplored;
    public boolean isCleared;
    public boolean isLootRoom;
    public boolean isBossRoom;
    public int size;
    
    // Entities
    public ArrayList<GameObject> roomObjects = new ArrayList<>();
    public ArrayList<Entity> roomEnemies = new ArrayList<>();

    public Room(int size, boolean isBossRoom, boolean isLootRoom) {
        this.size = size;
        this.isBossRoom = isBossRoom;
        this.isLootRoom = isLootRoom;
        this.isExplored = false;
        this.isCleared = false;
    }

    public void initiateRoom() { // closes the gates and spawns the enemies

    }

    public void roomCleared() { // opens the gates because the player has cleared the room / killed all the enemeis

    }

}
