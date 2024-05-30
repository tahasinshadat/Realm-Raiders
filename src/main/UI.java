package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class UI {
    GamePanel gamePanel;
    Font gameFont;

    private int healthBarHeight = 25;
    private int shieldBarHeight = this.healthBarHeight / 2;

    int spacing = 3;
    private int cornerX = 10;
    private int cornerY = 10;

    public UI(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.gameFont = new Font("Arial", Font.ITALIC, 40);
        
    }

    public void draw(Graphics2D g2) {
        g2.setFont(this.gameFont);
        g2.setColor(Color.WHITE);

        // Draw Text
        // g2.drawString("test", 5, 35);

        // Draw health bar and shield bar
        this.drawShieldBar(g2, this.gamePanel.player.shield);
        this.drawHealthBar(g2, this.gamePanel.player.health);
        this.drawMenu();
    }

    // Draw Shield indicator
    private void drawShieldBar(Graphics2D g2, int shieldAmt) {
        g2.setColor(Color.BLACK);
        g2.fillRect(this.cornerX - this.spacing, this.cornerY - this.spacing, this.gamePanel.player.maxShield + this.spacing*2, this.shieldBarHeight + this.spacing*2);
        g2.setColor(Color.GRAY);
        g2.fillRect(this.cornerX, this.cornerY, shieldAmt, this.shieldBarHeight);
    }

    // Draw Health indicator
    private void drawHealthBar(Graphics2D g2, int health) {
        g2.setColor(Color.BLACK);
        g2.fillRect(this.cornerX - this.spacing, this.cornerY + this.shieldBarHeight + this.spacing*2, this.gamePanel.player.maxHealth + this.spacing*2, this.healthBarHeight + this.spacing*2);
        g2.setColor(Color.RED);
        g2.fillRect(this.cornerX, this.cornerY + this.shieldBarHeight + this.spacing*3, health, this.healthBarHeight);
    }

    private void drawMenu() {
        
    }
}
