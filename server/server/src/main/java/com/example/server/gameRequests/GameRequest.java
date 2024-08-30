package com.example.server.gameRequests;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "game_requests")
public class GameRequest {
    @Id
    private String id;

    private String username;
    private String gameType;

    // Default constructor
    public GameRequest() {}

    // Constructor
    public GameRequest(String username, String gameType) {
        this.username = username;
        this.gameType = gameType;
        this.id = username + "_" + gameType;  // Concatenate username and gameType to create a unique id
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
}


