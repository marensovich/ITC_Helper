package me.marensovich.itsKipfin.database.repositories;

import me.marensovich.itsKipfin.database.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью {@link User}.
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository}.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
