package com.example.server.websocket;

import com.example.server.gameSessions.GameSessionsRepository;
import com.example.server.games.CheckersGameSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import com.example.server.games.BaseGameSession;
import com.example.server.games.TicTacToeGameSession;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private GameSessionsRepository gameSessionsRepository;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    // contains the websockets channel of the two players play against each other
    private final ConcurrentMap<String, WebSocketSession[]> sessionsMap = new ConcurrentHashMap<>();

    // contains the game type (tic-tac-toe, checkers, ...) that the two players play against each other
    private final ConcurrentMap<String, BaseGameSession> gameSessionsMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocketHandler - afterConnectionEstablished - New WebSocket connection established: " + session.getId());

        // extract 'gameSessionId' , 'gameType' fields from session
        String gameSessionId = getQueryParam(session, "gameSessionId");
        String gameType = getQueryParam(session, "gameType");

        logger.info("gameSessionId : " + gameSessionId);
        logger.info("gameType : " + gameType);

        // get the websocket channel related to 'gameSessionId' game
        WebSocketSession[] sessions = sessionsMap.computeIfAbsent(gameSessionId, k -> new WebSocketSession[2]);

        // if it is the first websocket connections -> store the first connection and wait for opponent connection
        if (sessions[0] == null) {
            sessions[0] = session;
            // Wait for 5 seconds for the second player to connect
            waitForSecondPlayer(gameSessionId, session);

        } else if (sessions[1] == null) {
            sessions[1] = session;
            // If second player connects, proceed to start the game
            logger.info("Both players are connected for gameSessionId: " + gameSessionId);
            String playerOneUsername = getQueryParam(sessions[0], "username");
            String playerTwoUsername = getQueryParam(sessions[1], "username");
            logger.info("player1 : " + playerOneUsername + " player2 : " + playerTwoUsername);
            startGame(gameSessionId, gameType);       // start the game

        } else {
            session.sendMessage(new TextMessage("Game session is already full."));
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Game session is already full."));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("WebSocketHandler - handleTextMessage : " + message.getPayload());

        // get the json received from client that send message
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(message.getPayload());

        String gameSessionId = getQueryParam(session, "gameSessionId");
        String game = jsonNode.get("game").asText();            // game type (tic-tac-toe, checkers, ...)
        String messageType = jsonNode.get("messageType").asText();    // type of message (move, forfeit, ...)

        BaseGameSession gameSession = gameSessionsMap.get(gameSessionId); // get the game item from data structure

        // Handle different message types according to game type (tic-tac-toe, checkers, ...)
        if (gameSession instanceof TicTacToeGameSession ticTacToeGameSession) {
            logger.info("WebSocketHandler - handleTextMessage - tic-tac-toe");
            // Handle different message types
            if (messageType.equals("move")) {
                logger.info("WebSocketHandler - handleTextMessage - tic-tac-toe - move");

                // 'value' represents the button clicked by client, 'mark' players current mark 'X' or 'O'
                String value = jsonNode.get("value").asText();
                String state = jsonNode.get("state").asText();
                String mark = jsonNode.get("mark").asText();
                char markChar = jsonNode.get("mark").asText().charAt(0);

                // check if move is valid, modify class object board
                boolean validMove = ticTacToeGameSession.validatePlayersMove(value, markChar);

                // handle if move isn't valid
                if (!validMove) {
                    invalidMoveMessage(session);
                    return;
                }

                // if (gameIsOver)
                if (ticTacToeGameSession.isWinner()) {
                    logger.info("WebSocketHandler - handleTextMessage - tic-tac-toe - move - isWinner");

                    playerWon(session, gameSessionId);  // modify players board

                    // Introduce a 2-second delay
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        logger.error("Interrupted while waiting", e);
                        Thread.currentThread().interrupt();
                    }

                    endGame(session, gameSessionId);        // end game

                } else if (ticTacToeGameSession.isDraw()) {
                    logger.info("WebSocketHandler - handleTextMessage - tic-tac-toe - move - isDraw");

                    playerDraw(session, gameSessionId, value, mark);

                    // Introduce a 2-second delay
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        logger.error("Interrupted while waiting", e);
                        Thread.currentThread().interrupt();
                    }

                    endGame(session, gameSessionId);        // end game

                } else {
                    logger.info("WebSocketHandler - handleTextMessage - tic-tac-toe - move - game Not over - switchTurn");
                    switchTurn(session, gameSessionId, value, mark);  // switch turn in game, send both player messages
                }

            } else if (messageType.equals("forfeit")) {
                logger.info("WebSocketHandler - handleTextMessage - tic-tac-toe - forfeit");
                playerForfeit(session, gameSessionId);

            } else {
                logger.info("WebSocketHandler - handleTextMessage - tic-tac-toe - else");

            }

        } else if (gameSession instanceof CheckersGameSession checkersGameSession) {
            logger.info("WebSocketHandler - handleTextMessage - checkers");

            if (messageType.equals("move")) {
                logger.info("WebSocketHandler - handleTextMessage - checkers - move");

            }
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocketHandler - afterConnectionClosed - WebSocket connection closed: " + session.getId());
    }

    private void waitForSecondPlayer(String gameSessionId, WebSocketSession session) {
        logger.info("WebSocketHandler - waitForSecondPlayer");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.schedule(() -> {
            WebSocketSession[] sessions = sessionsMap.get(gameSessionId);

            if (sessions == null || sessions[1] == null) {
                try {
                    if (sessions != null && sessions[0] != null) {
                        logger.info("Opponent did not connect in time. Ending game...");
                        connectionFailMessage(sessions[0]);
                        sessions[0].close(CloseStatus.NOT_ACCEPTABLE.withReason("Opponent did not connect in time."));
                    }
                } catch (IOException e) {
                    logger.error("Error closing session: ", e);
                } finally {
                    sessionsMap.remove(gameSessionId);

                }
            }
        }, 5, TimeUnit.SECONDS);

        scheduler.shutdown();
    }

    private void invalidMoveMessage(WebSocketSession session) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonMessage = new HashMap<>();
        jsonMessage.put("messageType", "invalidMove");
        String jsonString = mapper.writeValueAsString(jsonMessage);
        session.sendMessage(new TextMessage(jsonString));
    }

    private void connectionFailMessage(WebSocketSession session) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonMessage = new HashMap<>();
        jsonMessage.put("messageType", "connectionFail");
        String jsonString = mapper.writeValueAsString(jsonMessage);
        session.sendMessage(new TextMessage(jsonString));
    }

    private void startGame(String gameSessionId, String gameType) throws IOException {
        logger.info("WebSocketHandler - startGame");

        if (gameType.equals("Tic Tac Toe")) {
            logger.info("WebSocketHandler - startGame - Tic Tac Toe");
            // create game object and store it
            TicTacToeGameSession ticTacToeGameSession = new TicTacToeGameSession(gameSessionId);
            gameSessionsMap.put(gameSessionId, ticTacToeGameSession);

            // Send a message to both players to indicate the game is starting
            WebSocketSession[] sessions = sessionsMap.get(gameSessionId);
            startGameMessageToClient(sessions[0], "O", "blocked");
            startGameMessageToClient(sessions[1], "X", "active");

        } else if (gameType.equals("Checkers")) {
            // not relevant for now
            logger.info("WebSocketHandler - startGame - Checkers");
            CheckersGameSession checkersGameSession = new CheckersGameSession(gameSessionId);
            gameSessionsMap.put(gameSessionId, checkersGameSession);

            WebSocketSession[] sessions = sessionsMap.get(gameSessionId);
            startGameMessageToClient(sessions[0], "R", "blocked");
            startGameMessageToClient(sessions[1], "B", "active");

        } else {
            logger.info("WebSocketHandler - startGame - not supposed to be here");
        }
    }

    private void startGameMessageToClient(WebSocketSession session, String mark, String state) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonMessage = new HashMap<>();
        jsonMessage.put("messageType", "startGame");
        jsonMessage.put("mark", mark);
        jsonMessage.put("state", state);
        String jsonString = mapper.writeValueAsString(jsonMessage);
        session.sendMessage(new TextMessage(jsonString));
    }

    private void switchTurn(WebSocketSession session, String gameSessionId, String value, String mark) throws IOException {
        WebSocketSession[] sessions = sessionsMap.get(gameSessionId);
        WebSocketSession otherSession = (sessions[0] == session) ? sessions[1] : sessions[0];

        // Notify the player who made the move to be in "blocked" state
        switchTurnMessageToClient(session, "blocked", value, mark);

        // Notify the other player to be in "active" state
        switchTurnMessageToClient(otherSession, "active", value, mark);
    }


    public void switchTurnMessageToClient(WebSocketSession session, String playersNewState, String value, String mark) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonMessage = new HashMap<>();
        jsonMessage.put("messageType", "switchTurn");           // action name
        jsonMessage.put("playersNewState", playersNewState);    // switch turns
        jsonMessage.put("value", value);                        // button was clicked in last player move
        jsonMessage.put("mark", mark);                          // mark of the last player made a move
        String jsonString = mapper.writeValueAsString(jsonMessage);
        session.sendMessage(new TextMessage(jsonString));
    }

    public void playerWon(WebSocketSession session, String gameSessionId) throws IOException {
        logger.info("WebSocketHandler - playerWon");

        // Retrieve the websockets of current game
        WebSocketSession[] sessions = sessionsMap.get(gameSessionId);
        WebSocketSession otherSession = (sessions[0] == session) ? sessions[1] : sessions[0];

        // Retrieve the TicTacToe game session
        TicTacToeGameSession ticTacToeGameSession = (TicTacToeGameSession) gameSessionsMap.get(gameSessionId);

        // Get the winning combination
        String[] combination = ticTacToeGameSession.getWinningCombination();

        // Use the winning combination as needed
        // For example, log it:
        logger.info("Winning combination: " + String.join(", ", combination));

        playerWonMessageToClient(session, "playerWon", combination);
        playerWonMessageToClient(otherSession, "playerLose", combination);
    }

    public void playerWonMessageToClient(WebSocketSession session, String messageType, String[] combination) throws IOException {
        logger.info("WebSocketHandler - playerWonMessageToClient");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonMessage = new HashMap<>();
        jsonMessage.put("messageType", messageType);                // playerWon or playerLose message
        jsonMessage.put("first", combination[0]);
        jsonMessage.put("second", combination[1]);
        jsonMessage.put("third", combination[2]);
        String jsonString = mapper.writeValueAsString(jsonMessage);
        session.sendMessage(new TextMessage(jsonString));
    }

    public void playerForfeit(WebSocketSession session, String gameSessionId) throws IOException {
        logger.info("WebSocketHandler - playerForfeit");
        WebSocketSession[] sessions = sessionsMap.get(gameSessionId);
        WebSocketSession otherSession = (sessions[0] == session) ? sessions[1] : sessions[0];

        forfeitMessageToClient(session, "playerForfeit", "lose");
        forfeitMessageToClient(otherSession, "playerForfeit", "win");

        // Remove the game session from 'game_sessions' table
        gameSessionsRepository.deleteById(gameSessionId);

        // Remove the game session from both maps
        sessionsMap.remove(gameSessionId);
        gameSessionsMap.remove(gameSessionId);
        logger.info("WebSocketHandler - playerForfeit - Game session " + gameSessionId + " removed from sessionsMap and gameSessionsMap");
    }

    public void forfeitMessageToClient(WebSocketSession session, String messageType, String winOrLose) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonMessage = new HashMap<>();

        jsonMessage.put("messageType", messageType);
        jsonMessage.put("winOrLose", winOrLose);
        String jsonString = mapper.writeValueAsString(jsonMessage);
        session.sendMessage(new TextMessage(jsonString));
    }

    public void playerDraw(WebSocketSession session, String gameSessionId, String value, String mark) throws IOException {
        logger.info("WebSocketHandler - playerDraw");

        // Retrieve the websockets of current game
        WebSocketSession[] sessions = sessionsMap.get(gameSessionId);
        WebSocketSession otherSession = (sessions[0] == session) ? sessions[1] : sessions[0];

        playerDrawMessageToClient(session, "playerDraw", value, mark);
        playerDrawMessageToClient(otherSession, "playerDraw", value, mark);
    }

    public void playerDrawMessageToClient(WebSocketSession session, String messageType, String value, String mark) throws IOException {
        logger.info("WebSocketHandler - playerDrawMessageToClient");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonMessage = new HashMap<>();
        jsonMessage.put("messageType", messageType);
        jsonMessage.put("value", value);
        jsonMessage.put("mark", mark);
        String jsonString = mapper.writeValueAsString(jsonMessage);
        session.sendMessage(new TextMessage(jsonString));
    }

    public void endGame(WebSocketSession session, String gameSessionId) throws IOException {
        logger.info("WebSocketHandler - endGame");

        // Retrieve the websockets of the current game
        WebSocketSession[] sessions = sessionsMap.get(gameSessionId);
        WebSocketSession otherSession = (sessions[0] == session) ? sessions[1] : sessions[0];

        // Remove the game session from 'game_sessions' table
        gameSessionsRepository.deleteById(gameSessionId);

        // Remove the game session from both maps
        sessionsMap.remove(gameSessionId);
        gameSessionsMap.remove(gameSessionId);
        logger.info("WebSocketHandler - endGame - Game session " + gameSessionId + " removed from sessionsMap and gameSessionsMap");

        // Notify the clients about the end of the game
        endGameMessageToClient(session);
        endGameMessageToClient(otherSession);
    }

    public void endGameMessageToClient(WebSocketSession session) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonMessage = new HashMap<>();
        jsonMessage.put("messageType", "endGame");
        String jsonString = mapper.writeValueAsString(jsonMessage);
        session.sendMessage(new TextMessage(jsonString));
    }

    private String getQueryParam(WebSocketSession session, String paramName) {
        return session.getUri().getQuery().split(paramName + "=")[1].split("&")[0];
    }

}


