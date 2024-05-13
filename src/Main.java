import components.MapCreator;
import components.Window;
import main.GamePanel;

public class Main {
    public static void main(String[] args) { 

        GamePanel gamePanel = new GamePanel();
        Window window = new Window(gamePanel, "Realm Raiders");
        window.init();

        gamePanel.startGameThread();

        // MapCreator x = new MapCreator(1, 1, true, 1);
        // System.out.println(x.validateDirection(0,0, "down"));
        // int[][] array = {
        //     {1, 2, 3},
        //     {4, 5, 6},
        //     {7, 8, 9}
        // };

        // System.out.println("Original Matrix:");
        // x.printArray(array);

        // x.rotateRight(array);

        // System.out.println("\nMatrix after rotating 90 degrees:");
        // x.printArray(array);

        
    }
}
