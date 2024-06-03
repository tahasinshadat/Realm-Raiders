import components.Window;
import main.GamePanel;

public class Main {
    public static void main(String[] args) { 

        GamePanel gamePanel = new GamePanel();
        Window window = new Window(gamePanel, "Realm Raiders");
        window.init();
        gamePanel.setupGame();
        gamePanel.startGameThread();
        
    }
}

/*

-m "Major Progress 4"
-m "Weapon Pickup with GameObject, zooming needs work"
-m "Room Clearing Implementation & Player navigation tracking"
-m "Score Counting"
-m "Random Enemy Generation"

*/