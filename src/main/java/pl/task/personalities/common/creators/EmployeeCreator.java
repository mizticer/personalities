package pl.task.personalities.common.creators;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.task.personalities.model.Employee;
import pl.task.personalities.model.Person;
import pl.task.personalities.model.dto.response.EmployeeResponse;
import pl.task.personalities.model.dto.response.PersonResponse;

import java.util.Map;

@Service
@AllArgsConstructor
public class EmployeeCreator implements PersonCreator {
    private final ModelMapper mapper;

    @Override
    public String getTypeOfPerson() {
        return "EMPLOYEE";
    }

    @Override
    public Person create(Map<String, Object> fields) {
        return new Employee(getStringField(fields, "firstName"), getStringField(fields, "lastName"), getStringField(fields, "pesel"), getStringField(fields, "gender"),
                getIntegerField(fields, "height"), getDoubleField(fields, "weight"), getStringField(fields, "emailAddress"));
    }


    @Override
    public PersonResponse createResponse(Person person) {
        return mapper.map(person, EmployeeResponse.class);
    }
}