package ui;

import main.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import javax.swing.table.DefaultTableModel;

public class UI {
    GamePanel gamePanel;
    Font gameFont;

    // Login & Signup inputs and buttons
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JTextField signupUsernameField;
    private JTextField signupEmailField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmPasswordField;
    
    // In Game UI - HUD
    private static final int BASE_SHIELD = 300;
    private static final int BASE_HEALTH = 300;
    private static final int BASE_MANA = 300;
    private static final int HUD_X = 20;
    private static final int HUD_Y = 25;
    private static final int HUD_GAP_Y = 10;
    private static final int SH_HEIGHT = 10;
    private static final int HP_HEIGHT = 20;
    private static final int MP_HEIGHT = 14;
    private static final int FULL_W = 260;

    // Styling
    public BufferedImage gameTitle;
    public BufferedImage gameTitleBG;

    // Loading 
    private int loadScreenTimer = 0;
    private String[] gameFacts = {
        "This Game Was Inspired by Soul Knight!",
        "Check Out Realm Raiders On Github! It Was Made For Our APCSA Project.",
        "The Weapon Type Determines Fire Rate, The Weapon Rarity Determines Damage.",
        "Always Heal Up Before Moving Onto A Boss Room!",
        "Zoom Out Before Entering a Boss Room.",
        "There are 25 different weapons in this game!",
        "You can continue exploring the map after killing the boss",
        "Use different weapons to find the one that suits your playstyle best!",
        "Remember to pick up loot after clearing a room!",
        "Enemies have weaknesses – try different weapons to exploit them!",
        "Check the minimap frequently to avoid missing rooms!",
        "Bosses have attack patterns – learn them to avoid damage!",
        "Save your powerful weapons for tough enemies or bosses.",
        "Different characters have unique abilities – experiment with them!",
        "Use cover to avoid enemy fire and reduce damage taken.",
        "Explore every room – you never know what you might find!",
        "Healing items are scarce – use them wisely.",
        "Keep an eye on your mana – running out can be deadly!",
        "Defeated enemies can drop helpful items – collect them!",
        "Try different strategies for different enemy types.",
        "Pay attention to your surroundings – it could save your life!",
        "Focus tower enemies first! They get dangerous with time."
    };

    // UI STATES
    private final List<java.awt.Component> activeUI = new java.util.ArrayList<>();
    private GamePanel.GameState lastUIState = null;

    // Styling constants
    private static final Color BTN_BG = Color.BLACK;
    private static final Color BTN_FG = Color.WHITE;
    private static final Border BTN_BORDER = BorderFactory.createLineBorder(Color.WHITE);
    private static final Color BTN_BG_HOVER = new Color(40, 40, 40);


    public UI(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.gameTitle = this.gamePanel.assetManager.loadImage("../assets/icons/title.png");
        this.gameTitleBG = this.gamePanel.assetManager.loadImage("../assets/icons/titleBG.png");
        this.gameFont = new Font("Virgil", Font.PLAIN, 40);
        this.setupButtons();
        this.rebuild();
    }

    //
    //// Initialization Functions
    //
    private void setupButtons() {
        this.gamePanel.setLayout(null);
    }

