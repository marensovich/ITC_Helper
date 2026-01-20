package me.marensovich.itsKipfin.database.repositories;

import me.marensovich.itsKipfin.database.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для управления сущностями {@link Application}.
 * <p>
 * Используется для взаимодействия с базой данных и проверки состояния заявок.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * Проверяет, существует ли заявка с указанным ID и статусом.
     *
     * @param userId идентификатор заявки
     * @param status статус заявки {@link Application.Status}
     * @return {@code true}, если такая заявка существует, иначе {@code false}
     * @author marensovich
     * @since 0.0.1
     */
    boolean existsApplicationByUserIdAndStatus(Long userId, Application.Status status);

    void deleteApplicationByUserId(Long userId);

    Application findApplicationByUserId(Long userId);
}
