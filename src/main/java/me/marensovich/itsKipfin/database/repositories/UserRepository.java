package me.marensovich.itsKipfin.database.repositories;

import me.marensovich.itsKipfin.database.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
