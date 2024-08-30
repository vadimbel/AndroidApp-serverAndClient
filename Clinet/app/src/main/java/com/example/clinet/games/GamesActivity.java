package com.example.clinet.games;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinet.ApiService;
import com.example.clinet.R;
import com.example.clinet.RetrofitClient;
import com.example.clinet.profile.ProfileActivity;
import com.example.clinet.waitingRoom.WaitingRoomActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GamesActivity extends AppCompatActivity {
    private TextView responseTextView;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        responseTextView = findViewById(R.id.responseTextView);
        apiService = RetrofitClient.getApiService();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up the bottom navigation
        setupBottomNavigation(bottomNavigationView);

        // Set the selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
    }

    /**
     * Sends a request to start a Checkers game.
     * The game type is saved in SharedPreferences and the user is directed to the WaitingRoomActivity.
     *
     * @param view The view that triggered this method (i.e., the Checkers button).
     */
    public void sendCheckersRequest(View view) {
        Log.d("GamesActivity", "sendCheckersRequest executed.");
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gameType", "Checkers");
        editor.apply();

        Intent intent = new Intent(GamesActivity.this, WaitingRoomActivity.class);
        startActivity(intent);
    }

    /**
     * Sends a request to start a Tic Tac Toe game.
     * The game type is saved in SharedPreferences and the user is directed to the WaitingRoomActivity.
     *
     * @param view The view that triggered this method (i.e., the Tic Tac Toe button).
     */
    public void sendTicTacToeRequest(View view) {
        Log.d("GamesActivity", "sendTicTacToeRequest executed.");
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gameType", "Tic Tac Toe");
        editor.apply();

        // Start the WaitingRoomActivity
        Intent intent = new Intent(GamesActivity.this, WaitingRoomActivity.class);
        startActivity(intent);
    }

    /**
     * Sets up the bottom navigation bar.
     * Handles navigation item clicks to either navigate to the profile screen or stay on the games screen.
     *
     * @param bottomNavigationView The BottomNavigationView to set up.
     */
    public void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        Log.d("GamesActivity", "setupBottomNavigation executed.");

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Log the item ID
                Log.d("GamesActivity", "Selected item ID: " + itemId);

                // Handle navigation item clicks
                if (itemId == R.id.navigation_home) {
                    Log.d("GamesActivity", "Profile clicked.");
                    startActivity(new Intent(GamesActivity.this, ProfileActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_dashboard) {
                    Log.d("GamesActivity", "Games clicked");
                    return true;
                } else {
                    Log.d("GamesActivity", "setupBottomNavigation failed.");
                    return false;
                }
            }
        });
    }
}

