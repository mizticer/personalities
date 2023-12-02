package pl.task.personalities.common.creators;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.task.personalities.model.Person;
import pl.task.personalities.model.Student;
import pl.task.personalities.model.dto.response.PersonResponse;
import pl.task.personalities.model.dto.response.StudentResponse;

import java.util.Map;

@Service
@AllArgsConstructor
public class StudentCreator implements PersonCreator {
    private final ModelMapper mapper;

    @Override
    public String getTypeOfPerson() {
        return "STUDENT";
    }

    @Override
    public Person create(Map<String, Object> fields) {
        return new Student(getStringField(fields, "firstName"), getStringField(fields, "lastName"), getStringField(fields, "pesel"),
                getIntegerField(fields, "height"), getDoubleField(fields, "weight"),
                getStringField(fields, "emailAddress"), getStringField(fields, "universityName"), getIntegerField(fields, "yearStudy"),
                getStringField(fields, "fieldOfStudy"), getDoubleField(fields, "scholarshipAmount"));
    }

    @Override
    public PersonResponse createResponse(Person person) {
        return mapper.map(person, StudentResponse.class);
    }
}
