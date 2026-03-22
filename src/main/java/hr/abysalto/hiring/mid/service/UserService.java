package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.request.RegisterRequest;
import hr.abysalto.hiring.mid.dto.response.UserResponse;
import hr.abysalto.hiring.mid.model.User;

public interface UserService {

    /**
     * Registers a new user.
     *
     * @param request the registration details (username, email, password)
     * @return the persisted {@link User} entity
     * @throws hr.abysalto.hiring.mid.exception.UsernameAlreadyTakenException if the username is already taken
     * @throws hr.abysalto.hiring.mid.exception.EmailAlreadyInUseException if the email is already in use
     */
    User register(RegisterRequest request);

    /**
     * Retrieves a user by username.
     *
     * @param username the username to look up
     * @return the matching {@link User} entity
     * @throws hr.abysalto.hiring.mid.exception.UserNotFoundException if no user with the given username exists
     */
    User getUser(String username);

    /**
     * Returns a response DTO for the currently authenticated user.
     *
     * @param username the authenticated user's username
     * @return a {@link UserResponse} with the user's profile data
     */
    UserResponse getCurrentUser(String username);
}