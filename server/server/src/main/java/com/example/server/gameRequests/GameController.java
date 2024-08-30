package com.example.server.gameRequests;

import com.example.server.gameSessions.GameSessions;
import com.example.server.users.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    @Autowired
    private MatchmakingService matchmakingService;

    @PostMapping("/request")
    public void createGameRequest(@RequestBody GameRequest gameRequest) {
        try {
            logger.info("GameController - createGameRequest executed");
            matchmakingService.createGameRequest(gameRequest);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);

        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists", e);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating user", e);
        }
    }

    @GetMapping("/request")
    public ResponseEntity<List<GameRequest>> getAllGameRequests() {
        List<GameRequest> gameRequests = matchmakingService.getAllGameRequests();
        return ResponseEntity.ok(gameRequests);
    }

    @DeleteMapping("/request")
    public void deleteGameRequest(@RequestParam String id) {
        try {
            logger.info("GameController - deleteGameRequest - try delete game request from DB.");
            matchmakingService.deleteGameRequest(id);
        } catch (IllegalArgumentException e) {
            logger.info("GameController - deleteGameRequest - gameRequest not found.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "gameRequest not found", e);
        } catch (Exception e) {
            logger.info("GameController - deleteGameRequest - An error occurred while deleting game request.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while deleting game request", e);
        }
    }

    @GetMapping("/session")
    public ResponseEntity<GameSessions> checkGameSession(@RequestParam String username) {
        logger.info("GameController - checkGameSession - trying to find username in GameSessions DB table.");

        try {
            // Attempt to find the game session
            Optional<GameSessions> gameSession = matchmakingService.findGameSessionByUsername(username);

            // If found, return the session with ok 200 status
            // else, return NO_CONTENT status
            return gameSession.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        } catch (Exception e) {
            // Log the exception and return an INTERNAL_SERVER_ERROR status
            logger.error("GameController - checkGameSession - Exception occurred: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}

