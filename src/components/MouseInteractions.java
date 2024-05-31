package components;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;
import main.GamePanel;

public class MouseInteractions implements MouseListener, MouseWheelListener {

    private GamePanel gamePanel;
    public int mouseX;
    public int mouseY;
    private boolean leftMouseClicked;
    private boolean rightMouseClicked;
    
    public int wheelMoveAmount;
    
    public MouseInteractions(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.leftMouseClicked = false;
        this.rightMouseClicked = false;
        this.gamePanel.addMouseListener(this); // Add this as a MouseListener to the GamePanel
        this.gamePanel.addMouseWheelListener(this);
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

    public boolean isLeftMouseClicked() {
        return this.leftMouseClicked;
    }

    public boolean isRightMouseClicked() {
        return this.rightMouseClicked;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) this.leftMouseClicked = true;
        if (SwingUtilities.isRightMouseButton(e)) this.rightMouseClicked = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) this.leftMouseClicked = false;
        if (SwingUtilities.isRightMouseButton(e)) this.rightMouseClicked = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.wheelMoveAmount = e.getWheelRotation();
    }
}
