package pl.task.personalities.common.creators;

import org.springframework.stereotype.Service;
import pl.task.personalities.exceptions.InvalidRequestException;
import pl.task.personalities.model.Person;
import pl.task.personalities.model.dto.request.PersonRequest;
import pl.task.personalities.model.dto.response.PersonResponse;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PersonFactory {
    private final Map<String, PersonCreator> creators;

    public PersonFactory(Set<PersonCreator> creators) {
        this.creators = creators.stream().collect(Collectors.toMap(PersonCreator::getTypeOfPerson, Function.identity()));
    }

    public Person createPerson(PersonRequest personRequest) {
        if (personRequest.getFields() == null) {
            throw new InvalidRequestException("Bad request! fields are missing!");
        }
        if (!creators.containsKey(personRequest.getTypeOfPerson())) {
            throw new InvalidRequestException("Bad request! Type of person are missing!");
        }
        return creators.get(personRequest.getTypeOfPerson()).create(personRequest.getFields());
    }

    public PersonResponse createResponse(Person person) {
        return creators.get(person.getClass().getSimpleName().toUpperCase(Locale.ROOT)).createResponse(person);
    }
}