package pl.task.personalities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.task.personalities.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
