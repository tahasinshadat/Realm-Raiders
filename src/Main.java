import java.io.IOException;

import components.Window;
import main.GamePanel;
// import network.*;

public class Main {
    public static void main(String[] args) throws IOException { 
        
        GamePanel gamePanel = new GamePanel();
        Window window = new Window(gamePanel, "Realm Raiders");
        window.init();
        // System.out.println(NetworkManager.getPublicIP());
    }
}
// find src -name "*.class" -type f -delete