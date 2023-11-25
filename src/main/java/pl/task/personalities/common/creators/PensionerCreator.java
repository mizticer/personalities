package pl.task.personalities.common.creators;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.task.personalities.model.Pensioner;
import pl.task.personalities.model.Person;
import pl.task.personalities.model.response.PensionerResponse;
import pl.task.personalities.model.response.PersonResponse;

import java.util.Map;

@Service
@AllArgsConstructor
public class PensionerCreator implements PersonCreator {
    private final ModelMapper mapper;

    @Override
    public String getTypeOfPerson() {
        return "Pensioner";
    }

    @Override
    public Person create(Map<String, Object> fields) {
        return new Pensioner(getStringField(fields, "firstName"), getStringField(fields, "lastName"), getStringField(fields, "pesel"),
                getIntegerField(fields, "height"), getDoubleField(fields, "weight"), getStringField(fields, "emailAddress"), getDoubleField(fields, "amountOfPension"), getIntegerField(fields, "yearsWorked"));
    }

    @Override
    public PersonResponse createResponse(Person person) {
        return mapper.map(person, PensionerResponse.class);
    }
}
