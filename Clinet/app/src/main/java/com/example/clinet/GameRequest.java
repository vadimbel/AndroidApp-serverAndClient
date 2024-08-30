package com.example.clinet;

import com.google.gson.annotations.SerializedName;

public class GameRequest {
    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("gameType")
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



