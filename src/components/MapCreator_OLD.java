package components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.GamePanel;

public class MapCreator_OLD {
    
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

    // Game Settings
    private int numOfChestRooms = 1;
    private int numOfPortalRooms = 1;
    private int numOfEnemyRooms = this.sections * this.sections - this.numOfChestRooms - this.numOfPortalRooms;

    // private int gapRow;
    // private int gapCol;

    public MapCreator_OLD(GamePanel gamePanel, boolean wallsEnabled, int presetNum) {
        this.gamePanel = gamePanel;
        this.wallsEnabled = wallsEnabled;
        this.preset(presetNum);
    }

    public MapCreator_OLD(int rows, int columns, boolean wallsEnabled, int presetNum) {
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

    public void printArray(int[][] arr) {  
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printWorld() {
        
    }

    public int[][] createEnemyRoom(int size) {
        this.rows = size;
        this.columns = size;

        this.newRoom = new int[this.rows][this.columns];

        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                if (this.wallsEnabled && (i == 0 || i == this.rows - 1 || j == 0 || j == this.columns - 1)) {
                    this.newRoom[i][j] = this.getWallType(); // Set border walls
                } else {
                    this.newRoom[i][j] = this.getFloorType(); // Set inner floors
                }
            }
            this.wallCount++;
        }

        return this.newRoom;
    }

    public int getWallType() { // Return the walls in order, so if floorVarieties = 2, and wallVarieties = 2, then walls should be 2, 3, 2, 3, 2, 3, etc
        this.wallCount++;
        return (this.wallCount % this.wallVarieties) + this.floorVarieties;
    }

