package me.marensovich.itsKipfin.database.repositories;

import me.marensovich.itsKipfin.database.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
}