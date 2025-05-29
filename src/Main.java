import components.Window;
import main.GamePanel;

public class Main {
    public static void main(String[] args) { 
        
        GamePanel gamePanel = new GamePanel();
        Window window = new Window(gamePanel, "Realm Raiders");
        window.init();
        
    }
}
