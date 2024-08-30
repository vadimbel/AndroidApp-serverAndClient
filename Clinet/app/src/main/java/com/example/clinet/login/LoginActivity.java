package com.example.clinet.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clinet.ApiService;
import com.example.clinet.R;
import com.example.clinet.RetrofitClient;
import com.example.clinet.Users;
import com.example.clinet.profile.ProfileActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ApiService apiService; // API service for database connection
    private EditText edUser; // EditText for entering the username
    private EditText edPass; // EditText for entering the password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        edUser = findViewById(R.id.edUser);
        edPass = findViewById(R.id.edPass);

        // Initialize the API service
        apiService = RetrofitClient.getApiService();
    }

    /**
     * Handles the login button click.
     * Sends a login request to the API using the entered username and password.
     * If the login is successful, stores user information in SharedPreferences and navigates to the ProfileActivity.
     * If the login fails, displays an appropriate error message based on the HTTP status code.
     *
     * @param view The view that triggered this method (i.e., the login button).
     */
    public void login(View view) {
        Log.d("LoginActivity", "login method executed");

        // Get entered username and password from EditText fields
        String enteredUserName = edUser.getText().toString();
        String enteredPassword = edPass.getText().toString();

        // Call the login API method
        Call<Users> call = apiService.login(enteredUserName, enteredPassword);
        call.enqueue(new Callback<Users>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Users user = response.body();

                    // Save user information in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", user.getUsername());
                    editor.putString("password", user.getPassword());
                    editor.putBoolean("loggedIn", true);
                    editor.apply();

                    // Log and navigate to ProfileActivity
                    Log.d("LoginActivity", "Login successful, navigating to ProfileActivity");
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    startActivity(intent);

                } else {
                    // Determine the type of error based on the HTTP status code
                    int statusCode = response.code();
                    if (statusCode == 409) { // Conflict
                        Log.d("LoginActivity", "User is already logged in");
                        Toast.makeText(LoginActivity.this, "User is already logged in.", Toast.LENGTH_LONG).show();

                    } else if (statusCode == 401) { // Unauthorized - invalid password for valid username
                        Log.d("LoginActivity", "Invalid username or password");
                        Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_LONG).show();

                    } else if (statusCode == 404) { // Not Found - username not found
                        Log.d("LoginActivity", "User not found");
                        Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_LONG).show();

                    } else {
                        Log.d("LoginActivity", "Login failed with status code: " + statusCode);
                        Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.d("LoginActivity", "An error occurred: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Handles the create user button click.
     * Sends a request to create a new user account using the entered username and password.
     * Validates the username and password before sending the request.
     * If the user creation is successful, displays a success message.
     * If the creation fails, displays an appropriate error message.
     *
     * @param view The view that triggered this method (i.e., the create user button).
     */
    public void createUser(View view) {
        Log.d("LoginActivity", "createUser method executed");

        // Get the entered username and password from the EditText fields
        String enteredUserName = edUser.getText().toString();
        String enteredPassword = edPass.getText().toString();

        // Validate the username and password
        if (!isValidUserName(enteredUserName) || !isValidPassword(enteredPassword)) {
            Log.d("LoginActivity", "username or password check failed.");
            Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a new Users object
        Users newUser = new Users(enteredUserName, enteredPassword, 0);

        // Call the createUser API method
        Call<Void> call = apiService.createUser(newUser);
        call.enqueue(new Callback<Void>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // If the user is created successfully
                if (response.isSuccessful()) {
                    Log.d("CreateUser", "User created successfully");
                    Toast.makeText(LoginActivity.this, "User created successfully.", Toast.LENGTH_LONG).show();
                } else {
                    // If user creation fails, log and display the error details
                    Log.d("CreateUser", "Failed to create user");
                    Log.d("CreateUser", "Response Code: " + response.code());
                    Log.d("CreateUser", "Response Message: " + response.message());

                    try {
                        if (response.errorBody() != null) {
                            Log.d("CreateUser", "Error Body: " + response.errorBody().string());
                            Toast.makeText(LoginActivity.this, "Cannot create new user.", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // If an error occurs during the API call, display an error message
                Log.d("CreateUser", "An error occurred: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "error.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Validates the given username.
     * A valid username must be at least 8 characters long, start with a letter, and contain at least one uppercase letter, one lowercase letter, and one digit.
     * The username must be alphanumeric.
     *
     * @param username The username to validate.
     * @return true if the username is valid, false otherwise.
     */
    public static boolean isValidUserName(String username) {
        Log.d("LoginActivity", "isValidUserName method executed");

        // Username must be at least 8 characters long
        if (username == null || username.length() < 8) {
            return false;
        }
        // Username must start with a letter
        if (!Character.isLetter(username.charAt(0))) {
            return false;
        }

        // Valid username must contain at least one uppercase letter, one lowercase letter, and one digit
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        // Loop through each character in the username to check the requirements
        for (char c : username.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                // Username must be alphanumeric
                return false;
            }
        }
        // Return true if all conditions are met
        return hasUpperCase && hasLowerCase && hasDigit;
    }

    /**
     * Validates the given password.
     * A valid password must be at least 6 characters long and contain at least one digit and one letter.
     *
     * @param password The password to validate.
     * @return true if the password is valid, false otherwise.
     */
    public static boolean isValidPassword(String password) {
        Log.d("LoginActivity", "isValidPassword method executed");

        // Password must be at least 6 characters long
        if (password == null || password.length() < 6) {
            return false;
        }

        // Password must contain at least one digit and one letter
        boolean hasDigit = false;
        boolean hasChar = false;

        // Loop through each character in the password to check the requirements
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isLetter(c)) {
                hasChar = true;
            } else {
                // Invalid character (not digit or letter)
                return false;
            }
        }

        // Return true if both digit and letter are present
        return hasDigit && hasChar;
    }
}

