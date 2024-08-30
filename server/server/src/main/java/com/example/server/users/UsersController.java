package com.example.server.users;

// the API layer

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    private final UsersService usersService;        // service layer object of class 'UsersService'

    @Autowired
    public UsersController(UsersService usersService) {
        // constructor to initialize service layer object
        this.usersService = usersService;
    }

    @GetMapping
    public List<Users> getUsers() {
        // using the method from the service layer
        return usersService.getUsers();
    }

    @PostMapping("/create")
    public void createUser(@RequestBody Users user) {
        logger.info("UsersController - createUser - executed.");
        try {
            logger.info("UsersController - createUser - try add user.");
            usersService.addUser(user);
            logger.info("UsersController - createUser - user added to DB successfully.");

        } catch (IllegalArgumentException e) {  // username or password isn't valid
            logger.info("UsersController - createUser - IllegalArgumentException EXCEPTION.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);

        } catch (IllegalStateException e) {
            logger.info("UsersController - createUser - IllegalStateException EXCEPTION.");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists", e);

        } catch (Exception e) {
            logger.info("UsersController - createUser - Exception EXCEPTION.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating user", e);
        }
    }

    @PostMapping("/login")
    public Users login(@RequestParam String username, @RequestParam String password) {
        logger.info("UsersController - login - executed.");
        try {
            logger.info("UsersController - login - try login action.");
            return usersService.loginAction(username, password);

        } catch (IllegalStateException e) {   // user already logged in
            logger.info("UsersController - login - IllegalStateException EXCEPTION.");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already logged in", e);

        } catch (IllegalArgumentException e) {  // user not found or valid username + invalid password
            String message = e.getMessage();
            logger.info("UsersController - login - IllegalArgumentException EXCEPTION: " + message);
            if (message.equals("User not found.")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, message, e);
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message, e);
            }
        }
    }


    @DeleteMapping("/delete")
    public void deleteUser(@RequestParam String username) {
        logger.info("UsersController - deleteUser - executed.");
        try {
            logger.info("UsersController - deleteUser - executed.");
            usersService.deleteAction(username);
            logger.info("UsersController - deleteUser - deleteAction executed successfully.");

        } catch (IllegalArgumentException e) {  // username not found in DB
            logger.info("UsersController - deleteUser - IllegalArgumentException EXCEPTION.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);

        } catch (Exception e) {
            logger.info("UsersController - deleteUser - Exception EXCEPTION.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while deleting the user", e);
        }
    }

    @PostMapping("/logout")
    public void logoutUser(@RequestParam String username) {
        logger.info("UsersController - logoutUser - executed.");
        try {
            logger.info("UsersController - logoutUser - try logoutUser action.");
            usersService.logoutAction(username);
            logger.info("UsersController - deleteUser - logoutUser executed successfully.");
        } catch (IllegalArgumentException e) {
            logger.info("UsersController - logoutUser - IllegalArgumentException EXCEPTION.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
        } catch (Exception e) {
            logger.info("UsersController - logoutUser - Exception EXCEPTION.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while logging out", e);
        }
    }

}
