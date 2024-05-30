package components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.GamePanel;

public class MapCreator {
    
    private int rows;
    private int columns;
    private boolean wallsEnabled;

    private int floorVarieties = 1;
    private int wallVarieties = 1;
    private int wallCount = 0;
    private int[][] newRoom; // One Singluar New Room
    private int[][] worldMap; // Rooms combined
    private GamePanel gamePanel;
    private int sections;
    
    private int startRoomX;
    private int startRoomY;
    private int endRoomX;
    private int endRoomY;

    public MapCreator(GamePanel gamePanel, boolean wallsEnabled, int presetNum) {
        this.gamePanel = gamePanel;
        this.wallsEnabled = wallsEnabled;
        this.preset(presetNum);
    }

    public MapCreator(int rows, int columns, boolean wallsEnabled, int presetNum) {
        this.rows = rows;
        this.columns = columns;
        this.wallsEnabled = wallsEnabled;
        this.preset(presetNum);
    }

    public void preset(int num) {
        if (num == 1) {
            this.floorVarieties = 5;
            this.wallVarieties = 1;

        } else if (num == 2) {
            this.floorVarieties = 3;
            this.wallVarieties = 2;

        } else if (num == 3) {
            this.floorVarieties = 1;
            this.wallVarieties = 1;
        }
    }

    private int getWallType() { // Return the walls in order, so if floorVarieties = 2, and wallVarieties = 2, then walls should be 2, 3, 2, 3, 2, 3, etc
        this.wallCount++;
        return (this.wallCount % this.wallVarieties) + this.floorVarieties;
    }

    private int getFloorType() { // return a random floor type -> ex: 5 types -> possible floor Nums: 0, 1, 2, 3, 4
        return (int) (Math.random() * this.floorVarieties);
    }

    public int[][] getWorldMap() {
        return this.worldMap;
    }

    public int getWorldSize() {
        return this.worldMap.length;
    }
    
    /*
     * Key:
     * - Floor Varieties
     * - Wall Varieties
     * - Door
     * - Chest
     */

    public void setWorldSize(int sections, int sectionSize) {
        this.sections = sections;
        int worldSize = sections * sectionSize;
        this.worldMap = new int[worldSize][worldSize];
        for (int i = 0; i < worldSize; i++) {
            for (int j = 0; j < worldSize; j++) {
                this.worldMap[i][j] = 9;
            }
        }
    }


    // Random Arrangements of 2 corridor rooms work, but not 3 or 4.
    public void setEnvironment() {
        this.startRoomX = this.sections / 2;
        this.startRoomY = this.sections / 2;
        // this.placeRoom(startRoomX, startRoomY, this.createRoom(this.gamePanel.startingRoomSize)); // Place Starting Room (always in the middle of map)

        this.endRoomX = 0;
        this.endRoomY = 0;
        if (this.randomNum(0, 1) == 0) {
            this.endRoomX = (this.randomNum(0, 1) == 0) ? this.sections - 1 : 0;
            this.endRoomY = this.randomNum(0, this.sections - 1);
        } else {
            this.endRoomX = this.randomNum(0, this.sections - 1);
            this.endRoomY = (this.randomNum(0, 1) == 0) ? this.sections - 1 : 0;
        }

        // this.placeRoom(endRoomX, endRoomY, createRoom(this.gamePanel.startingRoomSize)); // Place End Room somewhere on the edge of the map

        ArrayList<int[]> path =  new ArrayList<>();
        int currentPathX = this.endRoomX;
        int currentPathY = this.endRoomY;

        while (currentPathX != this.startRoomX || currentPathY != this.startRoomY) { // create path
            int[] currentSection = {currentPathX, currentPathY};
            path.add(currentSection);
            String randomDirection = this.randomDirection(null);

            if (validateDirection(currentSection, randomDirection)) {

                switch(randomDirection) {
                    case "up":
                        currentPathY -= 1;
                        currentSection[0] = currentPathX; 
                        currentSection[1] = currentPathY;
                        break;
        
                    case "down":
                        currentPathY += 1;
                        currentSection[0] = currentPathX; 
                        currentSection[1] = currentPathY;
                        break;
        
                    case "left": 
                        currentPathX -= 1;
                        currentSection[0] = currentPathX; 
                        currentSection[1] = currentPathY;
                        break;
        
                    case "right":
                        currentPathX += 1;
                        currentSection[0] = currentPathX; 
                        currentSection[1] = currentPathY;
                        break;
        
                    default: break;
                }
            }
        }

        path.add(new int[]{this.endRoomX, this.endRoomY});
        // this.printPath(path);
        this.constructMap(path); // consturct path on matrix

    }


