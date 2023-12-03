package pl.task.personalities.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.task.personalities.model.security.ERole;
import pl.task.personalities.model.security.Role;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
