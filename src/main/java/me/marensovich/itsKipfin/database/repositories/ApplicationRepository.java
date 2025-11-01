package me.marensovich.itsKipfin.database.repositories;

import me.marensovich.itsKipfin.database.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Application repository.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    /**
     * Exists by id and status boolean.
     *
     * @param id     the id
     * @param status the status
     * @return the boolean
     */
    boolean existsByIdAndStatus(Long id, Application.Status status);
}