    //
    //// Core Functions
    //
    public void draw(Graphics2D g2) {

        if (gamePanel.gameState != lastUIState) {
            this.rebuild();               // clears + adds correct buttons
        }

        g2.setFont(gameFont);
        g2.setColor(Color.WHITE);

        switch (gamePanel.gameState) {
            case TITLE -> {
                if (this.gamePanel.user != null) {
                    this.gamePanel.setGameState(GamePanel.GameState.MAIN_MENU);
                }
                drawTitleScreen(g2);
                // addTitleButtons();
            }
            case LOGIN -> {
                drawScreenBackground(g2, "Log In");
                // addLoginScreenButtons();
            }
            case SIGNUP -> {
                drawScreenBackground(g2, "Sign Up");
                // addSignupScreenButtons();
            }
            case MAIN_MENU -> {
                drawScreenBackground(g2, "Main Menu");
                // addMainMenuButtons();
            }
            case SAVE_SLOT_SELECTION -> {
                drawScreenBackground(g2, "Load Game");
                // addSaveSlotButtons();
            }
            case MULTIPLAYER_MENU -> {
                drawScreenBackground(g2, "Multiplayer");
                // addMultiplayerMenuButtons();
            }
            case HOST_LOBBY -> {
                drawScreenBackground(g2, "Host Lobby");
                // addHostLobbyScreen();
            }
            case JOIN_LOBBY -> {
                drawScreenBackground(g2, "Join Lobby");
                // addJoinLobbyScreen();
            }
            case PAUSE -> {
                drawPausedScreen(g2);
                // addPauseButtons(); 
            }
            case PLAYING -> {
                drawInGameUI(g2);
            }
            case LOAD -> {
                drawLoadScreen(g2);
            }
            case END -> {
                drawEndScreen(g2);
                // addEndButtons(); 
            }
            default -> {
                throw new IllegalStateException("Unexpected state: " + gamePanel.gameState);
            }
        }
        
        if (gamePanel.gameState != GamePanel.GameState.PLAYING &&
            gamePanel.gameState != GamePanel.GameState.PAUSE) {

            g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
            g2.setColor(new Color(50, 50, 50, 180));
            g2.fillRect(gamePanel.screenWidth - 320, gamePanel.screenHeight - 60, 300, 40);

            g2.setColor(consoleType == ConsoleType.ERROR ? Color.RED : Color.YELLOW); // blue for info
            g2.drawString(consoleText, gamePanel.screenWidth - 310, gamePanel.screenHeight - 35);
        }

    }

    public void rebuild() {
        this.clearUI();

        switch (this.gamePanel.gameState) {
            case TITLE -> addTitleButtons();
            case LOGIN -> addLoginScreenButtons();
            case SIGNUP -> addSignupScreenButtons();
            case MAIN_MENU -> addMainMenuButtons();
            case SAVE_SLOT_SELECTION -> addSaveSlotButtons();
            case MULTIPLAYER_MENU -> addMultiplayerMenuButtons();
            case HOST_LOBBY -> addHostLobbyScreen();
            case JOIN_LOBBY -> addJoinLobbyScreen();
            case PAUSE -> addPauseButtons();
            case END -> addEndButtons();
            case PLAYING, LOAD -> {}
            default -> throw new IllegalArgumentException("Unexpected value: " + this.gamePanel.gameState);
        }

        lastUIState = gamePanel.gameState;
        gamePanel.revalidate();
        gamePanel.repaint();
    }


    //
    //// Draw Functions
    //
    private void drawTitleScreen(Graphics2D g2) {
        g2.drawImage(this.gameTitleBG, 0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight, null);
        g2.drawImage(this.gameTitle, this.gamePanel.screenWidth / 2 - 300 + 15, this.gamePanel.screenHeight / 8 - 150, 600, 300, null);
    }
    
    private void drawPausedScreen(Graphics2D g2) {
        
        gamePanel.drawGameFrame(g2);
        drawInGameUI(g2);
        
        g2.setColor(new Color(0,0,0,128));
        g2.fillRect(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);
        
        g2.setColor(Color.WHITE);
        g2.setFont(gameFont);
        
        String text = "Game Paused";
        g2.drawString(text, getXForCenteredText(g2, text), gamePanel.screenHeight / 2 - 40);
        g2.setFont(new Font("Virgil", Font.PLAIN, 20));
        String tip = "Press ESC to Resume";
        g2.drawString(tip, getXForCenteredText(g2, tip), gamePanel.screenHeight / 2);
        g2.drawImage(this.gameTitle, this.gamePanel.screenWidth / 2 - 150 + 6, this.gamePanel.screenHeight / 5 - 6, 300, 150, null);

    }

