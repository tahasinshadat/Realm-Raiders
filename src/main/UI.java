package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import elements.User;

public class UI {
    GamePanel gamePanel;

    // Fonts
    Font titleFont;
    Font buttonFont;
    Font labelFont;
    Font messageFont;
    Font slotButtonFont;
    Font inputFont;
    Font gameFont;

    // Colors
    Color bgColor = new Color(30, 30, 70); // Dark blue-ish
    Color buttonBgColor = new Color(70, 70, 110);
    Color buttonFgColor = Color.WHITE;
    Color textFieldBgColor = new Color(50, 50, 90);
    Color textFieldFgColor = Color.WHITE;
    Color borderColor = new Color(120, 120, 160);
    Color messageColorInfo = new Color(200, 200, 100); // Yellowish
    Color messageColorError = new Color(255, 100, 100);  // Reddish
    Color messageColorSuccess = new Color(100, 255, 100); // Greenish

    // Common UI Elements (managed by updateUIComponents)
    private List<JComponent> activeComponents = new ArrayList<>();

    // --- Buttons ---
    // Title Screen
    private JButton loginNavButton;
    private JButton signUpNavButton;
    private JButton newGameButton;
    private JButton loadGameButton;
    private JButton multiplayerNavButton;
    private JButton controlsButton;
    private JButton quitButton;
    private JButton logoutButton;

    // Auth Screens
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JButton attemptLoginButton;
    private JButton attemptSignUpButton;

    // Save/Load Slot Screen
    private JButton[] saveSlotButtons = new JButton[3];
    private JButton[] loadSlotButtons = new JButton[3];
    private String currentSlotSelectionAction = "LOAD"; // "LOAD", "SAVE", "SAVE_NEW"
    private int gameStateBeforeSlotSelection = GamePanel.TITLE_STATE;


    // Multiplayer Screen (Placeholders)
    private JButton hostGameButton;
    private JButton joinGameButton;
    private JTextField sessionCodeField;
    private JButton pvpButton;
    private JButton freeForAllButton;
    private JButton gunGameButton;
    private JButton saveAndQuitButton;
    private JButton backToTitleScreenButton;

    // General Navigation
    private JButton backButton; // Context-sensitive back button

    // Other UI elements
    private String uiMessage = "";
    private Color currentMessageColor = messageColorInfo;

    public BufferedImage gameTitleImg;
    public BufferedImage gameTitleBGImg;

    // In-Game UI (Health, Mana, etc.)
    private int healthBarHeight = 25;
    private int shieldBarHeight = healthBarHeight / 2;
    private int manaBarHeight = healthBarHeight / 3;
    private int barSpacing = 3;
    private int barCornerX = 15;
    private int barCornerY = 15;

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
    private String fact;

    public UI(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.gamePanel.gameUI = this;
        this.gameTitleImg = this.gamePanel.assetManager.loadImage("../assets/icons/title.png");
        this.gameTitleBGImg = this.gamePanel.assetManager.loadImage("../assets/icons/titleBG.png");

        // Initialize Fonts
        this.titleFont = new Font("Arial", Font.BOLD, 48);
        this.buttonFont = new Font("Arial", Font.BOLD, 18);
        this.labelFont = new Font("Arial", Font.PLAIN, 18);
        this.messageFont = new Font("Arial", Font.ITALIC, 16);
        this.slotButtonFont = new Font("Arial", Font.PLAIN, 16);
        this.inputFont = new Font("Arial", Font.BOLD, 16);
        this.gameFont = new Font("Arial", Font.PLAIN, 16);

        this.initializeAllSwingComponents();
        this.addActionListenersToComponents();
    }