    public int getFloorType() { // return a random floor type -> ex: 5 types -> possible floor Nums: 0, 1, 2, 3, 4
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

    private ArrayList<int[]> sectionsFilled =  new ArrayList<>();

    // Random Arrangements of 2 corridor rooms work, but not 3 or 4.
    public void setEnvironment(int amtOfRooms) {
        ArrayList<int[]> sectionQueue = new ArrayList<int[]>();
        ArrayList<String> directionQueue = new ArrayList<String>();
        int[] previousSection = new int[2];
        int[] currentSection = new int[2];
        int[] nextSection = new int[2];

        String prevDirection = this.randomDirection("null");
        // System.out.println(prevDirection);
        this.placeRoom(2, 2, randomRoom1(this.gamePanel.startingRoomSize, prevDirection)); // Place Starting Room

        updateCoords(nextSection, 2, 2);
        updateQueues(sectionQueue, nextSection, directionQueue, prevDirection);
        updateSectionTracking(this.sectionsFilled, 2, 2, sectionQueue);

        // System.out.println(this.sectionsFilled.get(0)[0] + " " + this.sectionsFilled.get(0)[1]);
        // System.out.println(sectionQueue.get(0)[0] + " " + sectionQueue.get(0)[1]);
        // System.out.println(directionQueue);

        for (int i = 0; i < amtOfRooms; i++) {

            // Go to and update nextSection, take it out from queue
            updateCoords(previousSection, nextSection[0], nextSection[1]);
            updateCoords(nextSection, sectionQueue.get(0)[0], sectionQueue.get(0)[1]);
            removeArray(sectionQueue, nextSection);

            System.out.println("Room: " + (i + 1) );
            System.out.print("Section List: [");
            for (int j = 0; j < this.sectionsFilled.size(); j++)
                System.out.print("(" + this.sectionsFilled.get(j)[0] + ", " + this.sectionsFilled.get(j)[1] + "), ");
            System.out.println("]");

            System.out.println("Direction List: " + directionQueue);
            System.out.println("Next Section: " + nextSection[0] + " " + nextSection[1]);
            System.out.println("Prev Section: " + previousSection[0] + " " + previousSection[1]);
            System.out.println("\n-------------------------------------------------------------------");

            prevDirection = directionQueue.get(0);
            directionQueue.remove(prevDirection);


            if (i == amtOfRooms - 1) {
                this.placeRoom(nextSection[0], nextSection[1], randomRoom1(this.gamePanel.portalRoomSize, oppositeDirection(prevDirection))); // Place Ending Room
                return;
            }

            // Choose random Corridor Amount
            int numOfCorridorsInNextSection = 2;//randomNum(2, 3);
            int attempts = 0;
            String direction1, direction2, direction3;

            switch (numOfCorridorsInNextSection) {
                case 2:
                    direction1 = oppositeDirection(prevDirection); // connects to previous room
                    System.out.println(direction1);
                    direction2 = randomDirection(direction1); // goes to new room
                    while (!validateDirection(nextSection, direction2)) {
                        if (hitDeadEnd(attempts)) { // End the map creation
                            System.out.println("\nDead End -> Prev Direction: " + prevDirection);
                            this.placeRoom(nextSection[0], nextSection[1], randomRoom1(this.gamePanel.portalRoomSize, oppositeDirection(prevDirection))); // Place Ending Room
                            return;
                        }
                        direction2 = randomDirection(direction1);
                        attempts++;
                    }

                    this.placeRoom(nextSection[0], nextSection[1], randomRoom2(this.gamePanel.enemyRoomSize, direction1, direction2));
                    updateSectionTracking(this.sectionsFilled, nextSection[0], nextSection[1], sectionQueue);
                    updateQueues(sectionQueue, nextSection, directionQueue, direction2);

                    break;
                case 3:
                    direction1 = oppositeDirection(prevDirection); // connects to previous room
                    direction2 = randomDirection(direction1); // goes to new room
                    direction3 = randomDirection(direction2); // goes to other new room
                    while (!validateDirection(nextSection, direction2) && !validateDirection(nextSection, direction3)) {
                        if (hitDeadEnd(attempts)) { // End the map creation
                            this.placeRoom(previousSection[0], previousSection[1], randomRoom1(this.gamePanel.portalRoomSize, oppositeDirection(prevDirection))); // Place Ending Room
                            return;
                        }
                        direction2 = randomDirection(direction1);
                        direction3 = randomDirection(direction2);
                        attempts++;
                    }

                    this.placeRoom(nextSection[0], nextSection[1], randomRoom3(this.gamePanel.enemyRoomSize, direction1, direction2, direction3));
                    updateSectionTracking(this.sectionsFilled, nextSection[0], nextSection[1], sectionQueue);
                    updateQueues(sectionQueue, nextSection, directionQueue, direction2);
                    updateQueues(sectionQueue, nextSection, directionQueue, direction3);

                    break;
                case 4:
                    break;
                default:
                    break;
            }

        }


        /*
        while (queue.size() > 0) {
            // Go to and update nextSection
            updateCoords(nextSection, queue.get(0)[0], queue.get(0)[1]);
            
            // Choose random Corridor Amount
            // int numOfCorridorsInNextSection = randomNum(2, 4);

            // if (numOfCorridorsInNextSection == 2) { 
                // Get random Direction thats Valid
                String direction2 = randomDirection(closedDirections);
                if (validateDirection(nextSection[0], nextSection[1], direction2)) {
                    // Place / Fill Room
                    this.placeRoom(nextSection[0], nextSection[1], randomRoom2(this.gamePanel.enemyRoomSize, prevDirection, direction2));
                
                    closedDirections.clear();
                    prevDirection = direction2;
                    closedDirections.add(prevDirection);

                    updateQueue(nextSection, queue, prevDirection); // update the queue
                    updateSectionTracking(sectionsFilled, queue, nextSection[0], nextSection[1]); // Log filled room
                }
                
         
            // else if (numOfCorridorsInNextSection == 3) {

            //     if ( // Edgecase (It's not possible to have 3 corridors in the corners)
            //         nextSection[0] == 0 && nextSection[1] == 0 ||
            //         nextSection[0] == 0 && nextSection[1] == this.sections ||
            //         nextSection[0] == this.sections && nextSection[1] == 0 ||
            //         nextSection[0] == this.sections && nextSection[1] == this.sections
            //     ) break;

            //     // Get random Directions that are Valid
            //     String direction2 = randomDirection(closedDirections);
            //     closedDirections.add(direction2);
            //     String direction3 = randomDirection(closedDirections);

            //     while (
            //         !validateDirection(nextSection[0], nextSection[1], direction2) &&
            //         !validateDirection(nextSection[0], nextSection[1], direction3) ) {
            //         closedDirections.clear();
            //         closedDirections.add(prevDirection);
            //         direction2 = randomDirection(closedDirections);
            //         closedDirections.add(direction2);
            //         direction3 = randomDirection(closedDirections);
            //     }
            //     // Place / Fill the room
            //     this.placeRoom(nextSection[0], nextSection[1], randomRoom3(this.gamePanel.enemyRoomSize, prevDirection, direction2, direction3));
            
            //     closedDirections.clear();
            //     prevDirection = direction2;
            //     closedDirections.add(prevDirection);
            //     updateQueue(nextSection, queue, prevDirection); // update the queue

            //     // closedDirections.clear();
            //     // prevDirection = direction2;
            //     // closedDirections.add(prevDirection);
            //     // updateQueue(nextSection, queue, prevDirection); // update the queue

            //     updateSectionTracking(sectionsFilled, queue, nextSection[0], nextSection[1]); // Log filled room

            // } 
            // else {
            //     if ( // Edgecase (It's not possible to have 4 corridors in the corners)
            //         nextSection[0] == 0 && nextSection[1] == 0 ||
            //         nextSection[0] == 0 && nextSection[1] == this.sections ||
            //         nextSection[0] == this.sections && nextSection[1] == 0 ||
            //         nextSection[0] == this.sections && nextSection[1] == this.sections
            //     ) break;
            //     this.placeRoom(nextSection[0], nextSection[1], this.allDirectionsRoom(this.gamePanel.enemyRoomSize));
                
            //     closedDirections.clear();
            //     updateQueue(nextSection, queue, randomDirection(closedDirections)); // update the queue
            //     updateSectionTracking(sectionsFilled, queue, nextSection[0], nextSection[1]); // Log filled room

            // }
            

        }
        */

    }


    public boolean hitDeadEnd(int attempts) { 
        return attempts > 10000; // There is probably no where else to turn
    }

    public void updateQueues(ArrayList<int[]> sectionQueue, int[] coords, ArrayList<String> directionQueue, String direction) {

        int[] newCoords = coords.clone();
        directionQueue.add(direction);

        switch(direction) {
            case "up":
                newCoords[1] -= 1;
                sectionQueue.add(newCoords);
                break;
            case "down":
                newCoords[1] += 1;
                sectionQueue.add(newCoords);
                break;
            case "left": 
                newCoords[0] -= 1;
                sectionQueue.add(newCoords);
                break;
            case "right":
                newCoords[0] += 1;
                sectionQueue.add(newCoords);
                break;
            default: break;
        }    
    }

    public void updateCoords(int[] coords, int newX, int newY) {
        coords[0] = newX;
        coords[1] = newY;
    }

    public void updateSectionTracking(ArrayList<int[]> sectionsFilled, int visitedX, int visitedY, ArrayList<int[]> sectionQueue) { 
        int[] section = new int[2];
        section[0] = visitedX;
        section[1] = visitedY;
        removeArray(sectionQueue, section);
        sectionsFilled.add(section);
    }

    public String oppositeDirection(String direction) {
        switch (direction) {
            case "up": return "down";
            case "down": return "up";
            case "left": return "right";
            case "right": return "left";
            default: return "";
        }
    }

    // Build Methods (Used to build the world by determining what sections to use like puzzle pieces)
    public void placeRoom(int sectionX, int sectionY, int[][] room) {
        int sectionsTopLeftX = (sectionX <= 0) ? 0 : (sectionX * this.gamePanel.sectionSize) - 1;
        int sectionsTopLeftY = (sectionY <= 0) ? 0 : (sectionY * this.gamePanel.sectionSize) - 1; 

        // System.out.println(sectionsTopLeftX +" " + sectionsTopLeftY);

        for (int i = 0; i < room.length; i++) {
            for (int j = 0; j < room.length; j++) {
                worldMap[sectionsTopLeftX + i][sectionsTopLeftY + j] = room[i][j];
            }
        }
    }

    // Gets a random direction given a list of directions that are NOT possible
    public String randomDirection(String closedOption) {
        ArrayList<String> possibleDirections = new ArrayList<String>();
        possibleDirections.add("up");
        possibleDirections.add("down");
        possibleDirections.add("left");
        possibleDirections.add("right");
        if (!closedOption.equals("null")) possibleDirections.remove(closedOption);
        return possibleDirections.get( this.randomNum(0, possibleDirections.size() - 1 ) );
    }

    public boolean validateDirection(int[] nextSection, String direction) {
        
        int sectionX = nextSection[0];
        int sectionY = nextSection[1];
        int[] newSection = new int[2];

        switch(direction) {
            case "up":
                sectionY -= 1;
                newSection[0] = sectionX; 
                newSection[1] = sectionY;
                if (sectionY >= 0 && !containsValues(this.sectionsFilled, newSection)) return true;
                break;

            case "down":
                sectionY += 1;
                newSection[0] = sectionX; 
                newSection[1] = sectionY;
                if (sectionY <= this.sections - 1 && !containsValues(this.sectionsFilled, newSection)) return true;
                break;

            case "left": 
                sectionX -= 1;
                newSection[0] = sectionX; 
                newSection[1] = sectionY;
                if (sectionX >= 0 && !containsValues(this.sectionsFilled, newSection)) return true;
                break;

            case "right":
                sectionX += 1;
                newSection[0] = sectionX; 
                newSection[1] = sectionY;
                if (sectionX <= this.sections - 1 && !containsValues(this.sectionsFilled, newSection)) return true;
                break;

            default: break;
        }
        
        return false;
    }
    
    public void rotateLeft(int[][] matrix) {
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
    

    // Higher level Creator Methods
    public int[][] randomRoom1(int size, String direction) {
        int[][] room = new int[40][40];

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

    public int[][] randomRoom2(int size, String direction1, String direction2) {
        int[][] room = new int[40][40];

        if (direction1 == "left" && direction2 == "right" || direction2 == "left" && direction1 == "right") { room = upDownRoom(size); rotateLeft(room); }

        else if (direction1 == "up" && direction2 == "down" || direction2 == "up" && direction1 == "down") { room = upDownRoom(size); }

        else if (direction1 == "up" && direction2 == "right" || direction2 == "up" && direction1 == "right") { room = leftUpRoom(size); rotateLeft(room); rotateLeft(room); rotateLeft(room);}
        
        else if (direction1 == "up" && direction2 == "left" || direction2 == "up" && direction1 == "left") { room = leftUpRoom(size); }

        else if (direction1 == "down" && direction2 == "right" || direction2 == "down" && direction1 == "right") { room = leftUpRoom(size); rotateLeft(room); rotateLeft(room); }

        else /* if (direction1 == "down" && direction2 == "left") */ { room = leftUpRoom(size); rotateLeft(room); }

        return room;
        
    }

    public int[][] randomRoom3(int size, String direction1, String direction2, String direction3) {
        int[][] room = new int[40][40];
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



    // Creator Methods (Creates Rooms with hallways in different orientations and orders, creating a full section)
    public int[][] startEndRoomUp(int size) {
        int[][] room = new int[40][40];
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                room[i][j] = 9;
            }
        }

        int start = (40 - size) / 2;
        int startCorridor = start + (size - 6) / 2;

        this.addRoom(start, start, size, room);
        this.addCorridor(startCorridor, start, "left", room, start + 1);

        return room;
    }

    public int[][] upDownRoom(int size) {
        int[][] room = new int[40][40];
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                room[i][j] = 9;
            }
        }

        int start = (40 - size) / 2;
        int startCorridor = start + (size - 6) / 2;

        this.addRoom(start, start, size, room);
        this.addCorridor(startCorridor, start, "left", room, start + 1);
        this.addCorridor(startCorridor, start + size - 1, "right", room, start + 1);

        return room;
    }

