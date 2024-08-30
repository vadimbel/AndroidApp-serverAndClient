package com.example.server.users;

import com.example.server.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
    private final UsersRepository usersRepository;      // Data access layer object

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        // Constructor for initializing the data access layer object and stats service
        this.usersRepository = usersRepository;
    }

    /**
     * Retrieve all users.
     *
     * @return list of users
     */
    public List<Users> getUsers() {
        // Implementation using the data access layer
        return usersRepository.findAll();
    }

    /**
     * Add a new user to the system.
     *
     * @param user the user to add
     */
    public synchronized void addUser(Users user) {
        logger.info("Attempting to add user: {}", user.getUsername());

        // Validate username and password
        if (!Utilities.isValidUserName(user.getUsername())) {
            logger.error("Invalid username: {}", user.getUsername());
            throw new IllegalArgumentException("Invalid username");
        }
        if (!Utilities.isValidPassword(user.getPassword())) {
            logger.error("Invalid password for user: {}", user.getUsername());
            throw new IllegalArgumentException("Invalid password");
        }

        // Check if the user already exists
        Optional<Users> existingUser = usersRepository.findById(user.getUsername());
        if (existingUser.isPresent()) {
            logger.error("Username already exists: {}", user.getUsername());
            throw new IllegalStateException("Username already exists");
        }

        // Save the new user
        usersRepository.save(user);
        logger.info("User added successfully: {}", user.getUsername());
    }

    /**
     * Perform login action for a user.
     *
     * @param username the username
     * @param password the password
     * @return the user if login is successful, null otherwise
     */
    public synchronized Users loginAction(String username, String password) {
        // Try to find the existing user by username
        Optional<Users> existingUser = usersRepository.findById(username);
        if (existingUser.isPresent()) {
            Users user = existingUser.get();
            // Check if the password matches
            if (user.getPassword().equals(password)) {
                // Check if the user is already logged in
                if (user.getLoggedIn() == 1) {
                    throw new IllegalStateException("User is already logged in.");
                }
                // Update the loggedIn status to true
                user.setLoggedIn(1);
                usersRepository.save(user);
                return user;

            } else {    // username is found, provided password not match
                throw new IllegalArgumentException("Invalid password.");
            }
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }


    /**
     * Delete a user from the system.
     *
     * @param username the username of the user to delete
     */
    public synchronized void deleteAction(String username) {
        // Try to find the existing user by username
        Optional<Users> optionalUser = usersRepository.findById(username);
        if (optionalUser.isPresent()) {
            // Delete the user if found
            usersRepository.delete(optionalUser.get());
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    /**
     * Perform logout action for a user.
     *
     * @param username the username of the user to log out
     */
    public synchronized void logoutAction(String username) {
        // Try to find the existing user by username
        Optional<Users> optionalUser = usersRepository.findById(username);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            // Update the loggedIn status to false
            user.setLoggedIn(0);
            usersRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
}

