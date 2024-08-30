package com.example.server.games;

public class CheckersGameSession implements BaseGameSession {

    private String gameSessionId;
    private char[][] board; // 8x8 for Checkers

    public CheckersGameSession(String gameSessionId) {
        this.gameSessionId = gameSessionId;
        this.board = new char[8][8]; // Initialize the 8x8 board
        initializeBoard();
    }

    // Method to initialize the board with pieces
    private void initializeBoard() {
        // Set up the initial positions of the pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row < 3 && (row + col) % 2 == 1) {
                    board[row][col] = 'R'; // 'r' for red pieces
                } else if (row > 4 && (row + col) % 2 == 1) {
                    board[row][col] = 'B'; // 'b' for black pieces
                } else {
                    board[row][col] = ' '; // Empty square
                }
            }
        }
    }

}