    private void drawEndScreen(Graphics2D g2) {
        g2.drawImage(gameTitleBG, 0, 0, gamePanel.screenWidth, gamePanel.screenHeight, null);

        g2.setColor(Color.WHITE);
        g2.setFont(gameFont);

        String over = "Game Over";
        g2.drawString(over, getXForCenteredText(g2, over), gamePanel.screenHeight / 3);

        String scoreTxt = "Score: " + gamePanel.score;
        g2.drawString(scoreTxt, getXForCenteredText(g2, scoreTxt), gamePanel.screenHeight / 3 + 60);

        String levelTxt = "Level: " + gamePanel.currentLevel;
        g2.drawString(levelTxt, getXForCenteredText(g2, levelTxt), gamePanel.screenHeight / 3 + 120);

    }

    private void drawInGameUI(Graphics2D g2) {
        
        if (gamePanel.player == null) return;   // safety
        
        int maxAllowed = (int) (gamePanel.getWidth() / 1.5 - 40);   // never exceed this amt
        int x = HUD_X;
        int y = HUD_Y;
        
        // sheild
        if (gamePanel.player.maxShield > 0) {
            int shW = Math.min(scaleWidth(gamePanel.player.maxShield, BASE_SHIELD), maxAllowed);
            drawBar(g2, x, y, shW, SH_HEIGHT, gamePanel.player.shield, gamePanel.player.maxShield, new Color(80,160,240), "SH");
            y += SH_HEIGHT + HUD_GAP_Y;
        }
            
        // health
        if (gamePanel.player.maxHealth > 0) {
            int hpW = Math.min(scaleWidth(gamePanel.player.maxHealth, BASE_HEALTH), maxAllowed);
            drawBar(g2, x, y, hpW, HP_HEIGHT, gamePanel.player.health, gamePanel.player.maxHealth, new Color(200,60,60),  "HP");
            y += HP_HEIGHT + HUD_GAP_Y;
        }
        
        // Mana
        if (gamePanel.player.maxMana > 0) {
            int mpW = Math.min(scaleWidth(gamePanel.player.maxMana, BASE_MANA), maxAllowed);
            drawBar(g2, x, y, mpW, MP_HEIGHT, gamePanel.player.mana,  gamePanel.player.maxMana, new Color(120,80,220),  "MP");
        }
            
        // this.drawShieldBar(g2, this.gamePanel.player.shield);
        // this.drawHealthBar(g2, this.gamePanel.player.health);
        // this.drawManaBar(g2, this.gamePanel.player.mana);
    }

    private void drawScreenBackground(Graphics2D g2, String screenTitle) {
        g2.drawImage(this.gameTitleBG, 0, 0, gamePanel.screenWidth, gamePanel.screenHeight, null);
        g2.drawImage(this.gameTitle, centerX() - 300 + 15, gamePanel.screenHeight / 8 - 150, 600, 300, null);
        g2.setColor(Color.WHITE);
        g2.setFont(gameFont);
        g2.drawString(screenTitle, centerX() - 80, gamePanel.screenHeight / 4 + 30);
    }

    private String fact;
    public void drawLoadScreen(Graphics2D g2) {
        this.logInfo("generating new level");
        g2.setColor(this.gamePanel.backgroundColor);
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);

        // draw buffer wheel
        int centerX = gamePanel.screenWidth / 2;
        int centerY = gamePanel.screenHeight / 2;
        int outerRadius = 60;
        int innerRadius = 40;
        int arcAngle = (loadScreenTimer * 360) / (gamePanel.FPS * 3);

        g2.setColor(Color.WHITE);
        g2.fillArc(centerX - outerRadius, centerY - outerRadius, outerRadius * 2, outerRadius * 2, 0, arcAngle);
        g2.setColor(this.gamePanel.backgroundColor);
        g2.fillArc(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2, 0, arcAngle);