    //
    //// Creator Functions
    //
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(this.buttonFont);
        button.setForeground(this.buttonFgColor);
        button.setBackground(this.buttonBgColor);
        Border line = BorderFactory.createLineBorder(this.borderColor, 1);
        Border padding = BorderFactory.createEmptyBorder(10, 20, 10, 20);
        button.setBorder(BorderFactory.createCompoundBorder(line, padding));
        button.setFocusPainted(false);
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(buttonBgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(buttonBgColor);
            }
        });
        return button;
    }

    private JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(this.inputFont);
        field.setBackground(this.textFieldBgColor);
        field.setForeground(this.textFieldFgColor);
        field.setCaretColor(Color.LIGHT_GRAY);
        Border line = BorderFactory.createLineBorder(this.borderColor, 1);
        Border padding = BorderFactory.createEmptyBorder(8, 8, 8, 8);
        field.setBorder(BorderFactory.createCompoundBorder(line, padding));
        return field;
    }

    private JPasswordField createPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(this.inputFont);
        field.setBackground(this.textFieldBgColor);
        field.setForeground(this.textFieldFgColor);
        field.setCaretColor(Color.LIGHT_GRAY);
        Border line = BorderFactory.createLineBorder(this.borderColor, 1);
        Border padding = BorderFactory.createEmptyBorder(8, 8, 8, 8);
        field.setBorder(BorderFactory.createCompoundBorder(line, padding));
        return field;
    }


    //
    //// Utility Functions
    //
    private void clearInputFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        emailField.setText("");
        if(sessionCodeField != null) sessionCodeField.setText("");
    }

    private void setUIMessage(String message) {
        setUIMessage(message, messageColorInfo);
    }

    private void setUIMessage(String message, Color color) {
        this.uiMessage = message;
        this.currentMessageColor = color;
        if (this.gamePanel != null) {
            this.gamePanel.repaint(); // Request repaint to show new message
        }
    }

    private void drawCenteredString(Graphics2D g2, String text, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (this.gamePanel.screenWidth - fm.stringWidth(text)) / 2;
        g2.drawString(text, x, y);
    }

    private void drawUIMessage(Graphics2D g2) {
        if (this.uiMessage != null && !this.uiMessage.isEmpty()) {
            g2.setFont(this.messageFont);
            g2.setColor(this.currentMessageColor);
            FontMetrics fm = g2.getFontMetrics();
            int msgWidth = fm.stringWidth(this.uiMessage);
            // Position message at the bottom center, above where buttons usually are
            g2.drawString(this.uiMessage, (this.gamePanel.screenWidth - msgWidth) / 2, this.gamePanel.screenHeight - 70);
        }
    }

    private int getXForCenteredText(Graphics2D g2, String text) {
        FontMetrics metrics = g2.getFontMetrics(g2.getFont());
        int x = (this.gamePanel.screenWidth - metrics.stringWidth(text)) / 2;
        return x;
    }

    private int randomNum(int min, int max) { // Inclusive
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }



    //
    //// Setup Functions
    //
    private void initializeAllSwingComponents() {
        // Title Screen
        loginNavButton = createButton("Login");
        signUpNavButton = createButton("Sign Up");
        newGameButton = createButton("New Game");
        loadGameButton = createButton("Load Game");
        multiplayerNavButton = createButton("Multiplayer");
        controlsButton = createButton("Controls");
        quitButton = createButton("Quit Game");
        logoutButton = createButton("Logout");

        // Auth Screens
        usernameField = createTextField(20);
        passwordField = createPasswordField(20);
        confirmPasswordField = createPasswordField(20);
        emailField = createTextField(20);
        attemptLoginButton = createButton("LOGIN");
        attemptSignUpButton = createButton("CREATE ACCOUNT");

        // Save/Load Slots
        for (int i = 0; i < 3; i++) {
            saveSlotButtons[i] = createButton("Save Slot " + (i + 1)); // Text updated dynamically
            loadSlotButtons[i] = createButton("Load Slot " + (i + 1)); // Text updated dynamically
        }

        // Multiplayer Screen
        hostGameButton = createButton("Host New Game");
        joinGameButton = createButton("Join with Code");
        sessionCodeField = createTextField(8); // For session code input
        pvpButton = createButton("1 v 1 (Soon)");
        freeForAllButton = createButton("Free For All (Soon)");
        gunGameButton = createButton("Gun Game (Soon)");

        // General
        backButton = createButton("Back");
        saveAndQuitButton = createButton("Save & Quit");
        backToTitleScreenButton = createButton("Return to Title");
        
    }

    private void addActionListenersToComponents() {
        // TITLE SCREEN ACTIONS
        loginNavButton.addActionListener(e -> {
            this.gamePanel.setGameState(GamePanel.LOGIN_STATE);
            setUIMessage("Enter your credentials to login.");
        });

        signUpNavButton.addActionListener(e -> {
            this.gamePanel.setGameState(GamePanel.SIGNUP_STATE);
            setUIMessage("Create your Realm Raiders account.");
        });

        newGameButton.addActionListener(e -> {
            if (this.gamePanel.user == null) {
                setUIMessage("Please login or sign up to start a new game.", messageColorError);
                this.gamePanel.setGameState(GamePanel.LOGIN_STATE);
            } else {
                this.currentSlotSelectionAction = "SAVE_NEW"; // Special action for initiating a new game
                this.gameStateBeforeSlotSelection = GamePanel.TITLE_STATE;
                this.gamePanel.setGameState(GamePanel.SAVE_SLOT_SELECTION_STATE);
                setUIMessage("Select a slot for your new adventure!");
            }
        });

        loadGameButton.addActionListener(e -> {
            if (this.gamePanel.user == null) {
                setUIMessage("Please login to load a game.", messageColorError);
                this.gamePanel.setGameState(GamePanel.LOGIN_STATE);
            } else {
                this.currentSlotSelectionAction = "LOAD";
                this.gameStateBeforeSlotSelection = GamePanel.TITLE_STATE;
                this.gamePanel.setGameState(GamePanel.SAVE_SLOT_SELECTION_STATE);
                setUIMessage("Select a game slot to load.");
            }
        });

        multiplayerNavButton.addActionListener(e -> {
             if (this.gamePanel.user == null) {
                setUIMessage("Please login for multiplayer.", messageColorError);
                this.gamePanel.setGameState(GamePanel.LOGIN_STATE);
            } else {
                this.gamePanel.setGameState(GamePanel.MULTIPLAYER_MENU_STATE);
                setUIMessage("Multiplayer (Work In Progress)");
            }
        });

        controlsButton.addActionListener(e -> this.gamePanel.setGameState(GamePanel.MENU_SCREEN_STATE));
        
        logoutButton.addActionListener(e -> {
            if (this.gamePanel.user != null) {
                setUIMessage("Logged out " + this.gamePanel.user.username + ".", messageColorSuccess);
            }
            this.gamePanel.user = null;
            this.gamePanel.setGameState(GamePanel.TITLE_STATE);
        });

        quitButton.addActionListener(e -> {
            if(this.gamePanel.dbManager != null) this.gamePanel.dbManager.disconnect();
            System.exit(0);
        });

        // LOGIN SCREEN ACTIONS
        attemptLoginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                setUIMessage("Username and password are required.", messageColorError); return;
            }
            User user = this.gamePanel.dbManager.loginUser(username, password);
            if (user != null) {
                this.gamePanel.user = user;
                 this.gamePanel.user.updateSaveSlots(this.gamePanel.dbManager); // Fetch save slot IDs
                setUIMessage("Welcome back, " + user.username + "!", messageColorSuccess);
                this.gamePanel.setGameState(GamePanel.TITLE_STATE); // Or a main menu
            } else {
                setUIMessage("Login failed. Check username or password.", messageColorError);
            }
            clearInputFields();
        });

        // SIGNUP SCREEN ACTIONS
        attemptSignUpButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPass = new String(confirmPasswordField.getPassword());

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || confirmPass.isEmpty()) {
                setUIMessage("All fields are required.", messageColorError); return;
            }
            if (!password.equals(confirmPass)) {
                setUIMessage("Passwords do not match.", messageColorError); return;
            }
            if (password.length() < 6) { // Example validation
                setUIMessage("Password must be at least 6 characters.", messageColorError); return;
            }
            if (!email.contains("@") || !email.contains(".")) { // Basic email check
                setUIMessage("Please enter a valid email address.", messageColorError); return;
            }

            boolean success = this.gamePanel.dbManager.registerUser(username, password, email);
            if (success) {
                setUIMessage("Account created! Please login.", messageColorSuccess);
                this.gamePanel.setGameState(GamePanel.LOGIN_STATE);
            } else {
                // DatabaseManager's registerUser should print more specific error (e.g., username exists)
                setUIMessage("Signup failed. Username may be taken or DB error.", messageColorError);
            }
            clearInputFields();
        });

        // GENERAL BACK BUTTON
        backButton.addActionListener(e -> {
            int currentState = this.gamePanel.gameState;
            if (currentState == GamePanel.LOGIN_STATE || currentState == GamePanel.SIGNUP_STATE ||
                currentState == GamePanel.MENU_SCREEN_STATE || currentState == GamePanel.SAVE_SLOT_SELECTION_STATE ||
                currentState == GamePanel.MULTIPLAYER_MENU_STATE) {
                this.gamePanel.setGameState(GamePanel.TITLE_STATE);
            } else if (currentState == GamePanel.HOST_LOBBY_STATE || currentState == GamePanel.JOIN_LOBBY_STATE) {
                this.gamePanel.setGameState(GamePanel.MULTIPLAYER_MENU_STATE);
                // TODO: Add network cleanup if canceling host/join
            }
            // Add more specific back navigation if needed
            setUIMessage("");
            clearInputFields();
        });
        
        // PAUSE SCREEN
        saveAndQuitButton.addActionListener(e -> {
            if (this.gamePanel.user != null) {
                 this.gameStateBeforeSlotSelection = GamePanel.PAUSE_STATE; // Remember we came from pause
                 this.currentSlotSelectionAction = "SAVE";
                 this.gamePanel.setGameState(GamePanel.SAVE_SLOT_SELECTION_STATE);
                 setUIMessage("Select slot to save & quit.");
            } else {
                setUIMessage("Not logged in. Quitting without save.", Color.ORANGE);
                this.gamePanel.setGameState(GamePanel.TITLE_STATE);
            }
        });
        
        // END SCREEN
        backToTitleScreenButton.addActionListener(e -> { // This button is for the End Screen
            this.gamePanel.setGameState(GamePanel.TITLE_STATE);
        });


        // SAVE/LOAD SLOT BUTTONS
        for (int i = 0; i < 3; i++) {
            final int slotIndex = i; // 0, 1, 2
            final int slotNumber = i + 1; // 1, 2, 3

            saveSlotButtons[i].addActionListener(evt -> {
                if (this.gamePanel.user != null) {
                    if ("SAVE_NEW".equals(currentSlotSelectionAction)) {
                        this.gamePanel.newGame(); // This will set PLAYING_STATE, then saveProgress will be called by it.
                                                  // newGame should take slotNumber or UI should handle slot then call newGame with it.
                                                  // For now, let's assume newGame prepares the state, then we save.
                        this.gamePanel.dataHandler.saveProgress(this.gamePanel.user.userId, slotNumber);
                        setUIMessage("New game started and saved to Slot " + slotNumber + "!", messageColorSuccess);
                        this.gamePanel.user.updateSaveSlots(this.gamePanel.dbManager); // Refresh save data
                        this.gamePanel.setGameState(GamePanel.PLAYING_STATE); // Start playing
                    } else if ("SAVE".equals(currentSlotSelectionAction)) {
                        this.gamePanel.dataHandler.saveProgress(this.gamePanel.user.userId, slotNumber);
                        setUIMessage("Game saved to Slot " + slotNumber + "!", messageColorSuccess);
                        this.gamePanel.user.updateSaveSlots(this.gamePanel.dbManager);
                        if (this.gameStateBeforeSlotSelection == GamePanel.PAUSE_STATE) { // If "Save & Quit"
                            this.gamePanel.setGameState(GamePanel.TITLE_STATE);
                        } else { // If just "Save" from a future main menu save option
                            this.gamePanel.setGameState(this.gameStateBeforeSlotSelection); // Go back
                        }
                    }
                } else {
                     setUIMessage("Login required to save.", messageColorError);
                     this.gamePanel.setGameState(GamePanel.LOGIN_STATE);
                }
            });

            loadSlotButtons[i].addActionListener(evt -> {
                if (this.gamePanel.user != null) {
                    int gameSaveIdToLoad = this.gamePanel.user.saveData[slotIndex];
                    if (gameSaveIdToLoad != -1) {
                        this.gamePanel.loadProgress(gameSaveIdToLoad); // GamePanel's method
                        // GamePanel.loadProgress should set gameState to PLAYING_STATE on success
                        // and updateUIComponents. If it fails, it should set an error message
                        // and potentially revert to a safe state like TITLE_STATE.
                        if(this.gamePanel.gameState == GamePanel.PLAYING_STATE){
                            setUIMessage("Game loaded from Slot " + slotNumber + ".", messageColorSuccess);
                        } else {
                            setUIMessage("Failed to load game from Slot " + slotNumber + ".", messageColorError);
                            // GamePanel.loadProgress should ideally handle setting a safe state on failure
                        }
                    } else {
                        setUIMessage("Slot " + slotNumber + " is empty.", Color.ORANGE);
                    }
                } else {
                    setUIMessage("Login required to load.", messageColorError);
                    this.gamePanel.setGameState(GamePanel.LOGIN_STATE);
                }
            });
        }

        // MULTIPLAYER ACTIONS
        // hostGameButton.addActionListener(e -> { /* ... call NetworkManager.startMultiplayerSession ... */ });
        // joinGameButton.addActionListener(e -> { /* ... call NetworkManager.joinMultiplayerSession ... */ });
    }



    //
    //// Central & Core UI Functions
    //

    // UI Draw Function
    public void updateUIComponents() {
        SwingUtilities.invokeLater(() -> {
            // 1. Remove all previously added JComponents from GamePanel
            for (JComponent comp : this.activeComponents) {
                this.gamePanel.remove(comp);
            }
            this.activeComponents.clear(); // Clear the tracking list

            // 2. Add components based on the current game state
            switch (this.gamePanel.gameState) {
                case GamePanel.TITLE_STATE: addTitleScreenElements(); break;
                case GamePanel.LOGIN_STATE: addLoginScreenElements(); break;
                case GamePanel.SIGNUP_STATE: addSignUpScreenElements(); break;
                case GamePanel.MENU_SCREEN_STATE: addControlsScreenElements(); break;
                case GamePanel.SAVE_SLOT_SELECTION_STATE: addSaveSlotSelectionScreenElements(); break;
                case GamePanel.MULTIPLAYER_MENU_STATE: addMultiplayerMenuElements(); break;
                // case GamePanel.HOST_LOBBY_STATE: addHostLobbyElements(); break;
                // case GamePanel.JOIN_LOBBY_STATE: addJoinLobbyElements(); break;
                case GamePanel.PAUSE_STATE: addPauseScreenElements(); break;
                case GamePanel.END_STATE: addEndScreenElements(); break;
                // PLAYING_STATE and LOAD_STATE typically don't have global JButtons added this way
            }

            // 3. Add all components from activeComponents list to the GamePanel
            for (JComponent comp : this.activeComponents) {
                this.gamePanel.add(comp);
            }

            // 4. Refresh GamePanel
            this.gamePanel.revalidate();
            this.gamePanel.repaint();
            this.gamePanel.requestFocusInWindow(); // Ensure GamePanel can receive key events
        });
    }

    // UI Draw Function
    public void draw(Graphics2D g2) {
        // Apply anti-aliasing for smoother graphics and text
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Default font and color for text drawn directly (not JComponents)
        g2.setFont(this.gameFont); // General game font
        g2.setColor(Color.WHITE);

        switch (this.gamePanel.gameState) {
            case GamePanel.TITLE_STATE: drawTitleScreen(g2); break;
            case GamePanel.LOGIN_STATE: drawLoginScreen(g2); break;
            case GamePanel.SIGNUP_STATE: drawSignUpScreen(g2); break;
            case GamePanel.MENU_SCREEN_STATE: drawControlsScreen(g2); break; // Controls
            case GamePanel.SAVE_SLOT_SELECTION_STATE: drawSaveSlotSelectionScreen(g2); break;
            case GamePanel.MULTIPLAYER_MENU_STATE: drawMultiplayerMenuScreen(g2); break;
            // case GamePanel.HOST_LOBBY_STATE: drawHostLobbyScreen(g2); break;
            // case GamePanel.JOIN_LOBBY_STATE: drawJoinLobbyScreen(g2); break;
            case GamePanel.PLAYING_STATE: drawInGameUI(g2); break; // Overlays like health bar
            case GamePanel.PAUSE_STATE: drawPausedScreen(g2); break;
            case GamePanel.END_STATE: drawEndScreen(g2); break;
            case GamePanel.LOAD_STATE: drawLoadScreen(g2); break;
            default:
                // Fallback for unknown state
                g2.setColor(bgColor);
                g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
                g2.setColor(Color.RED);
                g2.setFont(this.titleFont);
                g2.drawString("Error: Unknown Game State (" + this.gamePanel.gameState + ")", 50, 100);
                break;
        }
    }



    //
    //// Component Functions
    //
    private void addTitleScreenElements() {
        int centerX = this.gamePanel.screenWidth / 2;
        int startY = this.gamePanel.screenHeight / 2 - 150;
        int btnWidth = 220; int btnHeight = 45; int spacing = 15;

        newGameButton.setBounds(centerX - btnWidth / 2, startY, btnWidth, btnHeight);
        loadGameButton.setBounds(centerX - btnWidth / 2, startY + (btnHeight + spacing), btnWidth, btnHeight);
        // multiplayerNavButton.setBounds(centerX - btnWidth / 2, startY + 2 * (btnHeight + spacing), btnWidth, btnHeight);
        controlsButton.setBounds(centerX - btnWidth / 2, startY + 2 * (btnHeight + spacing), btnWidth, btnHeight); // Adjusted Y
        quitButton.setBounds(centerX - btnWidth / 2, startY + 3 * (btnHeight + spacing), btnWidth, btnHeight);     // Adjusted Y

        this.activeComponents.add(newGameButton);
        this.activeComponents.add(loadGameButton);
        // this.activeComponents.add(multiplayerNavButton);
        this.activeComponents.add(controlsButton);
        this.activeComponents.add(quitButton);

        if (this.gamePanel.user == null) {
            loginNavButton.setBounds(centerX - btnWidth - spacing / 2, startY + 4 * (btnHeight + spacing) + 20, btnWidth, btnHeight);
            signUpNavButton.setBounds(centerX + spacing / 2, startY + 4 * (btnHeight + spacing) + 20, btnWidth, btnHeight);
            this.activeComponents.add(loginNavButton);
            this.activeComponents.add(signUpNavButton);
        } else {
            logoutButton.setBounds(centerX - btnWidth / 2, startY + 4 * (btnHeight + spacing) + 20, btnWidth, btnHeight);
            this.activeComponents.add(logoutButton);
        }
    }

    private void addLoginScreenElements() {
        int centerX = this.gamePanel.screenWidth / 2;
        int fieldWidth = 280; int fieldHeight = 35;
        int buttonWidth = 180; int buttonHeight = 40;
        int startY = this.gamePanel.screenHeight / 2 - 100;

        usernameField.setBounds(centerX - fieldWidth / 2, startY, fieldWidth, fieldHeight);
        passwordField.setBounds(centerX - fieldWidth / 2, startY + fieldHeight + 10, fieldWidth, fieldHeight);
        attemptLoginButton.setBounds(centerX - buttonWidth / 2, startY + 2 * fieldHeight + 30, buttonWidth, buttonHeight);
        backButton.setBounds(centerX - buttonWidth / 2, startY + 2 * fieldHeight + 30 + buttonHeight + 10, buttonWidth, buttonHeight);

        this.activeComponents.add(usernameField);
        this.activeComponents.add(passwordField);
        this.activeComponents.add(attemptLoginButton);
        this.activeComponents.add(backButton);
    }

    private void addSignUpScreenElements() {
        int centerX = this.gamePanel.screenWidth / 2;
        int fieldWidth = 280; int fieldHeight = 35;
        int buttonWidth = 200; int buttonHeight = 40;
        int startY = this.gamePanel.screenHeight / 2 - 140;

        usernameField.setBounds(centerX - fieldWidth / 2, startY, fieldWidth, fieldHeight);
        emailField.setBounds(centerX - fieldWidth / 2, startY + fieldHeight + 10, fieldWidth, fieldHeight);
        passwordField.setBounds(centerX - fieldWidth / 2, startY + 2 * (fieldHeight + 10), fieldWidth, fieldHeight);
        confirmPasswordField.setBounds(centerX - fieldWidth / 2, startY + 3 * (fieldHeight + 10), fieldWidth, fieldHeight);
        attemptSignUpButton.setBounds(centerX - buttonWidth / 2, startY + 4 * (fieldHeight + 10) + 20, buttonWidth, buttonHeight);
        backButton.setBounds(centerX - buttonWidth / 2, startY + 4 * (fieldHeight + 10) + 20 + buttonHeight + 10, buttonWidth, buttonHeight);

        this.activeComponents.add(usernameField);
        this.activeComponents.add(emailField);
        this.activeComponents.add(passwordField);
        this.activeComponents.add(confirmPasswordField);
        this.activeComponents.add(attemptSignUpButton);
        this.activeComponents.add(backButton);
    }
    
    private void addControlsScreenElements() {
        // Back button position is crucial for this screen
        backButton.setBounds(this.gamePanel.screenWidth / 2 - 100, this.gamePanel.screenHeight - 100, 200, 40);
        this.activeComponents.add(backButton);
    }

    private void addSaveSlotSelectionScreenElements() {
        int centerX = this.gamePanel.screenWidth / 2;
        int startY = this.gamePanel.screenHeight / 2 - 120;
        int buttonWidth = 300; int buttonHeight = 50; int spacing = 20;

        if (this.gamePanel.user == null) { // Should ideally not happen if UI flow is correct
            this.gamePanel.setGameState(GamePanel.LOGIN_STATE);
            setUIMessage("Please login first.", Color.RED);
            return;
        }
        // Refresh save slot data from DB
        this.gamePanel.user.updateSaveSlots(this.gamePanel.dbManager);


        for (int i = 0; i < 3; i++) {
            JButton btnToDisplay;
            String baseText;

            if ("LOAD".equals(this.currentSlotSelectionAction)) {
                btnToDisplay = loadSlotButtons[i];
                baseText = "Load Slot " + (i + 1);
            } else { // "SAVE" or "SAVE_NEW"
                btnToDisplay = saveSlotButtons[i];
                baseText = "Save to Slot " + (i + 1);
            }

            if (this.gamePanel.user.saveData[i] != -1) {
                btnToDisplay.setText(baseText + " (Data Exists)"); // TODO: Add timestamp later
                btnToDisplay.setBackground("LOAD".equals(currentSlotSelectionAction) ? new Color(0,0,100) : new Color(100,0,0) );
            } else {
                btnToDisplay.setText(baseText + " (Empty)");
                 btnToDisplay.setBackground("LOAD".equals(currentSlotSelectionAction) ? new Color(50,50,100) : new Color(0,100,0) );
            }
            btnToDisplay.setBounds(centerX - buttonWidth / 2, startY + i * (buttonHeight + spacing), buttonWidth, buttonHeight);
            this.activeComponents.add(btnToDisplay);
        }
        backButton.setBounds(centerX - 100, startY + 3 * (buttonHeight + spacing) + spacing, 200, 40);
        this.activeComponents.add(backButton);
    }
    
    private void addMultiplayerMenuElements() {
        int centerX = this.gamePanel.screenWidth / 2;
        int startY = this.gamePanel.screenHeight / 2 - 100;
        int btnWidth = 250; int btnHeight = 45; int spacing = 15;

        hostGameButton.setBounds(centerX - btnWidth / 2, startY, btnWidth, btnHeight);
        joinGameButton.setBounds(centerX - btnWidth / 2, startY + (btnHeight + spacing), btnWidth, btnHeight);
        sessionCodeField.setBounds(centerX - 100 / 2, startY + 2 * (btnHeight + spacing), 100, 35); // Smaller field for code
        backButton.setBounds(centerX - 100, startY + 3 * (btnHeight + spacing) + 20, 200, 40);

        this.activeComponents.add(hostGameButton);
        this.activeComponents.add(joinGameButton);
        this.activeComponents.add(sessionCodeField); // Add only if "Join" is the mode
        this.activeComponents.add(backButton);
    }

    private void addPauseScreenElements() {
        saveAndQuitButton.setBounds(this.gamePanel.screenWidth / 2 - 100, this.gamePanel.screenHeight / 2 + 80, 200, 40);
        this.activeComponents.add(saveAndQuitButton);
    }

    private void addEndScreenElements() {
        newGameButton.setBounds(this.gamePanel.screenWidth / 2 - 100, this.gamePanel.screenHeight / 2 + 60, 200, 40);
        backToTitleScreenButton.setBounds(this.gamePanel.screenWidth / 2 - 100, this.gamePanel.screenHeight / 2 + 110, 200, 40);
        quitButton.setBounds(this.gamePanel.screenWidth / 2 - 100, this.gamePanel.screenHeight / 2 + 160, 200, 40);
        
        this.activeComponents.add(newGameButton);
        this.activeComponents.add(backToTitleScreenButton);
        this.activeComponents.add(quitButton);
    }


    //
    //// Individual Screen Functions
    //
    private void drawTitleScreen(Graphics2D g2) {
        // Background
        if (this.gameTitleBGImg != null) {
            g2.drawImage(this.gameTitleBGImg, 0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight, null);
        } else {
            g2.setColor(bgColor);
            g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
        }
        // Title Image
        if (this.gameTitleImg != null) {
            int titleWidth = 600; int titleHeight = 300; // Adjust if known
            // More dynamic centering if image size is known: titleWidth = this.gameTitleImg.getWidth();
            g2.drawImage(this.gameTitleImg, (this.gamePanel.screenWidth - titleWidth) / 2, this.gamePanel.screenHeight / 8, titleWidth, titleHeight, null);
        } else {
            g2.setFont(this.titleFont.deriveFont(72f));
            g2.setColor(Color.ORANGE);
            drawCenteredString(g2, "Realm Raiders", this.gamePanel.screenHeight / 4);
        }
        
        // Logged in user message
        if (this.gamePanel.user != null) {
            g2.setFont(this.messageFont.deriveFont(Font.BOLD, 18f));
            g2.setColor(messageColorSuccess);
            String welcomeMsg = "Logged in as: " + this.gamePanel.user.username;
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(welcomeMsg, this.gamePanel.screenWidth - fm.stringWidth(welcomeMsg) - 20, 30);
        }
        drawUIMessage(g2); // General messages
    }

    private void drawLoginScreen(Graphics2D g2) {
        g2.setColor(bgColor);
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
        g2.setFont(this.titleFont);
        g2.setColor(Color.WHITE);
        drawCenteredString(g2, "Player Login", this.gamePanel.screenHeight / 4 - 20);

        // Labels for text fields are drawn relative to field positions
        g2.setFont(this.labelFont);
        int labelX = usernameField.getX() - 10 - g2.getFontMetrics(this.labelFont).stringWidth("Username:");
        g2.drawString("Username:", labelX, usernameField.getY() + usernameField.getHeight() / 2 + 5);
        g2.drawString("Password:", labelX, passwordField.getY() + passwordField.getHeight() / 2 + 5);
        
        drawUIMessage(g2);
    }

    private void drawSignUpScreen(Graphics2D g2) {
        g2.setColor(bgColor);
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
        g2.setFont(this.titleFont);
        g2.setColor(Color.WHITE);
        drawCenteredString(g2, "Create Account", this.gamePanel.screenHeight / 4 - 50);

        g2.setFont(this.labelFont);
        int labelX = usernameField.getX() - 10 - g2.getFontMetrics(this.labelFont).stringWidth("Confirm Password:"); // Use widest label for alignment
        g2.drawString("Username:", labelX, usernameField.getY() + usernameField.getHeight() / 2 + 5);
        g2.drawString("Email:", labelX, emailField.getY() + emailField.getHeight() / 2 + 5);
        g2.drawString("Password:", labelX, passwordField.getY() + passwordField.getHeight() / 2 + 5);
        g2.drawString("Confirm Password:", labelX, confirmPasswordField.getY() + confirmPasswordField.getHeight() / 2 + 5);
        
        drawUIMessage(g2);
    }

    private void drawSaveSlotSelectionScreen(Graphics2D g2) {
        g2.setColor(new Color(40,40,80)); // Slightly different background
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
        g2.setFont(this.titleFont);
        g2.setColor(Color.WHITE);
        String title = ("SAVE".equals(currentSlotSelectionAction) || "SAVE_NEW".equals(currentSlotSelectionAction)) ? "Save Game" : "Load Game";
        drawCenteredString(g2, title, this.gamePanel.screenHeight / 4 - 30);
        drawUIMessage(g2);
    }
    
    private void drawMultiplayerMenuScreen(Graphics2D g2) {
        g2.setColor(bgColor);
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight);
        g2.setFont(this.titleFont);
        g2.setColor(Color.WHITE);
        drawCenteredString(g2, "Multiplayer", this.gamePanel.screenHeight / 4 - 20);
        // Labels for fields if any (like session code field)
        if (sessionCodeField != null && sessionCodeField.isVisible()) { // Check if it's supposed to be visible
            g2.setFont(this.labelFont);
            g2.drawString("Session Code:", sessionCodeField.getX() - 10 - g2.getFontMetrics(this.labelFont).stringWidth("Session Code:"), sessionCodeField.getY() + sessionCodeField.getHeight() / 2 + 5);
        }
        drawUIMessage(g2);
    }

    public void drawLoadScreen(Graphics2D g2) { // display the load screen with a buffer wheel for 3 seconds

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
            this.gamePanel.gameState = GamePanel.PLAYING_STATE;
        }
        this.loadScreenTimer++;
        
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
    }

    private void drawPausedScreen(Graphics2D g2) {
        
        this.gamePanel.drawGameFrame(g2);
        this.drawInGameUI(g2);
        Color tint = new Color(0, 0, 0, 255/2); // semi transparent tint
        g2.setColor(tint);
        g2.fillRect(0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight); // Fill the entire screen

        // Draw the paused screen content
        Color pauseTint = new Color(0, 0, 0, 255/2);
        g2.setColor(pauseTint);
        g2.fillRect(this.gamePanel.screenWidth / 4, this.gamePanel.screenHeight / 4, this.gamePanel.screenWidth / 2, this.gamePanel.screenHeight / 2);
        g2.setColor(Color.WHITE);
        g2.setFont(this.gameFont);

        // Draw Text
        String text = "Game Paused";
        g2.drawString(text, this.getXForCenteredText(g2, text), this.gamePanel.screenHeight / 2);

        this.gameFont = new Font("Virgil", Font.PLAIN, 20);
        g2.setFont(this.gameFont);
        String escText = "Press ESC to Resume";
        g2.drawString(escText, this.getXForCenteredText(g2, escText), this.gamePanel.screenHeight / 2 + 35);
        this.gameFont = new Font("Virgil", Font.PLAIN, 40);

        // Draw Icon
        g2.drawImage(this.gameTitleImg, this.gamePanel.screenWidth / 2 - 150 + 6, this.gamePanel.screenHeight / 5 - 6, 300, 150, null);
    }

    private void drawEndScreen(Graphics2D g2) {
        // Draw the end screen
        g2.drawImage(this.gameTitleBGImg, 0, 0, this.gamePanel.screenWidth, this.gamePanel.screenHeight, null);
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
    }

    private void drawInGameUI(Graphics2D g2) {
        this.drawShieldBar(g2, this.gamePanel.player.shield);
        this.drawHealthBar(g2, this.gamePanel.player.health);
        this.drawManaBar(g2, this.gamePanel.player.mana);
    }

    private void drawShieldBar(Graphics2D g2, int shieldAmt) {
        g2.setColor(Color.BLACK);
        g2.fillRect(this.barCornerX - this.barSpacing, this.barCornerY - this.barSpacing, this.gamePanel.player.maxShield + this.barSpacing * 2, this.shieldBarHeight + this.barSpacing * 2);
        g2.setColor(Color.GRAY);
        g2.fillRect(this.barCornerX, this.barCornerY, shieldAmt, this.shieldBarHeight);
    }

    private void drawHealthBar(Graphics2D g2, int health) {
        g2.setColor(Color.BLACK);
        g2.fillRect(this.barCornerX - this.barSpacing, this.barCornerY + this.shieldBarHeight + this.barSpacing * 2, this.gamePanel.player.maxHealth + this.barSpacing * 2, this.healthBarHeight + this.barSpacing * 2);
        g2.setColor(Color.RED);
        g2.fillRect(this.barCornerX, this.barCornerY + this.shieldBarHeight + this.barSpacing * 3, health, this.healthBarHeight);
    }
    
    private void drawManaBar(Graphics2D g2, int mana) {
        g2.setColor(Color.BLACK);
        g2.fillRect(this.barCornerX - this.barSpacing, this.barCornerY + this.shieldBarHeight + this.healthBarHeight + this.barSpacing * 5, this.gamePanel.player.maxMana + this.barSpacing * 2, this.manaBarHeight + this.barSpacing * 2);
        g2.setColor(Color.BLUE);
        g2.fillRect(this.barCornerX, this.barCornerY + this.shieldBarHeight + this.healthBarHeight + this.barSpacing * 6, mana, this.manaBarHeight);
    }
    
}