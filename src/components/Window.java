package components;

import javax.swing.JFrame;
import main.GamePanel;

public class Window {

    private JFrame window = new JFrame();

    public Window(GamePanel gamePanel, String gameName) {
        
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Window closes on close button press
        this.window.setResizable(false); // Window is not resizable

        this.window.setTitle(gameName); // name of window
        this.window.add(gamePanel);

        this.window.pack(); // Window will be sized to fit the settings of GamePanel
        this.window.setLocationRelativeTo(null); // Window is displayed in the center of the screen
    }

    public void init() {
        this.window.setVisible(true); // Window is visible
    }
}
