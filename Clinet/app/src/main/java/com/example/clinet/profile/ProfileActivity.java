package com.example.clinet.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.clinet.ApiService;
import com.example.clinet.R;
import com.example.clinet.RetrofitClient;
import com.example.clinet.games.GamesActivity;
import com.example.clinet.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageView; // ImageView to display selected image
    private ActivityResultLauncher<PickVisualMediaRequest> launcher; // Launcher for selecting images
    private ApiService apiService; // API service for database connection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        imageView = findViewById(R.id.imageView);
        MaterialButton pickImage = findViewById(R.id.pickImage);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Register the launcher for image selection
        launcher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                if (o == null) {
                    Toast.makeText(ProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                } else {
                    Glide.with(getApplicationContext()).load(o).into(imageView);
                }
            }
        });

        // Initialize the API service
        apiService = RetrofitClient.getApiService();

        // Set up click listener for the pick image button
        pickImage.setOnClickListener(this::pickImage);

        // Set up the bottom navigation
        setupBottomNavigation(bottomNavigationView);

        // Set the selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    /**
     * Launches the image picker to allow the user to select an image.
     * Once an image is selected, it is displayed in the ImageView.
     *
     * @param view The view that triggered this method (i.e., the pick image button).
     */
    public void pickImage(View view) {
        Log.d("ProfileActivity", "pickImage method executed.");
        launcher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    /**
     * Deletes the user account.
     * Sends a request to delete the user account associated with the username stored in SharedPreferences.
     * If successful, clears the SharedPreferences and navigates back to the LoginActivity.
     *
     * @param view The view that triggered this method (i.e., the delete button).
     */
    public void delete(View view) {
        Log.d("ProfileActivity", "delete method executed.");

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // If the username is not empty, proceed with the delete operation
        if (!username.isEmpty()) {
            Call<Void> call = apiService.delete(username);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Clear SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        // Navigate back to LoginActivity
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(ProfileActivity.this, "user deleted successfully.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Log.d("ProfileActivity", "delete method failed.");
                        Toast.makeText(ProfileActivity.this, "Failed to delete user", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Logs out the user.
     * Sends a request to log out the user associated with the username stored in SharedPreferences.
     * If successful, clears the SharedPreferences and navigates back to the LoginActivity.
     *
     * @param view The view that triggered this method (i.e., the logout button).
     */
    public void logout(View view) {
        Log.d("ProfileActivity", "logout method executed.");

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // If the username is not empty, proceed with the logout operation
        if (!username.isEmpty()) {
            Call<Void> call = apiService.logout(username);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Clear SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        // Navigate back to LoginActivity
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Log.d("ProfileActivity", "logout method executed successfully.");
                        finish();
                    } else {
                        Log.d("ProfileActivity", "logout method failed.");
                        Toast.makeText(ProfileActivity.this, "Failed to log out", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sets up the bottom navigation bar.
     * Handles navigation item clicks to either navigate to the profile screen or the games screen.
     *
     * @param bottomNavigationView The BottomNavigationView to set up.
     */
    public void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        Log.d("ProfileActivity", "setupBottomNavigation executed.");

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Log the item ID
                Log.d("ProfileActivity", "Selected item ID: " + itemId);

                // Handle navigation item clicks
                if (itemId == R.id.navigation_home) {
                    Log.d("ProfileActivity", "Profile clicked.");
                    // Navigate to Profile screen (already here, no action needed)
                    return true;

                } else if (itemId == R.id.navigation_dashboard) {
                    Log.d("ProfileActivity", "Games clicked");
                    // Navigate to Games screen
                    startActivity(new Intent(ProfileActivity.this, GamesActivity.class));
                    return true;

                } else {
                    Log.d("ProfileActivity", "setupBottomNavigation failed.");
                    return false;
                }
            }
        });
    }
}

