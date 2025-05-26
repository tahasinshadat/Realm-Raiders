package elements;

public class User {
    public int userId;
    String username;
    String password;
    String email;

    public User(int userId, String username, String password, String email) {

        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
    }
}