    // Creator Methods (Creates Rooms with hallways in different orientations and orders, creating a full section)
    private int[][] createRoom(int size) {
        int[][] room = new int[this.gamePanel.sectionSize][this.gamePanel.sectionSize];
        for (int i = 0; i < this.gamePanel.sectionSize; i++) {
            for (int j = 0; j < this.gamePanel.sectionSize; j++) {
                room[i][j] = 9;
            }
        }

        int start = (this.gamePanel.sectionSize - size) / 2;
        // int startCorridor = start + (size - 6) / 2;
        this.addRoom(start, start, size, room);

        return room;
    }

    private void constructMap(ArrayList<int[]> path) {
        
        for (int x = 0; x < this.sections; x++) {
            for (int y = 0; y < this.sections; y++) {
                ArrayList<String> corridorDirections = new ArrayList<String>();

                if (this.containsValues(path, new int[]{x, y})) { // if section is in path

                    if (y - 1 >= 0 && this.containsValues(path, new int[]{x, y - 1})) corridorDirections.add("up"); // check up

                    if (y + 1 <= this.sections - 1 && this.containsValues(path, new int[]{x, y + 1})) corridorDirections.add("down"); // check down

                    if (x - 1 >= 0 && this.containsValues(path, new int[]{x - 1, y})) corridorDirections.add("left"); // check left

                    if (x + 1 <= this.sections && this.containsValues(path, new int[]{x + 1, y})) corridorDirections.add("right"); // check right

                }

                int typeOfRoom = (this.randomNum(0, 100) <= 15) ? this.gamePanel.lootRoomSize : this.gamePanel.enemyRoomSize;
                if (this.startRoomX == x && this.startRoomY == y) typeOfRoom = this.gamePanel.startingRoomSize;  
                if (this.endRoomX == x && this.endRoomY == y) typeOfRoom = this.gamePanel.endRoomSize;      

                switch (corridorDirections.size()) {
                    case 1 -> this.placeRoom(x, y, this.room1(typeOfRoom, corridorDirections.get(0)));
                    case 2 -> this.placeRoom(x, y, this.room2(typeOfRoom, corridorDirections.get(0), corridorDirections.get(1)));
                    case 3 -> this.placeRoom(x, y, this.room3(typeOfRoom, corridorDirections.get(0), corridorDirections.get(1), corridorDirections.get(2)));
                    case 4 -> this.placeRoom(x, y, this.room4(typeOfRoom));
                    default -> {
                    }
                }
                
            }
        }
    }
    
    

    // Higher level Creator Methods
    private int[][] room1(int size, String direction) {
        int[][] room = new int[this.gamePanel.sectionSize][this.gamePanel.sectionSize];

        room = startEndRoomUp(size);
        switch(direction) {
            case "up": break;
            case "down":
                rotateLeft(room); rotateLeft(room);
                break;
            case "left": 
                rotateLeft(room);
                break;
            case "right":
                rotateLeft(room); rotateLeft(room); rotateLeft(room);
                break;
            default: break;
        }
        return room;
    }

    private int[][] room2(int size, String direction1, String direction2) {
        int[][] room = new int[this.gamePanel.sectionSize][this.gamePanel.sectionSize];

        if (direction1 == "left" && direction2 == "right" || direction2 == "left" && direction1 == "right") { room = upDownRoom(size); rotateLeft(room); }

        else if (direction1 == "up" && direction2 == "down" || direction2 == "up" && direction1 == "down") { room = upDownRoom(size); }

        else if (direction1 == "up" && direction2 == "right" || direction2 == "up" && direction1 == "right") { room = leftUpRoom(size); rotateLeft(room); rotateLeft(room); rotateLeft(room);}
        
        else if (direction1 == "up" && direction2 == "left" || direction2 == "up" && direction1 == "left") { room = leftUpRoom(size); }

        else if (direction1 == "down" && direction2 == "right" || direction2 == "down" && direction1 == "right") { room = leftUpRoom(size); rotateLeft(room); rotateLeft(room); }

        else /* if (direction1 == "down" && direction2 == "left") */ { room = leftUpRoom(size); rotateLeft(room); }

        return room;
        
    }

    private int[][] room3(int size, String direction1, String direction2, String direction3) {
        int[][] room = new int[this.gamePanel.sectionSize][this.gamePanel.sectionSize];
        // Add all directions
        ArrayList<String> possibleDirections = new ArrayList<String>();
        possibleDirections.add("up");
        possibleDirections.add("down");
        possibleDirections.add("left");
        possibleDirections.add("right");

        // Take out all needed ones
        possibleDirections.remove(direction1);
        possibleDirections.remove(direction2);
        possibleDirections.remove(direction3);

        // return something that doesnt have the unchoosen direction
        switch(possibleDirections.get(0)) {
            case "up": 
                room = upLeftDownRoom(size); rotateLeft(room);
                break;
            case "down":
                room = upLeftDownRoom(size); rotateLeft(room); rotateLeft(room); rotateLeft(room);
                break;
            case "left":
                room = upLeftDownRoom(size); rotateLeft(room); rotateLeft(room);
                break;
            case "right":
                room = upLeftDownRoom(size); 
                break;
            default: break;
        }
        return room;
    }