        // Draw loading text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("Loading...", centerX - 50, centerY + 120);

        // Draw random game fact
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.drawString("Tips & Facts", this.getXForCenteredText(g2, "Tips & Facts"), this.gamePanel.screenHeight - 80);

        if (fact == null) fact = this.gameFacts[this.randomNum(0, this.gameFacts.length - 1)];

        g2.drawString(fact, this.getXForCenteredText(g2, fact), this.gamePanel.screenHeight - 40);

        if (this.loadScreenTimer > this.gamePanel.FPS * 5) { // 5 seconds has passed
            this.loadScreenTimer = 0;
            fact = null;
            this.gamePanel.keyHandler = null;
            this.gamePanel.generateNewLevel();
            this.gamePanel.gameState = GamePanel.GameState.PLAYING;
        }
        this.loadScreenTimer++;
        
    }


    
    //
    //// Add Functions
    // 
    private void addTitleButtons(){
        this.clearUI();
        this.makeButton("Login",centerX()-100,centerY()-60,200,40,e->{
            gamePanel.setGameState(GamePanel.GameState.LOGIN);
            logInfo("Opening login screen.");
        });
        this.makeButton("Sign Up",centerX()-100,centerY()-10,200,40,e->{
            gamePanel.setGameState(GamePanel.GameState.SIGNUP);
            logInfo("Opening signup screen.");
        });
        this.makeButton("Quit Game",centerX()-100,centerY()+40,200,40,e->System.exit(0));
    }

    public void addLoginScreenButtons() {
        this.clearUI();

        loginUsernameField = new JTextField();
        loginUsernameField.setBounds(centerX() - 100, centerY() - 80, 200, 30);
        activeUI.add(loginUsernameField);
        gamePanel.add(loginUsernameField);

        loginPasswordField = new JPasswordField();
        loginPasswordField.setBounds(centerX() - 100, centerY() - 40, 200, 30);
        activeUI.add(loginPasswordField);
        gamePanel.add(loginPasswordField);

        this.makeButton("Log In",
            centerX() - 100, centerY() + 10, 200, 40,
            e -> {
                String u = loginUsernameField.getText().trim();
                String p = new String(loginPasswordField.getPassword());
                if (u.isBlank() || p.isBlank()) { logError("Enter username & password."); return; }

                gamePanel.user = gamePanel.dbManager.loginUser(u, p);
                if (gamePanel.user != null) {
                    logInfo("User logged in.");
                    gamePanel.setGameState(GamePanel.GameState.MAIN_MENU);
                } else {
                    logError("Login failed.");
                }
            });

        this.makeButton("Create Account",
            centerX() - 100, centerY() + 60, 200, 40,
            e -> gamePanel.setGameState(GamePanel.GameState.SIGNUP));

        this.makeButton("Back",
            centerX() - 100, centerY() + 110, 200, 40,
            e -> gamePanel.setGameState(GamePanel.GameState.TITLE));
    }

    public void addSignupScreenButtons() {
        this.clearUI();

        signupUsernameField = new JTextField();
        signupEmailField    = new JTextField();
        signupPasswordField = new JPasswordField();
        signupConfirmPasswordField = new JPasswordField();

        int y = centerY() - 110;
        JTextField[] fields = {
            signupUsernameField, signupEmailField,
            signupPasswordField, signupConfirmPasswordField
        };
        for (JTextField f : fields) {
            f.setBounds(centerX() - 100, y, 200, 30);
            activeUI.add(f); gamePanel.add(f);
            y += 40;
        }

        this.makeButton("Register",
            centerX() - 100, y, 200, 40,
            e -> {
                String u = signupUsernameField.getText().trim();
                String em = signupEmailField.getText().trim();
                String p1 = new String(signupPasswordField.getPassword());
                String p2 = new String(signupConfirmPasswordField.getPassword());

                if (u.isBlank() || p1.isBlank()) { logError("Username & password required."); return; }
                if (!p1.equals(p2))               { logError("Passwords don't match.");     return; }

                if (gamePanel.dbManager.registerUser(u, p1, em)) {
                    gamePanel.user = gamePanel.dbManager.loginUser(u, p1);
                    logInfo("User registered.");
                    gamePanel.setGameState(GamePanel.GameState.MAIN_MENU);
                } else {
                    logError("Signup failed.");
                }
            });

        this.makeButton("Back",
            centerX() - 100, y + 50, 200, 40,
            e -> gamePanel.setGameState(GamePanel.GameState.LOGIN));
    }

    public void addMainMenuButtons() {
        this.clearUI();

        this.makeButton("New Game",
            centerX() - 100, centerY() - 80, 200, 40,
            e -> {
                logInfo("New game started.");
                gamePanel.currentSlot = -1;
                gamePanel.newGame();
                gamePanel.setGameState(GamePanel.GameState.PLAYING);
            });

        this.makeButton("Load Game",
            centerX() - 100, centerY() - 30, 200, 40,
            e -> gamePanel.setGameState(GamePanel.GameState.SAVE_SLOT_SELECTION));

        this.makeButton("Multiplayer",
            centerX() - 100, centerY() + 20, 200, 40,
            e -> gamePanel.setGameState(GamePanel.GameState.MULTIPLAYER_MENU));

        this.makeButton("Log Out",
            centerX() - 100, centerY() + 70, 200, 40,
            e -> {
                gamePanel.user = null;
                logError("User logged out.");
                gamePanel.setGameState(GamePanel.GameState.TITLE);
            });

        this.makeButton("Quit Game",
            centerX() - 100, centerY() + 120, 200, 40,
            e -> System.exit(0));
    }

    public void addSaveSlotButtons() {
        this.clearUI();
        if (gamePanel.user == null) { logError("Log in first."); return; }

        gamePanel.user.updateSaveSlots(gamePanel.dbManager);

        for (int i = 0; i < 3; i++) {
            final int slot = i + 1;
            int saveId = gamePanel.user.saveData[i];
            String label = (saveId == -1) ? "Empty Slot " + slot : "Load Save " + slot;

            this.makeButton(label,
                centerX() - 100, centerY() - 80 + 50*i, 200, 40,
                e -> {
                    gamePanel.currentSlot = slot;
                    if (saveId == -1) {
                        logError("Starting new game in slot " + slot);
                        gamePanel.newGame();
                    } else {
                        logError("Loading slot " + slot);
                        gamePanel.loadProgress(slot);
                    }
                    gamePanel.setGameState(GamePanel.GameState.PLAYING);
                    this.gamePanel.requestFocus();
                });
        }

        this.makeButton("Back",
            centerX() - 100, centerY() + 90, 200, 40,
            e -> gamePanel.setGameState(GamePanel.GameState.MAIN_MENU));
    }

    public void addMultiplayerMenuButtons() {
        this.clearUI();

        this.makeButton("Host Game",
            centerX() - 120, centerY() - 100, 240, 40,
            e -> {
                gamePanel.prepareToHostMultiplayer();
                gamePanel.setGameState(GamePanel.GameState.HOST_LOBBY);
            });

        this.makeButton("Join Game",
            centerX() - 120, centerY() - 50, 240, 40,
            e -> gamePanel.setGameState(GamePanel.GameState.JOIN_LOBBY));

        JButton pvp = this.makeButton("PvP (Coming Soon)",
            centerX() - 120, centerY(), 240, 40,
            null);
        pvp.setEnabled(false);

        JButton ffa = this.makeButton("Free For All (Coming Soon)",
            centerX() - 120, centerY() + 50, 240, 40,
            null);
        ffa.setEnabled(false);

        JButton gg  = this.makeButton("Gun Game (Coming Soon)",
            centerX() - 120, centerY() + 100, 240, 40,
            null);
        gg.setEnabled(false);

        this.makeButton("Back",
            centerX() - 120, centerY() + 150, 240, 40,
            e -> gamePanel.setGameState(GamePanel.GameState.MAIN_MENU));
    }

    public void addHostLobbyScreen() {

        clearUI();

        // session code
        JLabel codeLabel = new JLabel("Session Code: "+ gamePanel.getSessionCode(), SwingConstants.CENTER);
        codeLabel.setFont(new Font("Monospaced",Font.BOLD,26));
        codeLabel.setForeground(Color.WHITE);
        codeLabel.setBounds(centerX()-200, centerY()-140, 400, 40);
        gamePanel.add(codeLabel);
        activeUI.add(codeLabel);

        /* table of clients */
        LobbyTableModel model = new LobbyTableModel(gamePanel.lobbyClients);
        JTable table = new JTable(model);
        table.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(centerX()-200, centerY()-90, 400, 150);
        gamePanel.add(scroll);  activeUI.add(scroll);

        /* refresh every 0.5 s – stop when UI is rebuilt */
        javax.swing.Timer t = new javax.swing.Timer(500, e -> model.fireTableDataChanged());
        t.start();   activeUI.add(new javax.swing.JLabel(){ public void removeNotify(){ t.stop(); }});

        /* START / READY button */
        if (gamePanel.isHost()) {
            makeButton("Start Game",
                centerX()-100, centerY()+80, 200, 40,
                e -> {
                    if (gamePanel.allClientsReady()) gamePanel.startMultiplayerGame();
                    else logError("Everyone must be ready.");
                });
        } else {
            JButton ready = makeButton(gamePanel.localReady ? "Unready" : "Ready",
                centerX()-100, centerY()+80, 200, 40, null);
            ready.addActionListener(ev -> {
                boolean want = ready.getText().equals("Ready");
                try {
                    gamePanel.setOwnReady(want);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                ready.setText(want ? "Unready" : "Ready");
            });
        }

        makeButton("Back",
            centerX()-100, centerY()+130, 200, 40,
            e -> gamePanel.leaveMultiplayerLobby());
    }

    public void addJoinLobbyScreen() {

        clearUI();

        // UNCONNECTED CLIENT
        if (gamePanel.networkClient == null || !gamePanel.networkClient.isConnected()) {

            JTextField codeField = new JTextField();
            codeField.setFont(new Font("Monospaced", Font.BOLD, 32));
            codeField.setHorizontalAlignment(JTextField.CENTER);
            codeField.setBounds(centerX() - 120, centerY() - 50, 240, 50);

            codeField.addKeyListener(new KeyAdapter() {
                @Override public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();

                    if (!Character.isLetterOrDigit(c) ||
                        codeField.getText().length() >= 6) {
                        e.consume();
                        return;
                    }
                    e.setKeyChar(Character.toUpperCase(c));
                }
            });

            activeUI.add(codeField);
            gamePanel.add(codeField);

            makeButton("Connect",
                centerX() - 100, centerY() + 10, 200, 40,
                e -> {
                    String code = codeField.getText().trim().toUpperCase();
                    if (code.length() != 6) { logError("Code must be 6 characters."); return; }

                    if (gamePanel.joinMultiplayerGame(code)) {
                        this.logInfo("Joined lobby " + code);
                        gamePanel.setGameState(GamePanel.GameState.JOIN_LOBBY);
                    } else {
                        this.logError("Failed to join lobby " + code);
                    }
                });

            makeButton("Back", centerX() - 100, centerY() + 70, 200, 40,
                e -> gamePanel.setGameState(GamePanel.GameState.MULTIPLAYER_MENU));

            codeField.requestFocusInWindow();
            return;
        }

        // CONNECTED CLIENT
        JLabel title = new JLabel("Lobby - waiting for host…", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setBounds(centerX() - 200, centerY() - 140, 400, 30);
        activeUI.add(title);  
        gamePanel.add(title);

        // players table
        String[] cols = {"Username", "IP", "Ready"};
        DefaultTableModel model =
            new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { 
                    return false; 
                }
            };
        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(centerX() - 200, centerY() - 100, 400, 120);
        activeUI.add(sp); gamePanel.add(sp);

        // refresh every half second
        new javax.swing.Timer(500, ev -> {
            model.setRowCount(0);
            synchronized (gamePanel.lobbyClients) {
                for (GamePanel.LobbyClient c : gamePanel.lobbyClients)
                    model.addRow(new Object[]{c.username, c.ip, c.ready ? "✓" : "✗"});
            }
        }).start();

        // Ready / Unready toggle
        JButton readyBtn = makeButton("Ready", centerX() - 100, centerY() + 40, 200, 40, null);
        
        readyBtn.addActionListener(e -> {
            boolean wantReady = readyBtn.getText().equals("Ready");
            gamePanel.networkClient.sendReadyState(wantReady);
            readyBtn.setText(wantReady ? "Unready" : "Ready");
        });

        // Leave lobby
        makeButton("Back",
            centerX() - 100, centerY() + 90, 200, 40,
            e -> gamePanel.leaveJoinLobby());
    }

    public void addSaveSlotOverwritePrompt() {
        this.clearUI();

        JLabel lbl = new JLabel("Choose Save Slot to Overwrite:");
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(centerX() - 150, centerY() - 90, 300, 30);
        activeUI.add(lbl);
        gamePanel.add(lbl);

        for (int i = 0; i < 3; i++) {
            final int slot = i + 1;
            this.makeButton("Save Slot " + slot,
                centerX() - 100, centerY() - 40 + 50*i, 200, 40,
                e -> {
                    gamePanel.saveProgress(slot);
                    logError("Saved to slot " + slot);
                    gamePanel.setGameState(GamePanel.GameState.TITLE);
                });
        }
    }

    public void addPauseButtons() {
        this.clearUI();

        int boxWidth = 240;
        int x = centerX() - boxWidth/2;
        int yBase = centerY() - 30;     // vertical anchor
        int gap   = 50;

        this.makeButton("Resume", x, yBase, boxWidth, 40, e -> {
            gamePanel.setGameState(GamePanel.GameState.PLAYING);
            logError("Resumed game.");
        });

        this.makeButton("Save & Quit", x, yBase + gap, boxWidth, 40, e -> {
            /* open the overwrite chooser right on top of the pause-overlay */
            addSaveSlotOverwritePrompt();
        });

        this.makeButton("Quit to Menu", x, yBase + 2*gap, boxWidth, 40, e -> {
            if (gamePanel.user == null) {
                gamePanel.setGameState(GamePanel.GameState.TITLE);
            } else {
                gamePanel.setGameState(GamePanel.GameState.MAIN_MENU);
            }
        });
    }

    public void addEndButtons() {
        this.clearUI();

        int boxWidth = 240;
        int x = centerX() - boxWidth/2;
        int yBase = centerY() + 40;   // below “Game Over / Score”

        this.makeButton("New Game", x, yBase, boxWidth, 40, e -> {
            logError("Starting new run.");
            gamePanel.newGame();
            gamePanel.setGameState(GamePanel.GameState.PLAYING);
        });

        this.makeButton("Back to Title", x, yBase + 50, boxWidth, 40, e -> {
            gamePanel.setGameState(GamePanel.GameState.TITLE);
        });

        this.makeButton("Quit Game", x, yBase + 100, boxWidth, 40, e -> System.exit(0));
    }



    //
    //// Console:
    //
    private enum ConsoleType { INFO, ERROR }
    private ConsoleType consoleType = ConsoleType.INFO;
    private String consoleText  = "";
    private void setConsole(String msg, ConsoleType t) {
        consoleType = t;
        consoleText = "Console >> " + msg;
    }
    public void logInfo(String msg)  {
        java.awt.EventQueue.invokeLater(() ->
            this.gamePanel.gameUI.setConsole(msg, ConsoleType.INFO));
    }
    public void logError(String msg) {
        java.awt.EventQueue.invokeLater(() ->
            this.gamePanel.gameUI.setConsole(msg, ConsoleType.ERROR));
    }



    //
    //// Utilities:
    //
    private int getXForCenteredText(Graphics2D g2, String text) {
        FontMetrics metrics = g2.getFontMetrics(g2.getFont());
        int x = (this.gamePanel.screenWidth - metrics.stringWidth(text)) / 2;
        return x;
    }

    private int randomNum(int min, int max) { // Inclusive
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    private int centerX() {
        return gamePanel.screenWidth / 2;
    }

    private int centerY() {
        return gamePanel.screenHeight / 2;
    }

    private JButton makeButton(String text, int x, int y, int w, int h, java.awt.event.ActionListener onClick) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        
        b.setBackground(BTN_BG);
        b.setForeground(BTN_FG);
        b.setBorder(BTN_BORDER);
        b.setFocusPainted(false);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // hover effect
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(BTN_BG_HOVER);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(BTN_BG);
            }
        });


        if (onClick != null) b.addActionListener(onClick);

        activeUI.add(b);
        gamePanel.add(b);
        return b;
    }

    private void clearUI() {
        for (java.awt.Component c : activeUI) {
            gamePanel.remove(c);
        }
        activeUI.clear();
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    private static int scaleWidth(int currentMax, int baseMax) {
        if (baseMax == 0) return FULL_W;       // safety
        return FULL_W * currentMax / baseMax;
    }

    private static void drawBar(Graphics2D g, int x, int y, int w, int h, int current, int max, Color fill, String label) {
        // outline & bevel
        g.setColor(Color.BLACK);
        g.fillRect(x - 2, y - 2, w + 4, h + 4);
        g.setColor(Color.WHITE);
        g.drawRect(x - 2, y - 2, w + 4, h + 4);

        // filled fraction
        int filled = (int)(w * Math.max(0, Math.min(1, (double)current / max)));
        g.setColor(fill);
        g.fillRect(x, y, filled, h);

        // text:  SH  400 / 560
        g.setFont(new Font("SansSerif", Font.BOLD, h - 4));
        FontMetrics fm = g.getFontMetrics();
        String txt = label + "  " + current + " / " + max;
        int textX = x + 6;                                   // left–aligned
        int textY = y + (h + fm.getAscent() - fm.getDescent()) / 2;
        g.setColor(Color.WHITE);
        g.drawString(txt, textX, textY);
    }

    class LobbyTableModel extends AbstractTableModel {

        private final String[]  cols = {"Username","IP","Ready"};
        private final List<GamePanel.LobbyClient> data;

        LobbyTableModel(List<GamePanel.LobbyClient> d){ this.data = d; }

        @Override 
        public int getRowCount() { 
            return data.size(); 
        }

        @Override 
        public int getColumnCount() { 
            return cols.length; 
        }

        @Override 
        public String getColumnName(int c) { 
            return cols[c]; 
        }
        
        @Override 
        public Object getValueAt(int r,int c) {
            GamePanel.LobbyClient cl = data.get(r);
            return switch(c){
                case 0 -> cl.username;
                case 1 -> cl.ip;
                case 2 -> cl.ready ? "✓" : "✗";
                default -> "";
            };
        }
    }

    class ClientInfo {
        public String username;
        public String ipAddress;
        public boolean ready;

        public ClientInfo(String username, String ipAddress, boolean ready) {
            this.username = username;
            this.ipAddress = ipAddress;
            this.ready = ready;
        }
    }

}