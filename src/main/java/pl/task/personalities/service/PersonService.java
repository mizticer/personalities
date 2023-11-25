package pl.task.personalities.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.task.personalities.model.Person;
import pl.task.personalities.repository.PersonRepository;

import java.util.List;

@Service
public class PersonService extends GenericService<Person, PersonRepository> {

    public PersonService(PersonRepository repository) {
        super(repository);
    }

    //Test
    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return repository.findAll();
    }

}
