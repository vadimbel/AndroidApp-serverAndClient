package com.example.server.gameSessions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "game_sessions")
public class GameSessions {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "first_username")
    private String firstUsername;

    @Column(name = "second_username")
    private String secondUsername;

    @Column(name = "game_type")
    private String gameType;

    // Default constructor
    public GameSessions() {}

    public GameSessions(String gameId, String firstUsername, String secondUsername, String gameType) {
        this.id = gameId;
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

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getSecondUsername() {
        return secondUsername;
    }

    public void setSecondUsername(String secondUsername) {
        this.secondUsername = secondUsername;
    }
}

