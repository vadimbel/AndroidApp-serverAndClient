package com.example.server.gameRequests;

import com.example.server.gameSessions.GameSessions;
import com.example.server.gameSessions.GameSessionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchmakingService {

    private static final Logger logger = LoggerFactory.getLogger(MatchmakingService.class);

    private final GameRequestRepository gameRequestRepository;

    private final GameSessionsRepository gameSessionsRepository;

    @Autowired
    public MatchmakingService(GameRequestRepository gameRequestRepository, GameSessionsRepository gameSessionsRepository) {
        this.gameRequestRepository = gameRequestRepository;
        this.gameSessionsRepository = gameSessionsRepository;
    }


    public synchronized void createGameRequest(GameRequest gameRequest) {
        try {
            // Attempt to save the game request to the repository (database)
            gameRequestRepository.save(gameRequest);
            logger.info("createGameRequest - new GameRequest is created and stored in DB.");

        } catch (IllegalArgumentException e) {
            // Handle invalid arguments passed to the save method
            logger.error("createGameRequest - Invalid game request: " + e.getMessage());
            throw e;  // Rethrow the exception to be handled by the controller

        } catch (DataIntegrityViolationException e) {
            // Handle specific database constraint violations (like duplicate entries)
            logger.error("createGameRequest - Data integrity violation: " + e.getMessage());
            throw new IllegalStateException("Game request could not be saved due to data integrity violation.", e);

        } catch (Exception e) {
            // Handle any other unexpected exceptions
            logger.error("createGameRequest - An unexpected error occurred: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred while creating the game request.", e);
        }
    }


    public synchronized void deleteGameRequest(String id) {
        logger.info("MatchMakingService - deleteGameRequest - try delete game request from DB.");
        Optional<GameRequest> existingRequest = gameRequestRepository.findById(id);
        if (existingRequest.isPresent()) {
            logger.info("MatchMakingService - deleteGameRequest - gameRequest found.");
            existingRequest.ifPresent(gameRequestRepository::delete);
            logger.info("MatchMakingService - deleteGameRequest - gameRequest deleted from DB.");
        } else {
            logger.info("MatchMakingService - deleteGameRequest - gameRequest not found.");
            throw new IllegalArgumentException("gameRequest not found.");
        }
    }


    public List<GameRequest> getAllGameRequests() {
        logger.info("getAllGameRequests - activated.");
        return gameRequestRepository.findAll();
    }

    /**
     * This method periodically checks the game requests stored in the database and tries to match pairs of players
     * who want to play the same game. The matching process is as follows:
     *
     * 1. Retrieve all game requests:
     *    - The method calls `getAllGameRequests`, which retrieves all game requests from the database.
     *    - These game requests are stored in a list called `requests`.
     *
     * 2. Group requests by game type:
     *    - `requests.stream()`: Converts the list of game requests into a stream to enable functional-style operations.
     *    - `collect(Collectors.groupingBy(GameRequest::getGameType))`: Groups the game requests by their `gameType`.
     *      This creates a map where the key is the game type (e.g., "Tic Tac Toe") and the value is a list of game
     *      requests for that game type.
     *    - `forEach((gameType, gameRequests) -> { ... })`: Iterates over each entry in the map, where `gameType` is
     *      the key and `gameRequests` is the list of requests for that game type.
     *
     * 3. Match players:
     *    - `while (gameRequests.size() > 1)`: This loop runs as long as there are at least two game requests for the
     *      current game type.
     *    - `gameRequests.remove(0)`: Removes and returns the first request in the list. This is done twice to get two
     *      players (`player1` and `player2`).
     *    - `handleMatch(player1, player2)`: This method is called to handle the matched players.
     *
     * 4. Handle the match:
     *    - The `handleMatch` method takes two matched players and handles the matchmaking process.
     *    - `System.out.println(...)`: Prints a message indicating that the players have been matched.
     *    - `gameRequestRepository.delete(...)`: Deletes the matched game requests from the database to prevent them
     *      from being matched again.
     *
     * This ensures that players who request the same game type are matched together and their requests are removed
     * from the database once they are matched.
     */
    @Scheduled(fixedRate = 5000)
    public synchronized void matchPlayers() {
        logger.info("MatchMakingService - matchPlayers - executed");
        try {
            List<GameRequest> requests = getAllGameRequests();

            // group by each game type
            // then for each group try to find two players that want to play the same gameType
            requests.stream()
                    .collect(Collectors.groupingBy(GameRequest::getGameType))
                    .forEach((gameType, gameRequests) -> {
                        logger.info("MatchMakingService - matchPlayers - gameType : " + gameType);
                        while (gameRequests.size() > 1) {
                            logger.info("MatchMakingService - matchPlayers - found two players.");
                            GameRequest player1 = gameRequests.remove(0);
                            GameRequest player2 = gameRequests.remove(0);
                            handleMatch(player1, player2);
                            logger.info("MatchMakingService - matchPlayers - handleMatch finished.");
                        }
                    });
        } catch (Exception e) {
            logger.error("MatchMakingService - matchPlayers - error during matching process: ", e);
        }
    }


    /**
     * This method handles the match between two players.
     *
     * @param player1 the first player to be matched
     * @param player2 the second player to be matched
     */
    private synchronized void handleMatch(GameRequest player1, GameRequest player2) {
        logger.info("MatchMakingService - handleMatch - executed.");
        try {
            logger.info("MatchMakingService - handleMatch - removing items from GameRequests DB.");
            gameRequestRepository.delete(player1);
            gameRequestRepository.delete(player2);

            logger.info("MatchMakingService - handleMatch - creating new GameSessions DB.");
            String gameId = player1.getUsername() + "_" + player2.getUsername() + "_" + player1.getGameType();
            GameSessions game = new GameSessions(gameId, player1.getUsername(), player2.getUsername(), player1.getGameType());
            gameSessionsRepository.save(game);

            logger.info("MatchMakingService - handleMatch - players deleted successfully.");
        } catch (Exception e) {
            logger.error("MatchMakingService - handleMatch - error deleting players: ", e);
        }
    }

    public Optional<GameSessions> findGameSessionByUsername(String username) {
        logger.info("MatchMakingService - findGameSessionByUsername - executed.");

        try {
            return gameSessionsRepository.findByFirstUsernameOrSecondUsername(username, username);
        } catch (Exception e) {
            // Log the exception
            logger.error("MatchMakingService - findGameSessionByUsername - Exception occurred: ", e);
            return Optional.empty(); // Or rethrow the exception if needed
        }
    }


}

