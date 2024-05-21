package components;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import javax.swing.SwingUtilities;

import main.GamePanel;

public class MouseInteractions implements MouseListener{

    private GamePanel gamePanel;
    public int mouseX;
    public int mouseY;
    public boolean BUTTON1;
    
    public MouseInteractions(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.gamePanel.addMouseListener(this);
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

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
            this.BUTTON1 = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == 1) {
            this.BUTTON1 = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
