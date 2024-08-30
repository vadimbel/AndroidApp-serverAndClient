package com.example.server.games;

import com.example.server.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicTacToeGameSession implements BaseGameSession {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    private String gameSessionId;
    private char[][] board; // 3x3 for TicTacToe

    public TicTacToeGameSession(String gameSessionId) {
        this.gameSessionId = gameSessionId;
        this.board = new char[3][3]; // Initialize the 3x3 board
        initializeBoard();
    }

    // Method to initialize the board with empty characters
    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' '; // Using space as the empty character
            }
        }
    }

    public boolean validatePlayersMove(String value, char mark) {
        int row = -1;
        int col = -1;

        switch (value) {
            case "zero":
                row = 0;
                col = 0;
                break;
            case "one":
                row = 0;
                col = 1;
                break;
            case "two":
                row = 0;
                col = 2;
                break;
            case "three":
                row = 1;
                col = 0;
                break;
            case "four":
                row = 1;
                col = 1;
                break;
            case "five":
                row = 1;
                col = 2;
                break;
            case "six":
                row = 2;
                col = 0;
                break;
            case "seven":
                row = 2;
                col = 1;
                break;
            case "eight":
                row = 2;
                col = 2;
                break;
            default:
                return false;
        }

        // Check if the move is valid by checking if the cell is empty
        if (this.board[row][col] == ' ') {
            this.board[row][col] = mark;
            return true;
        } else {
            return false;
        }
    }

    // Method to check if the game is over
    public boolean isWinner() {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return true;
            }
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return true;
        }
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return true;
        }

        return false;
    }

    public boolean isDraw() {
        // Check if all cells are filled
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == ' ')
                    return false; // There's still an empty cell

        return true;
    }

    // Method to get the winning combination
    public String[] getWinningCombination() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return new String[]{"zero", "one", "two"};
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                switch (i) {
                    case 0:
                        return new String[]{"zero", "three", "six"};
                    case 1:
                        return new String[]{"one", "four", "seven"};
                    case 2:
                        return new String[]{"two", "five", "eight"};
                }
            }
        }

        // Check diagonals
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return new String[]{"zero", "four", "eight"};
        }
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return new String[]{"two", "four", "six"};
        }

        return null; // No winning combination found
    }

}
