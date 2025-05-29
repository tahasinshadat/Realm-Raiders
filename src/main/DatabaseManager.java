package main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import elements.User;
import java.sql.Timestamp;
import java.util.Date;

// idk:
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

// Spring Security's BCrypt:
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;


// Placeholder for a GameSave object/summary if you create one
// class GameSaveSummary {
//     public int saveId;
//     public String saveName;
//     public java.sql.Timestamp lastSavedTimestamp;
//     // Constructor and other fields as needed
// }


public class DatabaseManager {

    private Connection connection;
    private String databaseUrl; // e.g., "jdbc:sqlite:realm_raiders.db"

    public DatabaseManager(String dbName) {


        this.databaseUrl = "jdbc:sqlite:database/" + dbName;

        File dbDirectory = new File("../database/");
        File db = new File("../database/realm_raiders_data.db");
        if (!dbDirectory.exists()) {
            // create folder
            boolean dirCreated = dbDirectory.mkdirs();
            if (dirCreated) {
                System.out.println("Directory 'database' created successfully.");
            } else {
                System.err.println("Failed to create directory 'database'. Check permissions.");
            }
            
            // create db file
            try {
                boolean dbCreated = db.createNewFile();
                if (dirCreated) {
                    System.out.println("Directory 'database' created successfully.");
                } else {
                    System.err.println("Failed to create directory 'database'. Check permissions.");
                }
            } catch (IOException e) {
                System.err.println("Failed to create file 'realm_raiders_data.db'. Check permissions.");
            }

        }

        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found. Ensure sqlite-jdbc-3.49.1.0.jar is in the 'lib' folder and on the classpath.");
            e.printStackTrace();
        }
    }

    //
    //// DATABASE SETUP 
    //

    // Establish a connection to the database
    public boolean connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Already connected to " + databaseUrl);
                return true; // Already connected
            }
            
            // SQLite creates the database file if it doesn't exist upon connection.
            connection = DriverManager.getConnection(databaseUrl);
            System.out.println("Connection to SQLite database '" + databaseUrl + "' established.");
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to connect to SQLite database '" + databaseUrl + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Closes the database connection
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Successfully Closed DB");
            }
        } catch (SQLException e) {
            System.out.println("Error disconnecting from SQLite database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Initialize necessary tables
    public void initializeTables() {
        
        System.out.println("Initializing tables: ");
        
        // users table:
        System.out.println("Initializing User Table: ");
        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users ("
        + "user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "username TEXT NOT NULL UNIQUE,"
        + "password_hash TEXT NOT NULL,"
        + "email TEXT,"
        + "save1 INTEGER DEFAULT -1,"
        + "save2 INTEGER DEFAULT -1,"
        + "save3 INTEGER DEFAULT -1"
        + ");";
        
        // game_saves table:
        System.out.println("Initializing Game Saves Table: ");
        String createGameSavesTableSQL = "CREATE TABLE IF NOT EXISTS game_saves ("
        + "save_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "user_id INTEGER NOT NULL,"
        + "save_data TEXT NOT NULL," // Or BLOB
        + "last_saved_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
        + "FOREIGN KEY (user_id) REFERENCES users(user_id)"
        + ");";
        
        String createSaveIdIndexSQL = "CREATE INDEX IF NOT EXISTS idx_user_save_name "
                                      + "ON game_saves (user_id, save_id);";
                                      
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUserTableSQL);
            System.out.println("'users' table checked/created successfully.");
            stmt.execute(createGameSavesTableSQL);
            System.out.println("'game_saves' table checked/created successfully.");
            stmt.execute(createSaveIdIndexSQL);
            System.out.println("Index 'idx_user_save_id' on 'game_saves' checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating/checking tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //
    //// SAVE GAME
    //
    /*
    public boolean saveGame(int userId, int saveId, String gameStateData) {
        if (connection == null && !connect()) {
            System.err.println("Failed to connect (saveGame), cannot save game.");
            return false;
        }

        String currentTimeStampString = getCurrentTimeStamp().toString();
        
        if (saveId != -1) {
            // Update existing save slot
            String sql = "UPDATE game_saves SET save_data = ?" +
            ", last_saved_timestamp = CURRENT_TIMESTAMP WHERE save_id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, gameStateData);
                pstmt.setInt(2, saveId);
                pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: users.username")) {
                System.err.println("Username '" + username + "' already exists.");
            } else {
                System.err.println("Error registering user: " + e.getMessage());
            }
            return false;
        }
        } else {
            String sql = "INSERT INTO game_saves (user_id, save_data, last_saved_timestamp) VALUES (" +
            userId + ", '" + gameStateData + "', '" + currentTimeStampString + "');";
            return execStatement(sql);
        }
    }
    */

    public boolean saveGameToSlot(int userId, int slotNumber, String gameStateData) {
        if (connection == null && !connect()) return false;
        if (slotNumber < 1 || slotNumber > 3) return false;
        
        int[] userSlots = getUserSaveSlots(userId);
        int existingGameSaveIdInUserTable = userSlots[slotNumber - 1];
        String sql;

        try {
            if (existingGameSaveIdInUserTable != -1 && gameSaveExists(existingGameSaveIdInUserTable)) {
                // Slot has a valid game_save_id, and that record exists: UPDATE it
                sql = "UPDATE game_saves SET save_data = ?, last_saved_timestamp = CURRENT_TIMESTAMP WHERE save_id = ? AND user_id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, gameStateData);
                    pstmt.setInt(2, existingGameSaveIdInUserTable);
                    pstmt.setInt(3, userId); // Ensure the save being updated belongs to this user
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Game data updated in game_saves for save_id: " + existingGameSaveIdInUserTable);
                        return true;
                    } else {
                        System.err.println("Failed to update game_save " + existingGameSaveIdInUserTable + " (maybe user_id mismatch or save_id gone).");
                        // If update failed, this slot might be orphaned, consider treating as new save
                        // existingGameSaveIdInUserTable = -1; // Force insert below
                    }
                }
            }
            
            // If existingGameSaveIdInUserTable was -1, or became -1 due to update failure, INSERT new game_save record
            sql = "INSERT INTO game_saves (user_id, save_data) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, gameStateData);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    System.err.println("Creating new game_save failed, no rows affected.");
                    return false;
                }

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newGameSaveId = generatedKeys.getInt(1);
                    // Now update the users table to point save<slotNumber> to this newGameSaveId
                    if (updateUserSaveSlot(userId, slotNumber, newGameSaveId)) {
                        System.out.println("New game data inserted (save_id: " + newGameSaveId + ") and linked to user " + userId + ", slot " + slotNumber);
                        return true;
                    } else {
                        System.err.println("Inserted new game_save (" + newGameSaveId + ") but failed to update user's slot " + slotNumber);
                        // CRITICAL: Potentially rollback or delete the orphaned game_save entry here
                        // For simplicity now, just returning error.
                        if (existingGameSaveIdInUserTable != -1) {
                            sql = "DELETE FROM game_saves WHERE save_id = ?;";
                            try (PreparedStatement pstmt2 = connection.prepareStatement(sql)) {
                                pstmt2.setInt(1, existingGameSaveIdInUserTable);
                                pstmt2.execute();
                            } catch (SQLException e) {
                                System.err.println("ERROR DELETING FAULTY SAVE_ID: " + existingGameSaveIdInUserTable);
                                e.printStackTrace();
                            }
                        }

                        return false;
                    }
                } else {
                    // System.err.println("Failed to retrieve generated key for new game_save.");
                    return false;
                }
            }

        } catch (SQLException e) {
            // System.err.println("Error in saveGameToSlot for user " + userId + ", slot " + slotNumber + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }



    //
    //// AUTH
    //
    private String hashPassword(String password) {
        return password;
    }
    
    public boolean registerUser(String username, String password, String email) {
        if (connection == null && !connect()) return false;
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) return false;
        
        String sql = "INSERT INTO users(username, password_hash, email, save1, save2, save3) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            pstmt.setInt(4, -1);
            pstmt.setInt(5, -1);
            pstmt.setInt(6, -1);
            pstmt.executeUpdate();
            // System.out.println("User " + username + " registered successfully.");
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: users.username")) {
                System.err.println("Username '" + username + "' already exists.");
            } else {
                System.err.println("Error registering user: " + e.getMessage());
            }
            return false;
        }
    }
    
    public User loginUser(String username, String password) {
        if (connection == null && !connect()) return null;
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = hashPassword(password);
                String userPassword = rs.getString("password_hash");
                
                if (hashedPassword.equals(userPassword)) {
                    User user = new User(rs.getInt("user_id"), username, null, rs.getString("email"));
                    user.saveData[0] = rs.getInt("save1");
                    user.saveData[1] = rs.getInt("save2");
                    user.saveData[2] = rs.getInt("save3");
                    return user;
                } else {
                    System.err.println("Wrong Password");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error with user: " + e.getMessage());
        }
        return null;
    }
    
    public boolean updateUserSaveSlots(int userId, int slotNumber, int saveId) {
        return false;
    }
    
    public int[] getUserSaveSlots(int userId) {
        if (connection == null && !connect()) return new int[]{-1, -1, -1};
        String sql = "SELECT save1, save2, save3 FROM users WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new int[]{rs.getInt("save1"), rs.getInt("save2"), rs.getInt("save3")};
            }
        } catch (SQLException e) {
            System.err.println("Error with user: " + e.getMessage());
        }

        return new int[]{-1, -1, -1};
    }

    public boolean updateUserSaveSlot(int userId, int slotNumber, int saveId) {
        if (connection == null && !connect()) return false;
        if (slotNumber < 1 || slotNumber > 3) {
            System.err.println("Invalid slot number: " + slotNumber + ". Must be 1, 2, or 3.");
            return false;
        }

        String slotColumn = "save" + slotNumber;
        String sql = "UPDATE users SET " + slotColumn + " = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, saveId); // The game_save.save_id to link
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User " + userId + " slot " + slotNumber + " updated to point to game_save_id: " + saveId);
                return true;
            } else {
                System.err.println("Failed to update slot " + slotNumber + " for user " + userId + " (user not found or value unchanged).");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user save slot " + slotNumber + " for user " + userId + ": " + e.getMessage());
            return false;
        }
    }

    private boolean gameSaveExists(int gameSaveId) {
        if (gameSaveId == -1) return false;
        if (connection == null && !connect()) return false;
        String sql = "SELECT 1 FROM game_saves WHERE save_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, gameSaveId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // True if a row is found
        } catch (SQLException e) {
            System.err.println("Error checking if game_save_id " + gameSaveId + " exists: " + e.getMessage());
            return false;
        }
    }
    
    public String loadGameData(int saveId) {
        if (connection == null && !connect()) {
            System.err.println("Failed to connect (saveGame), cannot save game.");
            return null;
        }
        
        if (saveId == -1) {
            return null;
        }
        
        String sql = "SELECT save_data FROM game_saves WHERE save_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, saveId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("save_data");
            }
            System.err.println("No save data for save_id: " + saveId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Timestamp getCurrentTimeStamp() {
        java.util.Date date = new java.util.Date();
        return new Timestamp(date.getTime());
    }
}