package components;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import javax.swing.SwingUtilities;

import main.GamePanel;

public class MouseInteractions {

    private GamePanel gamePanel;
    public int mouseX;
    public int mouseY;
    
    public MouseInteractions(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void getMousePosition() {
        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        // Convert screen coordinates to panel coordinates
        SwingUtilities.convertPointFromScreen(mousePoint, gamePanel);
        
        this.mouseX = (int) mousePoint.getX();
        this.mouseY = (int) mousePoint.getY();

        // System.out.println("Mouse X: " + mouseX + ", Mouse Y: " + mouseY);
    }

    public int getMouseX() {
        this.getMousePosition();
        return this.mouseX;
    }

    public int getMouseY() {
        this.getMousePosition();
        return this.mouseY;
    }
}
