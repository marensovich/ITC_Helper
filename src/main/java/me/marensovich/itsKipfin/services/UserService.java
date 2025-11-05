package me.marensovich.itsKipfin.services;

import me.marensovich.itsKipfin.database.models.User;
import me.marensovich.itsKipfin.database.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления пользователями.
 * <p>
 * Отвечает за создание пользователей, проверку существования и определение прав администратора.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Конструктор сервиса пользователей.
     *
     * @param userRepository репозиторий для работы с сущностями {@link User}
     * @author marensovich
     * @since 0.0.1
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Создаёт нового пользователя по его ID.
     *
     * @param userId ID пользователя
     * @return созданный объект {@link User}
     * @since 0.0.1
     * @author marensovich
     */
    public User createUser(Long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setAdmin(false);
        return userRepository.save(user);
    }

    /**
     * Создаёт нового пользователя по строковому ID.
     *
     * @param userId строковый идентификатор пользователя
     * @return созданный объект {@link User}
     * @since 0.0.1
     * @author marensovich
     */
    public User createUser(String userId) {
        User user = new User();
        user.setUserId(Long.valueOf(userId));
        user.setAdmin(false);
        return userRepository.save(user);
    }

    /**
     * Проверяет, существует ли пользователь.
     *
     * @param userId ID пользователя
     * @return {@code true}, если пользователь существует, иначе {@code false}
     * @since 0.0.1
     * @author marensovich
     */
    public boolean isUserExists(Long userId) {
        return userRepository.existsById(userId);
    }

    /**
     * Возвращает всех пользователей
     * @since 0.0.1
     * @author marensovich
     * @return Список всех пользователей
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Проверяет, является ли пользователь администратором.
     *
     * @param user объект пользователя
     * @return {@code true}, если пользователь — администратор, иначе {@code false}
     * @since 0.0.1
     * @author marensovich
     */
    public boolean isUserAdmin(User user) {
        return user != null && user.isAdmin();
    }

    /**
     * Проверяет, является ли пользователь администратором по ID.
     *
     * @param userId ID пользователя
     * @return {@code true}, если пользователь — администратор, иначе {@code false}
     * @since 0.0.1
     * @author marensovich
     */
    public boolean isUserAdmin(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.isAdmin();
    }

}
