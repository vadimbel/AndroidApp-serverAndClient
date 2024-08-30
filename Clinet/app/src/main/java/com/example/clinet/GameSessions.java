package com.example.clinet;

import com.google.gson.annotations.SerializedName;

public class GameSessions {

    @SerializedName("id")
    private String id;

    @SerializedName("firstUsername")
    private String firstUsername;

    @SerializedName("secondUsername")
    private String secondUsername;

    @SerializedName("gameType")
    private String gameType;

    public GameSessions() {}

    public GameSessions(String id, String firstUsername, String secondUsername, String gameType) {
        this.id = id;
        this.firstUsername = firstUsername;
        this.secondUsername = secondUsername;
        this.gameType = gameType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstUsername() {
        return firstUsername;
    }

    public void setFirstUsername(String firstUsername) {
        this.firstUsername = firstUsername;
    }

    public String getSecondUsername() {
        return secondUsername;
    }

    public void setSecondUsername(String secondUsername) {
        this.secondUsername = secondUsername;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
}

