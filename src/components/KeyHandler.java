package components;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import main.GamePanel;

public class KeyHandler implements KeyListener {
    
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean interactionButtonPressed;
    GamePanel gamePanel;

    // Limit amount player can zoom in and out to a certain range
    private int zoomInAmt = 0;
    private int zoomOutAmt = 0;
    private final int maxZoomAmt = 12;

    public KeyHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int code = event.getKeyCode();

        if (!(this.gamePanel.gameState == GamePanel.LOAD_STATE)) {
            if (code == KeyEvent.VK_W) this.upPressed = true;
            if (code == KeyEvent.VK_S) this.downPressed = true;
            if (code == KeyEvent.VK_A) this.leftPressed = true;
            if (code == KeyEvent.VK_D) this.rightPressed = true;
            if (code == KeyEvent.VK_E) this.interactionButtonPressed = true;
            if (code == KeyEvent.VK_UP) {
                if (this.zoomInAmt <= maxZoomAmt) {
                    this.zoomInAmt++;
                    this.zoomOutAmt--;
                    this.gamePanel.zoom(1);
                }
            }
            if (code == KeyEvent.VK_DOWN) {
                if (this.zoomOutAmt <= maxZoomAmt) {
                    this.zoomOutAmt++;
                    this.zoomInAmt--;
                    this.gamePanel.zoom(-1);
                }
            }
            if (code == KeyEvent.VK_ESCAPE) this.gamePanel.paused = !this.gamePanel.paused;
            if (this.gamePanel.paused) {
                this.gamePanel.gameState = GamePanel.PAUSE_STATE;
            } else {
                this.gamePanel.gameState = GamePanel.PLAYING_STATE;
                this.gamePanel.gameUI.drawnTint = false;
            }
        }
    
    }

    @Override
    public void keyReleased(KeyEvent event) {
        int code = event.getKeyCode();

        // MOVEMENT
        if (code == KeyEvent.VK_W) this.upPressed = false;
        if (code == KeyEvent.VK_S) this.downPressed = false;
        if (code == KeyEvent.VK_A) this.leftPressed = false;
        if (code == KeyEvent.VK_D) this.rightPressed = false;
        if (code == KeyEvent.VK_E) this.interactionButtonPressed = false;

    }

}
