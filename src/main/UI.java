package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class UI {
    GamePanel gamePanel;
    Font gameFont;

    // Buttons
    private JButton newGameButton;
    private JButton loadGameButton;
    private JButton controlsButton;
    private JButton backButton;
    private JButton backToTitleScreenButton;

    // In Game UI
    private int healthBarHeight = 25;
    private int shieldBarHeight = this.healthBarHeight / 2;
    private int spacing = 3;
    private int cornerX = 10;
    private int cornerY = 10;

    // Loading 
    private int loadScreenTimer = 0;
    private boolean loadScreenActive = false;

    public UI(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.gameFont = new Font("Virgil", Font.PLAIN, 40);
        this.setupButtons();
    }

    private void setupButtons() {
        this.newGameButton = new JButton("New Game");
        this.loadGameButton = new JButton("Load Game");
        this.controlsButton = new JButton("Controls + How to Play");
        this.backButton = new JButton("Back");
        this.backToTitleScreenButton = new JButton("Back To Title Screen");

        // Set position of buttons
        this.newGameButton.setBounds(this.gamePanel.screenWidth / 2 - 100, this.gamePanel.screenHeight / 2 - 60, 200, 40);
        this.loadGameButton.setBounds(this.gamePanel.screenWidth / 2 - 100, this.gamePanel.screenHeight / 2 - 10, 200, 40);
        this.controlsButton.setBounds(this.gamePanel.screenWidth / 2 - 100, this.gamePanel.screenHeight / 2 + 40, 200, 40);
        this.backButton.setBounds(this.gamePanel.screenWidth / 2 - 100, this.gamePanel.screenHeight / 2 + 100, 200, 40);
        this.backToTitleScreenButton.setBounds(this.gamePanel.screenWidth / 2 - 100, gamePanel.screenHeight / 2 + 100 + 50, 200, 40);

        newGameButton.addActionListener((ActionEvent e) -> {
            this.gamePanel.gameState = GamePanel.PLAYING_STATE;
            this.gamePanel.newGame();
            this.removeButtons();
        });

        loadGameButton.addActionListener((ActionEvent e) -> {
            // add load game logic
        });

        controlsButton.addActionListener((ActionEvent e) -> {
            this.gamePanel.gameState = GamePanel.MENU_SCREEN_STATE;
            this.gamePanel.requestFocus();
            this.removeButtons();
            this.addBackButton();
        });

        backButton.addActionListener((ActionEvent e) -> {
            this.gamePanel.gameState = GamePanel.TITLE_STATE;
            this.gamePanel.requestFocus();
            this.removeButtons();
            this.addTitleButtons();
        });

        this.backToTitleScreenButton.addActionListener((ActionEvent e) -> {
            this.gamePanel.gameState = GamePanel.TITLE_STATE;
            this.removeButtons();
            this.addTitleButtons();
            this.gamePanel.requestFocus();
        });

        this.gamePanel.setLayout(null);
        this.addTitleButtons();
    }

    private void styleButtons() {
        this.newGameButton.setForeground(Color.WHITE);
        this.newGameButton.setBackground(Color.BLACK); 
        this.newGameButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        this.loadGameButton.setForeground(Color.WHITE);
        this.loadGameButton.setBackground(Color.BLACK); 
        this.loadGameButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        this.controlsButton.setForeground(Color.WHITE);
        this.controlsButton.setBackground(Color.BLACK); 
        this.controlsButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        this.backButton.setForeground(Color.WHITE);
        this.backButton.setBackground(Color.BLACK); 
        this.backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        this.backToTitleScreenButton.setForeground(Color.WHITE);
        this.backToTitleScreenButton.setBackground(Color.BLACK); 
        this.backToTitleScreenButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
    }

    private void addTitleButtons() {
        this.styleButtons();
        // Set "New Game" button to proper Title Screen Button Position 
        this.newGameButton.setBounds(this.gamePanel.screenWidth / 2 - 100, this.gamePanel.screenHeight / 2 - 60, 200, 40);
        this.gamePanel.add(this.newGameButton); // adds JButton to a Container class which is displayed by the Panel via Swing
        this.gamePanel.add(this.loadGameButton);
        this.gamePanel.add(this.controlsButton);
        // Causes button flickering
        // this.gamePanel.revalidate();
        // this.gamePanel.repaint();
    }

    private void addEndButtons() {
        this.styleButtons();
        // Set "New Game" button to proper End Screen Button Position 
        this.newGameButton.setBounds(this.gamePanel.screenWidth / 2 - 100, this.gamePanel.screenHeight / 2 + 100, 200, 40);
        this.gamePanel.add(this.newGameButton);
        this.gamePanel.add(this.backToTitleScreenButton);
        // Causes button flickering
        // this.gamePanel.revalidate();
        // this.gamePanel.repaint();
    }

    public void removeButtons() {
        // Remove buttons only if they are currently added to the panel to minimize flickering
        if (this.gamePanel.isAncestorOf(this.newGameButton)) this.gamePanel.remove(this.newGameButton); 
        if (this.gamePanel.isAncestorOf(this.loadGameButton)) this.gamePanel.remove(this.loadGameButton);
        if (this.gamePanel.isAncestorOf(this.controlsButton)) this.gamePanel.remove(this.controlsButton);
        if (this.gamePanel.isAncestorOf(this.backButton)) this.gamePanel.remove(this.backButton);
        if (this.gamePanel.isAncestorOf(this.backToTitleScreenButton)) this.gamePanel.remove(this.backToTitleScreenButton);
    }

    private void addBackButton() {
        this.styleButtons();
        this.gamePanel.add(this.backButton);
    }

    public void draw(Graphics2D g2) {
        g2.setFont(this.gameFont);
        g2.setColor(Color.WHITE);

        switch (this.gamePanel.gameState) {
            case GamePanel.TITLE_STATE -> this.drawTitleScreen(g2);
            case GamePanel.MENU_SCREEN_STATE -> this.drawControlsScreen(g2);
            case GamePanel.END_STATE -> this.drawEndScreen(g2);
            case GamePanel.PAUSE_STATE -> this.drawPausedScreen(g2);
            case GamePanel.PLAYING_STATE -> this.drawInGameUI(g2);
            default -> throw new IllegalStateException("Unexpected value: " + this.gamePanel.gameState); // Not possible
        }
    }

    private void drawTitleScreen(Graphics2D g2) {
        g2.setColor(this.gamePanel.backgroundColor);
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
        g2.setFont(gameFont);
        g2.setColor(Color.WHITE);
        FontMetrics metrics = g2.getFontMetrics(g2.getFont());
        int nameWidth = metrics.stringWidth("Realm Raiders");
        g2.drawString("Realm Raiders", this.gamePanel.screenWidth / 2 - nameWidth / 2, this.gamePanel.screenHeight / 4);
        this.addTitleButtons();
    }

    private void drawControlsScreen(Graphics2D g2) {
        g2.setColor(this.gamePanel.backgroundColor);
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(Color.WHITE);
        g2.drawString("Controls:", this.gamePanel.screenWidth / 2 - 50, this.gamePanel.screenHeight / 4);
        g2.drawString("W - Move Up", this.gamePanel.screenWidth / 2 - 50, this.gamePanel.screenHeight / 4 + 30);
        g2.drawString("A - Move Left", this.gamePanel.screenWidth / 2 - 50, this.gamePanel.screenHeight / 4 + 60);
        g2.drawString("S - Move Down", this.gamePanel.screenWidth / 2 - 50, this.gamePanel.screenHeight / 4 + 90);
        g2.drawString("D - Move Right", this.gamePanel.screenWidth / 2 - 50, this.gamePanel.screenHeight / 4 + 120);
        g2.drawString("Mouse - Aim", this.gamePanel.screenWidth / 2 - 50, this.gamePanel.screenHeight / 4 + 150);
        g2.drawString("Scroll - Change Weapon", this.gamePanel.screenWidth / 2 - 50, this.gamePanel.screenHeight / 4 + 180);
        g2.drawString("Click Back to return to the title screen", this.gamePanel.screenWidth / 2 - 150, this.gamePanel.screenHeight / 4 + 240);
        this.addBackButton();
    }

    private void drawPausedScreen(Graphics2D g2) {
        g2.setColor(this.gamePanel.backgroundColor);
        g2.fillRect(this.gamePanel.screenWidth/4, this.gamePanel.screenHeight/4, this.gamePanel.screenWidth/2, this.gamePanel.screenHeight/2);
        g2.setColor(Color.WHITE);
        g2.setFont(this.gameFont);
        String text = "Game Paused";
        int x = getXForCenteredText(g2, text);
        int y = this.gamePanel.screenHeight / 2;
        g2.drawString(text, x, y);
    }

    private void drawEndScreen(Graphics2D g2) {
        // Draw the end screen
        g2.setColor(this.gamePanel.backgroundColor);
        g2.fillRect(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);
        g2.setColor(Color.WHITE);
        g2.setFont(gameFont);
        String gameOverText = "Game Over";
        String scoreText = "Score: " + gamePanel.score;
        int xGameOver = getXForCenteredText(g2, gameOverText);
        int yGameOver = gamePanel.screenHeight / 3;
        int xScore = getXForCenteredText(g2, scoreText);
        int yScore = yGameOver + 100;
        g2.drawString(gameOverText, xGameOver, yGameOver);
        g2.drawString(scoreText, xScore, yScore);
        this.addEndButtons();
    }

    private void drawInGameUI(Graphics2D g2) {
        this.drawShieldBar(g2, this.gamePanel.player.shield);
        this.drawHealthBar(g2, this.gamePanel.player.health);
    }

    private void drawShieldBar(Graphics2D g2, int shieldAmt) {
        g2.setColor(Color.BLACK);
        g2.fillRect(this.cornerX - this.spacing, this.cornerY - this.spacing, this.gamePanel.player.maxShield + this.spacing * 2, this.shieldBarHeight + this.spacing * 2);
        g2.setColor(Color.GRAY);
        g2.fillRect(this.cornerX, this.cornerY, shieldAmt, this.shieldBarHeight);
    }

    private void drawHealthBar(Graphics2D g2, int health) {
        g2.setColor(Color.BLACK);
        g2.fillRect(this.cornerX - this.spacing, this.cornerY + this.shieldBarHeight + this.spacing * 2, this.gamePanel.player.maxHealth + this.spacing * 2, this.healthBarHeight + this.spacing * 2);
        g2.setColor(Color.RED);
        g2.fillRect(this.cornerX, this.cornerY + this.shieldBarHeight + this.spacing * 3, health, this.healthBarHeight);
    }

    // display the load screen with a buffer wheel for 3 seconds
    public void drawLoadScreen(Graphics2D g2) {
        if (this.loadScreenActive) {
            g2.setColor(this.gamePanel.backgroundColor);
            g2.fillRect(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);

            // draw buffer wheel

            if (this.loadScreenTimer >= this.gamePanel.FPS * 3) { // 3 seconds has passed
                this.loadScreenActive = false;
                this.loadScreenTimer = 0;
            }
            this.loadScreenTimer++;
        }
    }

    private int getXForCenteredText(Graphics2D g2, String text) {
        FontMetrics metrics = g2.getFontMetrics(g2.getFont());
        int x = (this.gamePanel.screenWidth - metrics.stringWidth(text)) / 2;
        return x;
    }

}