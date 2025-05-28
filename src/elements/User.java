package elements;

import java.util.Arrays;
import main.DatabaseManager;

public class User {
    public int userId;
    public String username;
    public String password;
    public String email;
    public int[] saveData = new int[3];

    public User(int userId, String username, String password, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void updateSaveSlots(DatabaseManager dbManager) {
        if (dbManager == null || this.userId <= 0) return;
    
        int[] slots = dbManager.getUserSaveSlots(this.userId);
        if (slots != null && slots.length == 3) {
            this.saveData[0] = slots[0];
            this.saveData[1] = slots[1];
            this.saveData[2] = slots[2];
        } else {
            Arrays.fill(this.saveData, -1);
        }
    }

    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", saveSlotsGameIds=" + Arrays.toString(saveData) +
               '}';
    }
}