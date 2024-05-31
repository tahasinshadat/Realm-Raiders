package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JButton;

import java.awt.FontMetrics;

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
        this.gameFont = new Font("Virgil", Font.PLAIN, 40);
    }

    public void draw(Graphics2D g2) {
        g2.setFont(this.gameFont);
        g2.setColor(Color.WHITE);

        // Draw Text
        // g2.drawString("test", 5, 35);

        // Draw health bar and shield bar
        this.drawShieldBar(g2, this.gamePanel.player.shield);
        this.drawHealthBar(g2, this.gamePanel.player.health);
        // this.drawMenu();
        if (this.gamePanel.titleScreen) this.drawTitleScreen(g2);
        else if (this.gamePanel.endScreen) this.drawEndScreen(g2);
        else if (this.gamePanel.paused) this.pause(g2);
        else if (this.gamePanel.menuScreen) this.drawMenu(g2);
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

    private void drawMenu(Graphics2D g2) {
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
    }

    private void pause(Graphics2D g2) {
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
    }

    private void drawEndScreen(Graphics2D g2) {
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
    }

    // https://docs.oracle.com/javase/8/docs/api/javax/swing/JButton.html
    private void drawTitleScreen(Graphics2D g2) {
        g2.setColor(Color.black);
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
        g2.setFont(this.gameFont);
        g2.setColor(Color.WHITE);
        FontMetrics metrics = g2.getFontMetrics(g2.getFont());
        int nameWidth = metrics.stringWidth("Realm Raiders");
        g2.drawString("Realm Raiders", this.gamePanel.screenWidth/2 - nameWidth/2, this.gamePanel.screenHeight/4);

        // JButton x = new JButton("Realm Raiders");
        // this.gamePanel.add(x);
    }
}