    private int[][] room4(int size) {
        int[][] room = new int[this.gamePanel.sectionSize][this.gamePanel.sectionSize];
        for (int i = 0; i < this.gamePanel.sectionSize; i++) {
            for (int j = 0; j < this.gamePanel.sectionSize; j++) {
                room[i][j] = 9;
            }
        }

        int start = (this.gamePanel.sectionSize - size) / 2;
        int startCorridor = start + (size - 6) / 2;

        this.addRoom(start, start, size, room);
        this.addCorridor(startCorridor, start, "left", room, start + 1);
        this.addCorridor(startCorridor, start + size - 1, "right", room, start + 1);
        this.addCorridor(start, startCorridor, "up", room, start + 1);
        this.addCorridor(start + size - 1, startCorridor, "down", room, start + 1);

        return room;
    }

    // Creator Methods (Creates Rooms with hallways in different orientations and orders, creating a full section)
    private int[][] startEndRoomUp(int size) {
        int[][] room = new int[this.gamePanel.sectionSize][this.gamePanel.sectionSize];
        for (int i = 0; i < this.gamePanel.sectionSize; i++) {
            for (int j = 0; j < this.gamePanel.sectionSize; j++) {
                room[i][j] = 9;
            }
        }

        int start = (this.gamePanel.sectionSize - size) / 2;
        int startCorridor = start + (size - 6) / 2;

        this.addRoom(start, start, size, room);
        this.addCorridor(startCorridor, start, "left", room, start + 1);

        return room;
    }

    private int[][] upDownRoom(int size) {
        int[][] room = new int[this.gamePanel.sectionSize][this.gamePanel.sectionSize];
        for (int i = 0; i < this.gamePanel.sectionSize; i++) {
            for (int j = 0; j < this.gamePanel.sectionSize; j++) {
                room[i][j] = 9;
            }
        }

        int start = (this.gamePanel.sectionSize - size) / 2;
        int startCorridor = start + (size - 6) / 2;

        this.addRoom(start, start, size, room);
        this.addCorridor(startCorridor, start, "left", room, start + 1);
        this.addCorridor(startCorridor, start + size - 1, "right", room, start + 1);

        return room;
    }

    private int[][] leftUpRoom(int size) {
        int[][] room = new int[this.gamePanel.sectionSize][this.gamePanel.sectionSize];
        for (int i = 0; i < this.gamePanel.sectionSize; i++) {
            for (int j = 0; j < this.gamePanel.sectionSize; j++) {
                room[i][j] = 9;
            }
        }

        int start = (this.gamePanel.sectionSize - size) / 2;
        int startCorridor = start + (size - 6) / 2;

        this.addRoom(start, start, size, room);
        this.addCorridor(startCorridor, start, "left", room, start + 1);
        this.addCorridor(start, startCorridor, "up", room, start + 1);

        return room;
    }

    private int[][] upLeftDownRoom(int size) {
        int[][] room = new int[this.gamePanel.sectionSize][this.gamePanel.sectionSize];
        for (int i = 0; i < this.gamePanel.sectionSize; i++) {
            for (int j = 0; j < this.gamePanel.sectionSize; j++) {
                room[i][j] = 9;
            }
        }

        int start = (this.gamePanel.sectionSize - size) / 2;
        int startCorridor = start + (size - 6) / 2;

        this.addRoom(start, start, size, room);
        this.addCorridor(startCorridor, start, "left", room, start + 1);
        this.addCorridor(startCorridor, start + size - 1, "right", room, start + 1);
        this.addCorridor(start, startCorridor, "up", room, start + 1);

        return room;
    }



    // Build Methods (Used to build the world by determining what sections to use like puzzle pieces)
    private void placeRoom(int sectionX, int sectionY, int[][] room) {
        int sectionsTopLeftX = (sectionX <= 0) ? 0 : (sectionX * this.gamePanel.sectionSize) - 1;
        int sectionsTopLeftY = (sectionY <= 0) ? 0 : (sectionY * this.gamePanel.sectionSize) - 1; 

        // System.out.println(sectionsTopLeftX +" " + sectionsTopLeftY);

        for (int i = 0; i < room.length; i++) {
            for (int j = 0; j < room.length; j++) {
                worldMap[sectionsTopLeftX + i][sectionsTopLeftY + j] = room[i][j];
            }
        }
    }



