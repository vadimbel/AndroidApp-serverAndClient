package com.example.server.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class Users {
    @Id
    private String username;

    private String password;

    @Column(name = "logged_in")
    private int loggedIn;

    public Users() {}

    public Users(String username, String password, int loggedIn) {
        this.username = username;
        this.password = password;
        this.loggedIn = loggedIn;
    }

    // Getters and Setters

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



