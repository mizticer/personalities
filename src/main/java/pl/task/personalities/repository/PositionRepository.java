package pl.task.personalities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.task.personalities.model.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
