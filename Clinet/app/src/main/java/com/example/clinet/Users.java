package com.example.clinet;
import com.google.gson.annotations.SerializedName;

public class Users {
    @SerializedName("username")
    private String username;
    private String password;

    @SerializedName("logged_in")
    private int loggedIn;

    public Users() {}

    public Users(String username, String password, int loggedIn) {
        this.username = username;
        this.password = password;
        this.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(int loggedIn) {
        this.loggedIn = loggedIn;
    }
}