    // Factory methods to create Creator Methods (Adds components into a 2d array to aid in creating a section of the worlds map)
    private void addRoom(int col, int row, int roomSize, int[][] arr) {
        
        for (int i = 0; i < roomSize; i++) {
            for (int j = 0; j < roomSize; j++) {

                if (i == 0 || i == roomSize - 1 || j == 0 || j == roomSize - 1) {
                    arr[i + col][j + row] = this.getWallType(); // Set border walls
                } else {
                    arr[i + col][j + row] = this.getFloorType(); // Set inner floors
                }

            }
            this.wallCount++;
        }

    }

    private void addCorridor(int col, int row, String direction, int[][] arr, int length) {
        switch (direction) {
            case "left":
                for (int j = 0; j < length; j++) {
                    for (int i = 0; i < 6; i++) {

                        if (i == 0 || i == 6 - 1) {
                            arr[i + col][row - j] = this.getWallType(); // Set border walls
                        } else {
                            arr[i + col][row - j] = this.getFloorType(); // Set inner floors
                        }
                    }
                    this.wallCount++;
                }
                break;
            case "right":
                for (int i = 0; i < 6; i++) {
                    for (int j = 0; j < length; j++) {

                        if (i == 0 || i == 6 - 1) {
                            arr[i + col][j + row] = this.getWallType(); // Set border walls
                        } else {
                            arr[i + col][j + row] = this.getFloorType(); // Set inner floors
                        }
                    }
                    this.wallCount++;
                }
                break;
            case "down":
                for (int i = 0; i < length; i++) {
                    for (int j = 0; j < 6; j++) {

                        if (j == 0 || j == 6 - 1) {
                            arr[i + col][j + row] = this.getWallType(); // Set border walls
                        } else {
                            arr[i + col][j + row] = this.getFloorType(); // Set inner floors
                        }
                    }
                    this.wallCount++;
                }
                break;
            case "up":
                for (int i = 0; i < length; i++) {
                    for (int j = 0; j < 6; j++) {

                        if (j == 0 || j == 6 - 1) {
                            arr[col - i][j + row] = this.getWallType(); // Set border walls
                        } else {
                            arr[col - i][j + row] = this.getFloorType(); // Set inner floors
                        }
                    }
                    this.wallCount++;
                }
                break;
            default: break;
        }
    }

    private int randomNum(int min, int max) { // Inclusive
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }
    


    // Helpers
    private void rotateLeft(int[][] matrix) {
        int n = matrix.length;
    
        // Transpose the matrix
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
    
        // Reverse each column to get the final rotated matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n / 2; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[i][n - 1 - j];
                matrix[i][n - 1 - j] = temp;
            }
        }
    }
    
    private String randomDirection(String closedOption) { // Gets a random direction given a list of directions that are NOT possible
        ArrayList<String> possibleDirections = new ArrayList<String>();
        possibleDirections.add("up");
        possibleDirections.add("down");
        possibleDirections.add("left");
        possibleDirections.add("right");
        if (closedOption != null) possibleDirections.remove(closedOption);
        return possibleDirections.get( this.randomNum(0, possibleDirections.size() - 1 ) );
    }

    private boolean validateDirection(int[] section, String direction) {
        
        int sectionX = section[0];
        int sectionY = section[1];

        switch(direction) {
            case "up":
                sectionY -= 1;
                if (sectionY >= 0) return true;
                break;

            case "down":
                sectionY += 1;
                if (sectionY <= this.sections - 1) return true;
                break;

            case "left": 
                sectionX -= 1;
                if (sectionX >= 0) return true;
                break;

            case "right":
                sectionX += 1;
                if (sectionX <= this.sections - 1) return true;
                break;

            default: break;
        }
        
        return false;
    }
    
    private boolean containsValues(ArrayList<int[]> list, int[] arrayToCheck) {
        for (int[] arr : list) {
            if (Arrays.equals(arr, arrayToCheck)) {
                return true;
            }
        }
        return false;
    }

    private void removeArray(ArrayList<int[]> list, int[] arrayToRemove) {
        for (int i = 0; i < list.size(); i++) {
            int[] currentArray = list.get(i);
            if (Arrays.equals(currentArray, arrayToRemove)) {
                list.remove(i);
                i--;
            }
        }
    }

    private void printPath(ArrayList<int[]> path) {
        StringBuilder sb = new StringBuilder("[ ");
        for (int[] point : path) {
            sb.append("( ").append(point[0]).append(", ").append(point[1]).append("), ");
        }
        if (!path.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove the trailing comma and space
        }
        sb.append(" ]");
        System.out.println(sb.toString());
    }

    private void printArray(int[][] arr) {  
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

}
