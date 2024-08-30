package com.example.clinet.ticTacToe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clinet.R;
import com.example.clinet.games.GamesActivity;
import com.example.clinet.websocket.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

public class TicTacToe extends AppCompatActivity implements WebSocketClient.OnWebSocketMessageListener {

    private TextView player1;
    private TextView player2;
    private TextView playerTurn;

    private TextView btnZero;
    private TextView btnOne;
    private TextView btnTwo;
    private TextView btnThree;
    private TextView btnFour;
    private TextView btnFive;
    private TextView btnSix;
    private TextView btnSeven;
    private TextView btnEight;
    private WebSocketClient webSocketClient;
    private static final String TAG = "TicTacToe";
    private String mark = "";
    private String playerState;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        // Initialize UI components
        player1 = findViewById(R.id.firstUsernameTextView);
        player2 = findViewById(R.id.secondUsernameTextView);
        playerTurn = findViewById(R.id.playersTurn);

        // Initialize the buttons representing the game board
        btnZero = findViewById(R.id.cell_00);
        btnOne = findViewById(R.id.cell_01);
        btnTwo = findViewById(R.id.cell_02);
        btnThree = findViewById(R.id.cell_10);
        btnFour = findViewById(R.id.cell_11);
        btnFive = findViewById(R.id.cell_12);
        btnSix = findViewById(R.id.cell_20);
        btnSeven = findViewById(R.id.cell_21);
        btnEight = findViewById(R.id.cell_22);

        // Retrieve game session details from the intent
        String gameSessionId = getIntent().getStringExtra("gameSessionId");
        String firstUsername = getIntent().getStringExtra("firstUsername");
        String secondUsername = getIntent().getStringExtra("secondUsername");
        String gameType = getIntent().getStringExtra("gameType");

        // Set the player labels
        if (firstUsername != null) {
            player1.setText(firstUsername);
        }
        if (secondUsername != null) {
            player2.setText(secondUsername);
        }

        // Initialize the WebSocket client and set the message listener
        webSocketClient = new WebSocketClient();
        webSocketClient.setOnWebSocketMessageListener(this);

