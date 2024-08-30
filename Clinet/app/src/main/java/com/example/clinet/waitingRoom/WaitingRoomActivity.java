package com.example.clinet.waitingRoom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clinet.ApiService;
import com.example.clinet.GameRequest;
import com.example.clinet.GameSessions;
import com.example.clinet.R;
import com.example.clinet.RetrofitClient;
import com.example.clinet.checkers.CheckersActivity;
import com.example.clinet.games.GamesActivity;
import com.example.clinet.ticTacToe.TicTacToe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitingRoomActivity extends AppCompatActivity {

    private static final long POLLING_INTERVAL = 5000; // Interval for polling in milliseconds
    private Button cancelButton; // Button to cancel the game request
    private TextView waitingText; // TextView to display waiting message
    private ApiService apiService; // API service for database connection
    private Handler handler = new Handler(); // Handler for scheduling tasks
    private Runnable pollingRunnable; // Runnable for polling game session status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        // Initialize UI components
        cancelButton = findViewById(R.id.cancel_button);
        waitingText = findViewById(R.id.waiting_text);

        // Initialize the API service
        apiService = RetrofitClient.getApiService();

        // Retrieve the username and game type from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        String gameType = sharedPreferences.getString("gameType", null);

        // If both username and gameType are available, send game request and start polling
        if (username != null && gameType != null) {
            Log.d("WaitingRoomActivity", "Sending game request by client with username: " + username + ", game type: " + gameType);
            sendGameRequest(username, gameType);
            Log.d("WaitingRoomActivity", "Request has been sent successfully.");
            startPollingForMatch(username, gameType);

            // else - return back to games selection page
        } else {
            Log.d("WaitingRoomActivity", "Failed to send game request.");
            Toast.makeText(WaitingRoomActivity.this, "ERROR occurred", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(WaitingRoomActivity.this, GamesActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Sends a game request to the server.
     *
     * @param username The username of the player requesting a game.
     * @param gameType The type of game requested (e.g., "Checkers", "Tic Tac Toe").
     */
    private void sendGameRequest(String username, String gameType) {
        Log.d("WaitingRoomActivity", "sendGameRequest - executed");
        GameRequest gameRequest = new GameRequest(username, gameType);
        apiService.createGameRequest(gameRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("WaitingRoomActivity", "Game request sent: " + gameType);
                    Toast.makeText(WaitingRoomActivity.this, "Game request sent: " + gameType, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("WaitingRoomActivity", "Failed to send game request");
                    Toast.makeText(WaitingRoomActivity.this, "Failed to send game request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("WaitingRoomActivity", "Error: " + t.getMessage());
                Toast.makeText(WaitingRoomActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cancels the game request.
     * This method is triggered when the user clicks the cancel button.
     *
     * @param view The view that triggered this method (i.e., the cancel button).
     */
    public void cancelGame(View view) {
        Log.d("WaitingRoomActivity", "cancelGame executed.");

        // Retrieve the username and game type from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        String gameType = sharedPreferences.getString("gameType", null);

        // If both username and gameType are available, send request to delete game request
        if (username != null && gameType != null) {
            Log.d("WaitingRoomActivity", "username: " + username + " | gameType: " + gameType);
            String id = username + "_" + gameType;
            apiService.deleteGameRequest(id).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("WaitingRoomActivity", "Game request canceled");

                        Toast.makeText(WaitingRoomActivity.this, "Game request canceled", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(WaitingRoomActivity.this, GamesActivity.class));
                        finish();
                    } else {
                        Log.d("WaitingRoomActivity", "Failed to cancel game request");
                        Toast.makeText(WaitingRoomActivity.this, "Failed to cancel game request", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("WaitingRoomActivity", "Error: " + t.getMessage());
                    Toast.makeText(WaitingRoomActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            Log.d("WaitingRoomActivity", "Failed to retrieve game request details");
            Toast.makeText(this, "Failed to retrieve game request details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Starts polling the server to check if a game match has been found.
     *
     * @param username The username of the player waiting for a match.
     * @param gameType The type of game the player is waiting to play.
     */
    private void startPollingForMatch(String username, String gameType) {
        Log.d("WaitingRoomActivity", "startPollingForMatch");
        // Run to find game session
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                apiService.checkGameSession(username).enqueue(new Callback<GameSessions>() {
                    @Override
                    public void onResponse(Call<GameSessions> call, Response<GameSessions> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // If game session found (status: ok 200 returned) -> move to game page
                            Log.d("WaitingRoomActivity", "Match found for username: " + username);
                            navigateToGamePage(response.body(), gameType);

                        } else {
                            // Game session not found -> run thread again every 5 seconds
                            Log.d("WaitingRoomActivity", "No match found for username: " + username);
                            handler.postDelayed(pollingRunnable, POLLING_INTERVAL);
                        }
                    }

                    @Override
                    public void onFailure(Call<GameSessions> call, Throwable t) {
                        Log.d("WaitingRoomActivity", "Error checking game session: " + t.getMessage());
                        handler.postDelayed(pollingRunnable, POLLING_INTERVAL);
                    }
                });
            }
        };
        handler.post(pollingRunnable);
    }

    /**
     * Navigates to the appropriate game page (Tic Tac Toe or Checkers) when a match is found.
     *
     * @param gameSession The game session object containing details about the match.
     * @param gameType The type of game to navigate to.
     */
    private void navigateToGamePage(GameSessions gameSession, String gameType) {
        if (gameType.equals("Tic Tac Toe")) {
            Intent intent = new Intent(WaitingRoomActivity.this, TicTacToe.class);
            intent.putExtra("gameSessionId", gameSession.getId());
            intent.putExtra("firstUsername", gameSession.getFirstUsername());
            intent.putExtra("secondUsername", gameSession.getSecondUsername());
            intent.putExtra("gameType", gameSession.getGameType());
            startActivity(intent);
        } else {
            // not relevant for now - checkers
            Intent intent = new Intent(WaitingRoomActivity.this, CheckersActivity.class);
            intent.putExtra("gameSessionId", gameSession.getId());
            intent.putExtra("firstUsername", gameSession.getFirstUsername());
            intent.putExtra("secondUsername", gameSession.getSecondUsername());
            intent.putExtra("gameType", gameSession.getGameType());
            startActivity(intent);
        }

        finish();
    }
}


