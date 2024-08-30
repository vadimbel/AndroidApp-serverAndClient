package com.example.clinet.websocket;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {

    private static final String TAG = "WebSocketClient";
    private WebSocket webSocket;
    private OnWebSocketConnectedListener onWebSocketConnectedListener;
    private OnWebSocketMessageListener onWebSocketMessageListener;

    public interface OnWebSocketConnectedListener {
        void onWebSocketConnected();
    }

    public interface OnWebSocketMessageListener {
        void onMessageReceived(String message);
    }

    public void setOnWebSocketConnectedListener(OnWebSocketConnectedListener listener) {
        this.onWebSocketConnectedListener = listener;
    }

    public void setOnWebSocketMessageListener(OnWebSocketMessageListener listener) {
        this.onWebSocketMessageListener = listener;
    }

    public void connect(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket connection opened: " + response.message());
                if (onWebSocketConnectedListener != null) {
                    onWebSocketConnectedListener.onWebSocketConnected();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received message: " + text);
                if (onWebSocketMessageListener != null) {
                    onWebSocketMessageListener.onMessageReceived(text);
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                Log.d(TAG, "WebSocket connection closing: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket connection failed: " + t.getMessage());
            }
        });
        client.dispatcher().executorService().shutdown();
    }

    public void sendMessage(String game, String messageType, String value) {
        if (webSocket != null) {
            try {
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("game", game);
                jsonMessage.put("messageType", messageType);
                jsonMessage.put("value", value);
                webSocket.send(jsonMessage.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Failed to create JSON message: " + e.getMessage());
            }
        }
    }

    public void sendMoveMessage(String game, String messageType, String value, String mark, String state) {
        Log.e(TAG, "sendMoveMessage");
        if (webSocket != null) {
            try {
                if (game.equals("Tic Tac Toe")) {
                    Log.e(TAG, "sendMoveMessage - Tic Tac Toe");
                    JSONObject jsonMessage = new JSONObject();
                    jsonMessage.put("game", game);
                    jsonMessage.put("messageType", messageType);
                    jsonMessage.put("value", value);
                    jsonMessage.put("mark", mark);
                    jsonMessage.put("state", state);
                    webSocket.send(jsonMessage.toString());

                } else if (game.equals("Checkers")) {
                    Log.e(TAG, "sendMoveMessage - checkers");

                } else {
                    Log.e(TAG, "sendMoveMessage - else");

                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to create JSON message: " + e.getMessage());
            }
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }
}



