import components.MapCreator;
import components.Window;
import main.GamePanel;

public class Main {
    public static void main(String[] args) {

        GamePanel gamePanel = new GamePanel();
        Window window = new Window(gamePanel, "Realm Raiders");
        window.init();

        gamePanel.startGameThread();

        MapCreator map = new MapCreator(13, 22, true, 2);
        map.createMap();
    }
}
