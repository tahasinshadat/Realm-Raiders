package components;

public class MapCreator {
    
    private int rows;
    private int columns;
    private boolean wallsEnabled;

    private int floorVarieties = 1;
    private int wallVarieties = 1;
    private int wallCount = 0;

    // private int gapRow;
    // private int gapCol;

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

    public void createMap() {
        int[][] map = new int[this.rows][this.columns];

        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                if (this.wallsEnabled && (i == 0 || i == this.rows - 1 || j == 0 || j == this.columns - 1)) {
                    map[i][j] = this.getWallType(); // Set border walls
                } else {
                    map[i][j] = this.getFloorType(); // Set inner floors
                }
            }
            this.wallCount++;
        }

        // Print the map
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int getWallType() { // Return the walls in order, so if floorVarieties = 2, and wallVarieties = 2, then walls should be 2, 3, 2, 3, 2, 3, etc
        this.wallCount++;
        return (this.wallCount % this.wallVarieties) + this.floorVarieties;
    }

    public int getFloorType() { // return a random floor type -> ex: 5 types -> possible floor Nums: 0, 1, 2, 3, 4
        return (int) (Math.random() * this.floorVarieties);
    }


}
