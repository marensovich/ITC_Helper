package me.marensovich.itsKipfin.database.repositories;

import me.marensovich.itsKipfin.database.models.Applications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationsRepository extends JpaRepository<Applications, Long> {
}