    public int[][] leftUpRoom(int size) {
        int[][] room = new int[40][40];
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                room[i][j] = 9;
            }
        }

        int start = (40 - size) / 2;
        int startCorridor = start + (size - 6) / 2;

        this.addRoom(start, start, size, room);
        this.addCorridor(startCorridor, start, "left", room, start + 1);
        this.addCorridor(start, startCorridor, "up", room, start + 1);

        return room;
    }

    public int[][] upLeftDownRoom(int size) {
        int[][] room = new int[40][40];
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                room[i][j] = 9;
            }
        }

        int start = (40 - size) / 2;
        int startCorridor = start + (size - 6) / 2;

        this.addRoom(start, start, size, room);
        this.addCorridor(startCorridor, start, "left", room, start + 1);
        this.addCorridor(startCorridor, start + size - 1, "right", room, start + 1);
        this.addCorridor(start, startCorridor, "up", room, start + 1);

        return room;
    }

    public int[][] allDirectionsRoom(int size) {
        int[][] room = new int[40][40];
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                room[i][j] = 9;
            }
        }

        int start = (40 - size) / 2;
        int startCorridor = start + (size - 6) / 2;

        this.addRoom(start, start, size, room);
        this.addCorridor(startCorridor, start, "left", room, start + 1);
        this.addCorridor(startCorridor, start + size - 1, "right", room, start + 1);
        this.addCorridor(start, startCorridor, "up", room, start + 1);
        this.addCorridor(start + size - 1, startCorridor, "down", room, start + 1);

        return room;
    }

    

    // Factory methods to create Creator Methods (Adds components into a 2d array to aid in creating a section of the worlds map)
    public void addRoom(int col, int row, int roomSize, int[][] arr) {
        
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

    public void addCorridor(int col, int row, String direction, int[][] arr, int length) {
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

    public int randomNum(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }
    

    // Helpers
    public boolean containsValues(ArrayList<int[]> list, int[] arrayToCheck) {
        for (int[] arr : list) {
            if (Arrays.equals(arr, arrayToCheck)) {
                return true;
            }
        }
        return false;
    }

    public void removeArray(ArrayList<int[]> list, int[] arrayToRemove) {
        for (int i = 0; i < list.size(); i++) {
            int[] currentArray = list.get(i);
            if (Arrays.equals(currentArray, arrayToRemove)) {
                list.remove(i);
                i--;
            }
        }
    }

    /* /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // enemy:9, loot:15, portal:17
    public void setEnvironment() {
        // Place rooms in all sections
        int roomOffset = 9; // To make the room centered in each section

        // while ()
        for (int i = 0; i < this.sections; i++) {
            for (int j = 0; j < this.sections; j++) {
                this.addRoom(i * 40 + roomOffset, j * 40 + roomOffset, this.gamePanel.enemyRoomSize);
            }
        }

        int[] coords = this.getCoords(2, 2, "up", "enemy");
        this.addCorridor(coords[0], coords[1], "up");
    }
    
    // HELPER FUNCTIONS TO CREATE WORLD

    // Adds a new enemy room anywhere in the world map
    public void addRoom(int col, int row, int roomSize) {
        
        for (int i = 0; i < roomSize; i++) {
            for (int j = 0; j < roomSize; j++) {

                if (i == 0 || i == roomSize - 1 || j == 0 || j == roomSize - 1) {
                    this.worldMap[i + col][j + row] = this.getWallType(); // Set border walls
                } else {
                    this.worldMap[i + col][j + row] = this.getFloorType(); // Set inner floors
                }

            }
            this.wallCount++;
        }

    }

    // Adds a new enemy room anywhere in the world map
    public void addCorridor(int col, int row, String direction) {
        switch (direction) {
            case "up":
                for (int i = 0; i < this.gamePanel.corridorHeight; i++) {
                    for (int j = 0; j < this.gamePanel.corridorLength; j++) {

                        if (i == 0 || i == this.gamePanel.corridorHeight - 1) {
                            this.worldMap[i + col][row - j] = this.getWallType(); // Set border walls
                        } else {
                            this.worldMap[i + col][row - j] = this.getFloorType(); // Set inner floors
                        }
                    }
                    this.wallCount++;
                }
                break;
            case "down":
                for (int i = 0; i < this.gamePanel.corridorHeight; i++) {
                    for (int j = 0; j < this.gamePanel.corridorLength; j++) {

                        if (i == 0 || i == this.gamePanel.corridorHeight - 1) {
                            this.worldMap[i + col][j + row] = this.getWallType(); // Set border walls
                        } else {
                            this.worldMap[i + col][j + row] = this.getFloorType(); // Set inner floors
                        }
                    }
                    this.wallCount++;
                }
                break;
            case "right":
                for (int i = 0; i < this.gamePanel.corridorLength; i++) {
                    for (int j = 0; j < this.gamePanel.corridorHeight; j++) {

                        if (j == 0 || j == this.gamePanel.corridorHeight - 1) {
                            this.worldMap[i + col][j + row] = this.getWallType(); // Set border walls
                        } else {
                            this.worldMap[i + col][j + row] = this.getFloorType(); // Set inner floors
                        }
                    }
                    this.wallCount++;
                }
                break;
            case "left":
                for (int i = 0; i < this.gamePanel.corridorLength; i++) {
                    for (int j = 0; j < this.gamePanel.corridorHeight; j++) {

                        if (j == 0 || j == this.gamePanel.corridorHeight - 1) {
                            this.worldMap[col - i][j + row] = this.getWallType(); // Set border walls
                        } else {
                            this.worldMap[col - i][j + row] = this.getFloorType(); // Set inner floors
                        }
                    }
                    this.wallCount++;
                }
                break;
            default: break;
        }
    }
    
    // Gets a random direction given a list of directions that are NOT possible
    public String randomDirection(String[] closedOptions) {
        List<String> possibleDirections = new ArrayList<>();
        possibleDirections.add("up");
        possibleDirections.add("down");
        possibleDirections.add("left");
        possibleDirections.add("right");

        for (int i = 0; i < closedOptions.length; i++)
            if (possibleDirections.contains(closedOptions[i])) possibleDirections.remove(closedOptions[i]);
        
        return possibleDirections.get( (int) ( Math.random() * possibleDirections.size() ) );
    }

    // Sees if its possible to branch out in a certain direction from a given position
    public boolean validateDirection(int x, int y, String direction) {
        switch (direction) {
            case "up":
                if (y - 22 <= 0) return true;
                break;
            case "down":
                if (y + 22 <= 0) return true;
                break;
            case "left":
                if (x - 22 <= 0) return true;
                break;
            case "right":
                if (x + 22 >= this.gamePanel.worldSize) return true;
                break;
            default:
                return false;
        }
        return false;
    }

    // Gets the coordinates for corridor placement
    public int[] getCoords(int sectionX, int sectionY, String direction, String roomType) {
        // int sectionsTopLeftX = (sectionX * this.gamePanel.sectionSize) - 1;
        // int sectionsTopLeftY = (sectionY * this.gamePanel.sectionSize) - 1;  
        // int roomsTopLeftX = sectionsTopLeftX + 10;
        // int roomsTopLeftY = sectionsTopLeftX + 10;

        int roomOffset = 9;
        int toMiddleOffset = 7;
        int toEndOffset = 19;

        switch(roomType) {
            case "enemy":
                roomOffset = 9;
                toMiddleOffset = 7;
                toEndOffset = this.gamePanel.enemyRoomSize - 1;
                break;
            case "loot":
                roomOffset = 9 + 6;
                toMiddleOffset = 4;
                toEndOffset = this.gamePanel.lootRoomSize - 1;
                break;
            case "portal":
                roomOffset = 9 + 8;
                toMiddleOffset = 3;
                toEndOffset = this.gamePanel.portalRoomSize - 1;
                break;
            default:
                break;
        }

        int roomsTopLeftX = (sectionX * this.gamePanel.sectionSize) + roomOffset;
        int roomsTopLeftY = (sectionY * this.gamePanel.sectionSize) + roomOffset;
        int[] coords = new int[2];

        coords[0] = roomsTopLeftX;
        coords[1] = roomsTopLeftY;
        System.out.println(roomsTopLeftX +" "+ roomsTopLeftY);

        switch(direction) {
            case "up":
                coords[0] = roomsTopLeftX + toMiddleOffset;
                coords[1] = roomsTopLeftY;
                break;
            case "down":
                coords[0] = roomsTopLeftX + toMiddleOffset;
                coords[1] = roomsTopLeftY + toEndOffset;
                break;
            case "left":
                coords[0] = roomsTopLeftX;
                coords[1] = roomsTopLeftY + toMiddleOffset;
                break;
            case "right":
                coords[0] = roomsTopLeftX + toEndOffset;
                coords[1] = roomsTopLeftY + toMiddleOffset;
                break;
            default: 
                break;
        }

        return coords;
    }


    */ /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
