package com.example.clinet.checkers;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.clinet.R;
import com.example.clinet.websocket.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckersActivity extends AppCompatActivity implements WebSocketClient.OnWebSocketMessageListener {

    private final String TAG = "CheckersActivity";
    private WebSocketClient webSocketClient;
    private TextView playerTurn;
    private String mark = "";
    private String playerState;
    private AppCompatButton selectedButton = null; // To keep track of the selected button
    int prevBackgroundColor;
    private int [] prevBtnId;
    private androidx.appcompat.widget.AppCompatButton cell00;
    private androidx.appcompat.widget.AppCompatButton cell01;
    private androidx.appcompat.widget.AppCompatButton cell02;
    private androidx.appcompat.widget.AppCompatButton cell03;
    private androidx.appcompat.widget.AppCompatButton cell04;
    private androidx.appcompat.widget.AppCompatButton cell05;
    private androidx.appcompat.widget.AppCompatButton cell06;
    private androidx.appcompat.widget.AppCompatButton cell07;

    private androidx.appcompat.widget.AppCompatButton cell10;
    private androidx.appcompat.widget.AppCompatButton cell11;
    private androidx.appcompat.widget.AppCompatButton cell12;
    private androidx.appcompat.widget.AppCompatButton cell13;
    private androidx.appcompat.widget.AppCompatButton cell14;
    private androidx.appcompat.widget.AppCompatButton cell15;
    private androidx.appcompat.widget.AppCompatButton cell16;
    private androidx.appcompat.widget.AppCompatButton cell17;

    private androidx.appcompat.widget.AppCompatButton cell20;
    private androidx.appcompat.widget.AppCompatButton cell21;
    private androidx.appcompat.widget.AppCompatButton cell22;
    private androidx.appcompat.widget.AppCompatButton cell23;
    private androidx.appcompat.widget.AppCompatButton cell24;
    private androidx.appcompat.widget.AppCompatButton cell25;
    private androidx.appcompat.widget.AppCompatButton cell26;
    private androidx.appcompat.widget.AppCompatButton cell27;

    private androidx.appcompat.widget.AppCompatButton cell30;
    private androidx.appcompat.widget.AppCompatButton cell31;
    private androidx.appcompat.widget.AppCompatButton cell32;
    private androidx.appcompat.widget.AppCompatButton cell33;
    private androidx.appcompat.widget.AppCompatButton cell34;
    private androidx.appcompat.widget.AppCompatButton cell35;
    private androidx.appcompat.widget.AppCompatButton cell36;
    private androidx.appcompat.widget.AppCompatButton cell37;

    private androidx.appcompat.widget.AppCompatButton cell40;
    private androidx.appcompat.widget.AppCompatButton cell41;
    private androidx.appcompat.widget.AppCompatButton cell42;
    private androidx.appcompat.widget.AppCompatButton cell43;
    private androidx.appcompat.widget.AppCompatButton cell44;
    private androidx.appcompat.widget.AppCompatButton cell45;
    private androidx.appcompat.widget.AppCompatButton cell46;
    private androidx.appcompat.widget.AppCompatButton cell47;

    private androidx.appcompat.widget.AppCompatButton cell50;
    private androidx.appcompat.widget.AppCompatButton cell51;
    private androidx.appcompat.widget.AppCompatButton cell52;
    private androidx.appcompat.widget.AppCompatButton cell53;
    private androidx.appcompat.widget.AppCompatButton cell54;
    private androidx.appcompat.widget.AppCompatButton cell55;
    private androidx.appcompat.widget.AppCompatButton cell56;
    private androidx.appcompat.widget.AppCompatButton cell57;

    private androidx.appcompat.widget.AppCompatButton cell60;
    private androidx.appcompat.widget.AppCompatButton cell61;
    private androidx.appcompat.widget.AppCompatButton cell62;
    private androidx.appcompat.widget.AppCompatButton cell63;
    private androidx.appcompat.widget.AppCompatButton cell64;
    private androidx.appcompat.widget.AppCompatButton cell65;
    private androidx.appcompat.widget.AppCompatButton cell66;
    private androidx.appcompat.widget.AppCompatButton cell67;

    private androidx.appcompat.widget.AppCompatButton cell70;
    private androidx.appcompat.widget.AppCompatButton cell71;
    private androidx.appcompat.widget.AppCompatButton cell72;
    private androidx.appcompat.widget.AppCompatButton cell73;
    private androidx.appcompat.widget.AppCompatButton cell74;
    private androidx.appcompat.widget.AppCompatButton cell75;
    private androidx.appcompat.widget.AppCompatButton cell76;
    private androidx.appcompat.widget.AppCompatButton cell77;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkers_game);

        cell00 = findViewById(R.id.button_00);
        cell01 = findViewById(R.id.button_01);
        cell02 = findViewById(R.id.button_02);
        cell03 = findViewById(R.id.button_03);
        cell04 = findViewById(R.id.button_04);
        cell05 = findViewById(R.id.button_05);
        cell06 = findViewById(R.id.button_06);
        cell07 = findViewById(R.id.button_07);

        cell10 = findViewById(R.id.button_10);
        cell11 = findViewById(R.id.button_11);
        cell12 = findViewById(R.id.button_12);
        cell13 = findViewById(R.id.button_13);
        cell14 = findViewById(R.id.button_14);
        cell15 = findViewById(R.id.button_15);
        cell16 = findViewById(R.id.button_16);
        cell17 = findViewById(R.id.button_17);

        cell20 = findViewById(R.id.button_20);
        cell21 = findViewById(R.id.button_21);
        cell22 = findViewById(R.id.button_22);
        cell23 = findViewById(R.id.button_23);
        cell24 = findViewById(R.id.button_24);
        cell25 = findViewById(R.id.button_25);
        cell26 = findViewById(R.id.button_26);
        cell27 = findViewById(R.id.button_27);

        cell30 = findViewById(R.id.button_30);
        cell31 = findViewById(R.id.button_31);
        cell32 = findViewById(R.id.button_32);
        cell33 = findViewById(R.id.button_33);
        cell34 = findViewById(R.id.button_34);
        cell35 = findViewById(R.id.button_35);
        cell36 = findViewById(R.id.button_36);
        cell37 = findViewById(R.id.button_37);

        cell40 = findViewById(R.id.button_40);
        cell41 = findViewById(R.id.button_41);
        cell42 = findViewById(R.id.button_42);
        cell43 = findViewById(R.id.button_43);
        cell44 = findViewById(R.id.button_44);
        cell45 = findViewById(R.id.button_45);
        cell46 = findViewById(R.id.button_46);
        cell47 = findViewById(R.id.button_47);

        cell50 = findViewById(R.id.button_50);
        cell51 = findViewById(R.id.button_51);
        cell52 = findViewById(R.id.button_52);
        cell53 = findViewById(R.id.button_53);
        cell54 = findViewById(R.id.button_54);
        cell55 = findViewById(R.id.button_55);
        cell56 = findViewById(R.id.button_56);
        cell57 = findViewById(R.id.button_57);

        cell60 = findViewById(R.id.button_60);
        cell61 = findViewById(R.id.button_61);
        cell62 = findViewById(R.id.button_62);
        cell63 = findViewById(R.id.button_63);
        cell64 = findViewById(R.id.button_64);
        cell65 = findViewById(R.id.button_65);
        cell66 = findViewById(R.id.button_66);
        cell67 = findViewById(R.id.button_67);

        cell70 = findViewById(R.id.button_70);
        cell71 = findViewById(R.id.button_71);
        cell72 = findViewById(R.id.button_72);
        cell73 = findViewById(R.id.button_73);
        cell74 = findViewById(R.id.button_74);
        cell75 = findViewById(R.id.button_75);
        cell76 = findViewById(R.id.button_76);
        cell77 = findViewById(R.id.button_77);

        // get information through intent
        String gameSessionId = getIntent().getStringExtra("gameSessionId");
        String firstUsername = getIntent().getStringExtra("firstUsername");
        String secondUsername = getIntent().getStringExtra("secondUsername");
        String gameType = getIntent().getStringExtra("gameType");

        playerTurn = findViewById(R.id.PlayerTurn);

        // Client sends first message to server (just to see that the server receives messages from client)
        webSocketClient = new WebSocketClient();
        webSocketClient.setOnWebSocketMessageListener(this);

        // Connect to server
        String url = "ws://10.0.2.2:8080/ws?gameSessionId=" + gameSessionId + "&username=" + firstUsername + "&gameType=" + gameType;
        webSocketClient.connect(url);

        // Initialize the board
        initializeBoard();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMessageReceived(String message) {
        Log.d(TAG, "onMessageReceived executed.");
        runOnUiThread(() -> {
            try {
                JSONObject json = new JSONObject(message);
                String messageType = json.getString("messageType");  // get messageType from server

                if ("startGame".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - startGame.");
                    // get player mark and enable/disable board
                    String mark = json.getString("mark");
                    String state = json.getString("state");

                    // set players attributes and enable/disable board
                    this.mark = mark;
                    this.playerState = state;
                    setActiveState();

                } else if ("switchTurn".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - switchTurn.");


                }  else if ("playerWon".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - playerWon.");


                } else if ("playerLose".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - playerLose.");


                } else if ("endGame".equals(messageType)) {
                    Log.d(TAG, "onMessageReceived - endGame.");


                }

            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse message: " + e.getMessage());
            }
        });
    }

    public void move(View view) {
        Log.d(TAG, "CheckersActivity - move executed.");
        // Cast the view to a button
        AppCompatButton btn = (AppCompatButton) view;

        // Get the button ID
        String btnId = getResources().getResourceEntryName(btn.getId());

        Log.d(TAG, "Button ID: " + btnId);

        if (selectedButton == null) {
            // First selection: check if it's the player's piece
            if (btn.getText().equals(this.mark)) {
                Log.d(TAG, "CheckersActivity - move - players mark");
                selectedButton = btn;
                prevBackgroundColor = ((ColorDrawable) btn.getBackground()).getColor();
                btn.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark)); // Change background color
                prevBtnId = getButtonPosition(btnId);
            } else {
                Log.d(TAG, "CheckersActivity - move - not players mark");
                // Not the player's piece, do nothing or show a message
                Toast.makeText(this, "Select your piece", Toast.LENGTH_SHORT).show();

            }
        } else if (view.equals(selectedButton)){
            // same button selected twice => cancel the turn
            Log.d(TAG, "same button selected");
            // change back to previews color and cancel selection of button, and remove id
            selectedButton.setBackgroundColor(prevBackgroundColor);
            selectedButton = null;
            prevBtnId = null;
        } else {
            Log.d(TAG, "other button selected");
            // check if new place is valid

            // check if new place is empty

            //
        }
    }

    private int[] getButtonPosition(String buttonId) {
        // Assuming the buttonId is in the format "button_77"
        if (buttonId.startsWith("button_")) {
            String position = buttonId.substring(7); // Extracts the part after "button_"
            int row = Character.getNumericValue(position.charAt(0));
            int col = Character.getNumericValue(position.charAt(1));
            return new int[] {row, col};
        }
        return new int[] {-1, -1}; // Return an invalid position if the format is incorrect
    }


    private void setActiveState() {
        if (this.playerState.equals("active")) {
            enableUI();
            playerTurn.setText("YOUR TURN");
        } else {
            disableUI();
            playerTurn.setText("OPPONENT TURN");
        }
    }

    private void enableUI() {
        Log.d(TAG, "enableUI");
        // Enable the UI elements
        setBoardButtonsClickable(true);
    }

    private void disableUI() {
        Log.d(TAG, "disableUI");
        // Disable the UI elements
        setBoardButtonsClickable(false);
    }

    // Helper method to enable/disable board buttons
    private void setBoardButtonsClickable(boolean clickable) {
        cell00.setClickable(clickable);
        cell01.setClickable(clickable);
        cell02.setClickable(clickable);
        cell03.setClickable(clickable);
        cell04.setClickable(clickable);
        cell05.setClickable(clickable);
        cell06.setClickable(clickable);
        cell07.setClickable(clickable);
        cell10.setClickable(clickable);
        cell11.setClickable(clickable);
        cell12.setClickable(clickable);
        cell13.setClickable(clickable);
        cell14.setClickable(clickable);
        cell15.setClickable(clickable);
        cell16.setClickable(clickable);
        cell17.setClickable(clickable);
        cell20.setClickable(clickable);
        cell21.setClickable(clickable);
        cell22.setClickable(clickable);
        cell23.setClickable(clickable);
        cell24.setClickable(clickable);
        cell25.setClickable(clickable);
        cell26.setClickable(clickable);
        cell27.setClickable(clickable);
        cell30.setClickable(clickable);
        cell31.setClickable(clickable);
        cell32.setClickable(clickable);
        cell33.setClickable(clickable);
        cell34.setClickable(clickable);
        cell35.setClickable(clickable);
        cell36.setClickable(clickable);
        cell37.setClickable(clickable);
        cell40.setClickable(clickable);
        cell41.setClickable(clickable);
        cell42.setClickable(clickable);
        cell43.setClickable(clickable);
        cell44.setClickable(clickable);
        cell45.setClickable(clickable);
        cell46.setClickable(clickable);
        cell47.setClickable(clickable);
        cell50.setClickable(clickable);
        cell51.setClickable(clickable);
        cell52.setClickable(clickable);
        cell53.setClickable(clickable);
        cell54.setClickable(clickable);
        cell55.setClickable(clickable);
        cell56.setClickable(clickable);
        cell57.setClickable(clickable);
        cell60.setClickable(clickable);
        cell61.setClickable(clickable);
        cell62.setClickable(clickable);
        cell63.setClickable(clickable);
        cell64.setClickable(clickable);
        cell65.setClickable(clickable);
        cell66.setClickable(clickable);
        cell67.setClickable(clickable);
        cell70.setClickable(clickable);
        cell71.setClickable(clickable);
        cell72.setClickable(clickable);
        cell73.setClickable(clickable);
        cell74.setClickable(clickable);
        cell75.setClickable(clickable);
        cell76.setClickable(clickable);
        cell77.setClickable(clickable);
    }


    private void initializeBoard() {
        // Place red pieces
        placePiece(cell00, "red");
        placePiece(cell02, "red");
        placePiece(cell04, "red");
        placePiece(cell06, "red");
        placePiece(cell11, "red");
        placePiece(cell13, "red");
        placePiece(cell15, "red");
        placePiece(cell17, "red");
        placePiece(cell20, "red");
        placePiece(cell22, "red");
        placePiece(cell24, "red");
        placePiece(cell26, "red");

        // Place black pieces
        placePiece(cell71, "black");
        placePiece(cell73, "black");
        placePiece(cell75, "black");
        placePiece(cell77, "black");
        placePiece(cell60, "black");
        placePiece(cell62, "black");
        placePiece(cell64, "black");
        placePiece(cell66, "black");
        placePiece(cell51, "black");
        placePiece(cell53, "black");
        placePiece(cell55, "black");
        placePiece(cell57, "black");
    }

    private void placePiece(androidx.appcompat.widget.AppCompatButton button, String color) {
        if (color.equals("red")) {
            button.setText("R");
            button.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

        } else {
            button.setText("B");
            button.setTextColor(getResources().getColor(android.R.color.black));
        }

        button.setTextSize(20);
        button.setEnabled(true);
    }

}