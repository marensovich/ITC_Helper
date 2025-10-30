package me.marensovich.itsKipfin.services;

import me.marensovich.itsKipfin.database.models.User;
import me.marensovich.itsKipfin.database.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(Long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setAdmin(false);

        return userRepository.save(user);
    }

    public User createUser(String userId) {
        User user = new User();
        user.setUserId(Long.valueOf(userId));
        user.setAdmin(false);

        return userRepository.save(user);
    }

    public boolean isUserExists(Long userId) {
        return userRepository.existsById(userId);
    }

    public boolean isUserAdmin(User user) {
        return user != null && user.isAdmin();
    }

    public boolean isUserAdmin(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.isAdmin();
    }

}
