package main;

import java.io.File;
import java.io.IOException;
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

    private boolean execStatement(String statementString) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(statementString);
            System.out.println(statementString + " executed");
            return true;
        } catch (SQLException e) {
            // System.err.println("Error executing statement: " + statementString + " " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Timestamp getCurrentTimeStamp() {
        java.util.Date date = new java.util.Date();
        return new Timestamp(date.getTime());
    }

    // public boolean writeToDatabase(String userId, String saveId, String table, String gameStateData, Timestamp timeStamp) {
        
    //     // return execStatment("INSERT INTO " + table
    //     //                     + "VALUES (" + userId + ", " + gameStateData + ", " + );
    // }

    
    // public boolean readFromDatabase(String userId, String saveId, String table) {
        
    // }
    
    // Initialize necessary tables
    public void initializeTables() {

        System.out.println("Initializing tables: ");
        
        // users table:
        System.out.println("Initializing User Table: ");
        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL UNIQUE,"
                + "password_hash TEXT NOT NULL,"
                + "email TEXT"
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

    /**
     * Saves the current game state for a given user.
     * @param userId The ID of the user saving the game.
     * @param saveName The name for this save slot.
     * @param gameStateData A string (e.g., JSON) or byte array representing the game state.
     * @return true if saving is successful, false otherwise.
     */
    // public boolean saveGame(int userId, String gameStateData) {
        
    //     // Implementation to insert or update a record in the 'game_saves' table.
    //     // Handle cases where saveName for a user might already exist (overwrite or create new).

    //     return execStatement("INSERT INTO game_saves"
    //                     + "VALUES (" + userId + ", " + gameStateData + ", " + getCurrentTimeStamp());
    // }

    private int findSaveSlotIdSecure(int userId) {
        if (connection == null && !connect()) {
            System.err.println("Failed to connect (findSaveSlotIdSecure).");
            return -1;
        }
        String sql = "SELECT save_id FROM game_saves WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("save_id");
            }
        } catch (SQLException e) {
            System.err.println("Error finding save slot (secure) for user_id=" + userId + ": " + e.getMessage());
            // e.printStackTrace();
        }
        return -1; // Not found
    }

    public boolean saveGame(int userId, String gameStateData) {
        if (connection == null && !connect()) {
             System.err.println("Failed to connect (saveGame), cannot save game.");
            return false;
        }

        // Basic escaping for single quotes in string literals for SQLite.
        // THIS IS NOT A SUBSTITUTE FOR PREPAREDSTATEMENTS FOR SECURITY.
        // String escapedGameStateData = gameStateData.replace("'", "''");
        
        // Check if a save slot already exists.
        // For this check, using a PreparedStatement is still much safer and cleaner,
        // even if the subsequent write uses execStatement.

        String currentTimeStampString = getCurrentTimeStamp().toString();
        int existingSaveId = findSaveSlotIdSecure(userId);

        if (existingSaveId != -1) {
            // Update existing save slot
            String sql = "UPDATE game_saves SET save_data = '" + gameStateData +
                  "', last_saved_timestamp = CURRENT_TIMESTAMP WHERE save_id = " + existingSaveId + ";";
            return execStatement(sql);
        } else {
            String sql = "INSERT INTO game_saves (user_id, save_data, last_saved_timestamp) VALUES (" +
                  userId + ", '" + gameStateData + "', '" + currentTimeStampString + "');";
            return execStatement(sql);
        }
    }

    /**
     * Registers a new user in the database.
     * @param username The username for the new user.
     * @param plainPassword The plain text password for the new user.
     * @param email The email for the new user (optional).
     * @return true if registration is successful, false otherwise (e.g., username exists).
     */
    public boolean registerUser(String username, String plainPassword, String email) {
        // 1. Hash the plainPassword (e.g., using BCrypt)
        // 2. Insert the new user into the 'users' table

        return false;
    }

    /**
     * Attempts to log in a user.
     * @param username The username of the user.
     * @param plainPassword The plain text password of the user.
     * @return The user_id if login is successful, -1 or throw exception otherwise.
     */
    public int loginUser(String username, String plainPassword) {
        // 1. Retrieve the user from the 'users' table by username.
        // 2. If user exists, hash the provided plainPassword and compare with stored hash.
        // 3. Return user_id on success.
        return -1;
    }

    // Optional: Method to get more user details
    // public User getUserDetails(int userId) {
    //     // Implementation
    //     return null;
    // }


    /**
     * Retrieves a list of save game summaries for a given user.
     * @param userId The ID of the user whose save slots are to be retrieved.
     * @return A list of GameSaveSummary objects, or an empty list if none.
     */
    // public List<GameSaveSummary> getSaveSlots(int userId) {
    //     // Implementation to query 'game_saves' table for the user.
    //     List<GameSaveSummary> saveSlots = new ArrayList<>();
    //     return saveSlots;
    // }

    /**
     * Loads a specific game save's data.
     * @param saveId The ID of the game save to load.
     * @return A string (e.g., JSON) or byte array representing the game state, or null if not found.
     */
    public String loadGameData(int saveId) {
        // Implementation to retrieve 'save_data' from 'game_saves' table by save_id.
        return null;
    }

    /**
     * Deletes a specific game save.
     * @param saveId The ID of the game save to delete.
     * @return true if deletion is successful, false otherwise.
     */
    public boolean deleteGameSave(int saveId) {
        // Implementation to delete a record from 'game_saves' table.
        return false;
    }

    // Helper method for password hashing (example, you'd use a proper library)
    // private String hashPassword(String plainPassword) {
    //     // Use a strong hashing library like BCrypt or Argon2
    //     // Placeholder:
    //     return "hashed_" + plainPassword;
    // }

    // Helper method for checking passwords (example)
    // private boolean checkPassword(String plainPassword, String hashedPassword) {
    //     // Use the same hashing library to compare
    //     // Placeholder:
    //     return ("hashed_" + plainPassword).equals(hashedPassword);
    // }
}