        // Connect to the server using WebSocket
        String url = "ws://10.0.2.2:8080/ws?gameSessionId=" + gameSessionId + "&username=" + firstUsername + "&gameType=" + gameType;
        webSocketClient.connect(url);
    }

    /**
     * Handles messages received from the server via WebSocket and performs actions based on the received message.
     *
     * @param message The message received from the server in JSON format.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onMessageReceived(String message) {
        Log.d(TAG, "onMessageReceived executed.");
        runOnUiThread(() -> {
            try {
                JSONObject json = new JSONObject(message);
                String messageType = json.getString("messageType");  // Get messageType from server

                // Execute different actions based on the message type received from the server
                if ("startGame".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - startGame.");

                    // Retrieve the player's mark and state, then update the UI accordingly
                    String mark = json.getString("mark");
                    String state = json.getString("state");
                    this.mark = mark;
                    this.playerState = state;
                    setActiveState();  // Modify 'playerTurn' label on screen and enable/disable the game board

                } else if ("switchTurn".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - switchTurn.");

                    // Update the game state after a move has been made
                    String newPlayersState = json.getString("playersNewState");
                    String value = json.getString("value");
                    String mark = json.getString("mark");
                    updateGameBoard(value, mark);  // Update the game board after a move
                    this.playerState = newPlayersState;
                    setActiveState();

                } else if ("playerWon".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - playerWon.");
                    String first = json.getString("first");
                    String second = json.getString("second");
                    String third = json.getString("third");
                    modifyEndGameBoard(first, second, third, "win", this.mark);

                } else if ("playerLose".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - playerLose.");
                    String first = json.getString("first");
                    String second = json.getString("second");
                    String third = json.getString("third");
                    modifyEndGameBoard(first, second, third, "lose", "O");

                } else if ("endGame".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - endGame.");
                    webSocketClient.close();  // Close WebSocket connection
                    // Navigate back to the games selection screen
                    Intent intent = new Intent(this, GamesActivity.class);
                    startActivity(intent);
                    finish();  // Optionally finish the current activity if you don't want to return to it

                } else if ("playerForfeit".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - playerForfeit");
                    String winOrLose = json.getString("winOrLose");
                    modifyEndGameForfeit(winOrLose);
                    webSocketClient.close();
                    Intent intent = new Intent(this, GamesActivity.class);
                    startActivity(intent);
                    finish();  // Optionally finish the current activity if you don't want to return to it

                } else if ("connectionFail".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - connectionFail");
                    Toast.makeText(TicTacToe.this, "Connection failed", Toast.LENGTH_LONG).show();
                    webSocketClient.close();
                    Intent intent = new Intent(this, GamesActivity.class);
                    startActivity(intent);
                    finish();  // Optionally finish the current activity if you don't want to return to it

                } else if ("playerDraw".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - playerDraw");
                    String value = json.getString("value");
                    String mark = json.getString("mark");
                    updateGameBoard(value, mark);
                    Toast.makeText(TicTacToe.this, "draw", Toast.LENGTH_LONG).show();

                } else if ("invalidMove".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - invalidMove");
                    Toast.makeText(TicTacToe.this, "move fail", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse message: " + e.getMessage());
            }
        });
    }

    /**
     * Sets the player's active state, enabling or disabling the UI based on the current turn.
     */
    @SuppressLint("SetTextI18n")
    private void setActiveState() {
        if (this.playerState.equals("active")) {
            enableUI();
            playerTurn.setText("YOUR TURN");
        } else {
            disableUI();
            playerTurn.setText("OPPONENT TURN");
        }
    }

    /**
     * Enables the UI elements representing the game board, allowing the player to interact with them.
     */
    private void enableUI() {
        Log.d(TAG, "enableUI");
        // Enable the UI elements
        btnZero.setClickable(true);
        btnOne.setClickable(true);
        btnTwo.setClickable(true);
        btnThree.setClickable(true);
        btnFour.setClickable(true);
        btnFive.setClickable(true);
        btnSix.setClickable(true);
        btnSeven.setClickable(true);
        btnEight.setClickable(true);
    }

    /**
     * Disables the UI elements representing the game board, preventing the player from interacting with them.
     */
    private void disableUI() {
        Log.d(TAG, "disableUI");
        // Disable the UI elements
        btnZero.setClickable(false);
        btnOne.setClickable(false);
        btnTwo.setClickable(false);
        btnThree.setClickable(false);
        btnFour.setClickable(false);
        btnFive.setClickable(false);
        btnSix.setClickable(false);
        btnSeven.setClickable(false);
        btnEight.setClickable(false);
    }

    /**
     * Updates the game board by setting the specified mark on the given cell.
     *
     * @param value The cell identifier (e.g., "zero", "one", etc.).
     * @param mark  The player's mark (e.g., "X" or "O").
     */
    public void updateGameBoard(String value, String mark) {
        switch (value) {
            case "zero":
                btnZero.setText(mark);
                break;
            case "one":
                btnOne.setText(mark);
                break;
            case "two":
                btnTwo.setText(mark);
                break;
            case "three":
                btnThree.setText(mark);
                break;
            case "four":
                btnFour.setText(mark);
                break;
            case "five":
                btnFive.setText(mark);
                break;
            case "six":
                btnSix.setText(mark);
                break;
            case "seven":
                btnSeven.setText(mark);
                break;
            case "eight":
                btnEight.setText(mark);
                break;
        }
    }

    /**
     * Modifies the game board to indicate the end of the game, highlighting the winning or losing cells.
     *
     * @param first  The first cell in the winning or losing combination.
     * @param second The second cell in the winning or losing combination.
     * @param third  The third cell in the winning or losing combination.
     * @param type   The result type ("win" or "lose").
     * @param mark   The player's mark (e.g., "X" or "O").
     */
    public void modifyEndGameBoard(String first, String second, String third, String type, String mark) {
        int color;
        if (type.equals("win")) {
            color = Color.GREEN;  // Change to your desired color for win
            playerTurn.setText("YOU WON !");
        } else {
            color = Color.RED;  // Change to your desired color for lose
            playerTurn.setText("you lose :(");
        }

        changeBackgroundColor(first, color, mark);
        changeBackgroundColor(second, color, mark);
        changeBackgroundColor(third, color, mark);
    }

    /**
     * Modifies the game board when a player forfeits, highlighting the result in red or green.
     *
     * @param winOrLose Indicates whether the player won or lost ("win" or "lose").
     */
    public void modifyEndGameForfeit(String winOrLose) {
        // When a player forfeits
        int color;
        if (winOrLose.equals("win")) {
            color = Color.GREEN;
            playerTurn.setText("YOU WON !");
        } else {
            color = Color.RED;
            playerTurn.setText("you lose :(");
        }

        changeBackgroundBoard(color);
    }

    /**
     * Changes the background color of all cells on the game board to indicate a forfeit.
     *
     * @param color The color to set for all cells.
     */
    private void changeBackgroundBoard(int color) {
        // When a player forfeits, color all cells in red or green
        btnZero.setBackgroundColor(color);
        btnOne.setBackgroundColor(color);
        btnTwo.setBackgroundColor(color);
        btnThree.setBackgroundColor(color);
        btnFour.setBackgroundColor(color);
        btnFive.setBackgroundColor(color);
        btnSix.setBackgroundColor(color);
        btnSeven.setBackgroundColor(color);
        btnEight.setBackgroundColor(color);
    }

    /**
     * Changes the background color of specific cells on the game board to indicate a win or loss.
     *
     * @param position The position identifier (e.g., "zero", "one", etc.).
     * @param color    The color to set for the cell.
     * @param mark     The player's mark (e.g., "X" or "O").
     */
    private void changeBackgroundColor(String position, int color, String mark) {
        switch (position) {
            case "zero":
                btnZero.setBackgroundColor(color);
                btnZero.setText(mark);
                break;
            case "one":
                btnOne.setBackgroundColor(color);
                btnOne.setText(mark);
                break;
            case "two":
                btnTwo.setBackgroundColor(color);
                btnTwo.setText(mark);
                break;
            case "three":
                btnThree.setBackgroundColor(color);
                btnThree.setText(mark);
                break;
            case "four":
                btnFour.setBackgroundColor(color);
                btnFour.setText(mark);
                break;
            case "five":
                btnFive.setBackgroundColor(color);
                btnFive.setText(mark);
                break;
            case "six":
                btnSix.setBackgroundColor(color);
                btnSix.setText(mark);
                break;
            case "seven":
                btnSeven.setBackgroundColor(color);
                btnSeven.setText(mark);
                break;
            case "eight":
                btnEight.setBackgroundColor(color);
                btnEight.setText(mark);
                break;
            default:
                break;
        }
    }

    /**
     * Handles the click event for the "zero" cell.
     * Checks if the cell is empty and sends a move message if it is.
     *
     * @param view The view that triggered the click event.
     */
    public void clickZero(View view) {
        Log.d(TAG, "clickZero");
        // Get the TextView
        TextView textView = (TextView) view;
        // Check the text value of the TextView
        String textValue = textView.getText().toString();

        if (textValue.isEmpty()) {
            // If the TextView is empty, proceed with the move
            webSocketClient.sendMoveMessage("Tic Tac Toe", "move", "zero", this.mark, this.playerState);
        } else {
            // If the TextView is not empty, the move is invalid
            Log.d(TAG, "Invalid move, cell is already occupied");
            Toast.makeText(TicTacToe.this, "Invalid move, cell is already occupied.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the click event for the "one" cell.
     * Checks if the cell is empty and sends a move message if it is.
     *
     * @param view The view that triggered the click event.
     */
    public void clickOne(View view) {
        Log.d(TAG, "clickOne");
        // Get the TextView
        TextView textView = (TextView) view;
        // Check the text value of the TextView
        String textValue = textView.getText().toString();

        if (textValue.isEmpty()) {
            // If the TextView is empty, proceed with the move
            webSocketClient.sendMoveMessage("Tic Tac Toe", "move", "one", this.mark, this.playerState);
        } else {
            // If the TextView is not empty, the move is invalid
            Log.d(TAG, "Invalid move, cell is already occupied");
            Toast.makeText(TicTacToe.this, "Invalid move, cell is already occupied.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the click event for the "two" cell.
     * Checks if the cell is empty and sends a move message if it is.
     *
     * @param view The view that triggered the click event.
     */
    public void clickTwo(View view) {
        Log.d(TAG, "clickTwo");
        // Get the TextView
        TextView textView = (TextView) view;
        // Check the text value of the TextView
        String textValue = textView.getText().toString();

        if (textValue.isEmpty()) {
            // If the TextView is empty, proceed with the move
            webSocketClient.sendMoveMessage("Tic Tac Toe", "move", "two", this.mark, this.playerState);
        } else {
            // If the TextView is not empty, the move is invalid
            Log.d(TAG, "Invalid move, cell is already occupied");
            Toast.makeText(TicTacToe.this, "Invalid move, cell is already occupied.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the click event for the "three" cell.
     * Checks if the cell is empty and sends a move message if it is.
     *
     * @param view The view that triggered the click event.
     */
    public void clickThree(View view) {
        Log.d(TAG, "clickThree");
        // Get the TextView
        TextView textView = (TextView) view;
        // Check the text value of the TextView
        String textValue = textView.getText().toString();

        if (textValue.isEmpty()) {
            // If the TextView is empty, proceed with the move
            webSocketClient.sendMoveMessage("Tic Tac Toe", "move", "three", this.mark, this.playerState);
        } else {
            // If the TextView is not empty, the move is invalid
            Log.d(TAG, "Invalid move, cell is already occupied");
            Toast.makeText(TicTacToe.this, "Invalid move, cell is already occupied.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the click event for the "four" cell.
     * Checks if the cell is empty and sends a move message if it is.
     *
     * @param view The view that triggered the click event.
     */
    public void clickFour(View view) {
        Log.d(TAG, "clickFour");
        // Get the TextView
        TextView textView = (TextView) view;
        // Check the text value of the TextView
        String textValue = textView.getText().toString();

        if (textValue.isEmpty()) {
            // If the TextView is empty, proceed with the move
            webSocketClient.sendMoveMessage("Tic Tac Toe", "move", "four", this.mark, this.playerState);
        } else {
            // If the TextView is not empty, the move is invalid
            Log.d(TAG, "Invalid move, cell is already occupied");
            Toast.makeText(TicTacToe.this, "Invalid move, cell is already occupied.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the click event for the "five" cell.
     * Checks if the cell is empty and sends a move message if it is.
     *
     * @param view The view that triggered the click event.
     */
    public void clickFive(View view) {
        Log.d(TAG, "clickFive");
        // Get the TextView
        TextView textView = (TextView) view;
        // Check the text value of the TextView
        String textValue = textView.getText().toString();

        if (textValue.isEmpty()) {
            // If the TextView is empty, proceed with the move
            webSocketClient.sendMoveMessage("Tic Tac Toe", "move", "five", this.mark, this.playerState);
        } else {
            // If the TextView is not empty, the move is invalid
            Log.d(TAG, "Invalid move, cell is already occupied");
            Toast.makeText(TicTacToe.this, "Invalid move, cell is already occupied.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the click event for the "six" cell.
     * Checks if the cell is empty and sends a move message if it is.
     *
     * @param view The view that triggered the click event.
     */
    public void clickSix(View view) {
        Log.d(TAG, "clickSix");
        // Get the TextView
        TextView textView = (TextView) view;
        // Check the text value of the TextView
        String textValue = textView.getText().toString();

        if (textValue.isEmpty()) {
            // If the TextView is empty, proceed with the move
            webSocketClient.sendMoveMessage("Tic Tac Toe", "move", "six", this.mark, this.playerState);
        } else {
            // If the TextView is not empty, the move is invalid
            Log.d(TAG, "Invalid move, cell is already occupied");
            Toast.makeText(TicTacToe.this, "Invalid move, cell is already occupied.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the click event for the "seven" cell.
     * Checks if the cell is empty and sends a move message if it is.
     *
     * @param view The view that triggered the click event.
     */
    public void clickSeven(View view) {
        Log.d(TAG, "clickSeven");
        // Get the TextView
        TextView textView = (TextView) view;
        // Check the text value of the TextView
        String textValue = textView.getText().toString();

        if (textValue.isEmpty()) {
            // If the TextView is empty, proceed with the move
            webSocketClient.sendMoveMessage("Tic Tac Toe", "move", "seven", this.mark, this.playerState);
        } else {
            // If the TextView is not empty, the move is invalid
            Log.d(TAG, "Invalid move, cell is already occupied");
            Toast.makeText(TicTacToe.this, "Invalid move, cell is already occupied.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the click event for the "eight" cell.
     * Checks if the cell is empty and sends a move message if it is.
     *
     * @param view The view that triggered the click event.
     */
    public void clickEight(View view) {
        Log.d(TAG, "clickEight");
        // Get the TextView
        TextView textView = (TextView) view;
        // Check the text value of the TextView
        String textValue = textView.getText().toString();

        if (textValue.isEmpty()) {
            // If the TextView is empty, proceed with the move
            webSocketClient.sendMoveMessage("Tic Tac Toe", "move", "eight", this.mark, this.playerState);
        } else {
            // If the TextView is not empty, the move is invalid
            Log.d(TAG, "Invalid move, cell is already occupied");
            Toast.makeText(TicTacToe.this, "Invalid move, cell is already occupied.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the player's decision to forfeit the game by sending a forfeit message to the server.
     *
     * @param view The view that triggered the forfeit event.
     */
    public void forfeit(View view) {
        webSocketClient.sendMessage("Tic Tac Toe", "forfeit", "forfeit");
    }

}

