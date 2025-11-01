package me.marensovich.itsKipfin.services;

import me.marensovich.itsKipfin.database.models.User;
import me.marensovich.itsKipfin.database.repositories.UserRepository;
import org.springframework.stereotype.Service;

/**
 * The type User service.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Instantiates a new User service.
     *
     * @param userRepository the user repository
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create user user.
     *
     * @param userId the user id
     * @return the user
     */
    public User createUser(Long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setAdmin(false);

        return userRepository.save(user);
    }

    /**
     * Create user user.
     *
     * @param userId the user id
     * @return the user
     */
    public User createUser(String userId) {
        User user = new User();
        user.setUserId(Long.valueOf(userId));
        user.setAdmin(false);

        return userRepository.save(user);
    }

    /**
     * Is user exists boolean.
     *
     * @param userId the user id
     * @return the boolean
     */
    public boolean isUserExists(Long userId) {
        return userRepository.existsById(userId);
    }

    /**
     * Is user admin boolean.
     *
     * @param user the user
     * @return the boolean
     */
    public boolean isUserAdmin(User user) {
        return user != null && user.isAdmin();
    }

    /**
     * Is user admin boolean.
     *
     * @param userId the user id
     * @return the boolean
     */
    public boolean isUserAdmin(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.isAdmin();
    }

}
