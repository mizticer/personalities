package pl.task.personalities.common.creators;

import pl.task.personalities.exceptions.InvalidRequestException;
import pl.task.personalities.model.Person;
import pl.task.personalities.model.dto.response.PersonResponse;

import java.time.LocalDate;
import java.util.Map;

public interface PersonCreator {

    String getTypeOfPerson();

    Person create(Map<String, Object> fields);

    PersonResponse createResponse(Person person);

    default String getStringField(Map<String, Object> fields, String fieldName) {
        return getField(fields, fieldName, String.class);
    }

    default Double getDoubleField(Map<String, Object> fields, String fieldName) {
        return getField(fields, fieldName, Double.class);
    }

    default Integer getIntegerField(Map<String, Object> fields, String fieldName) {
        return getField(fields, fieldName, Integer.class);
    }

    default LocalDate getDateField(Map<String, Object> fields, String fieldName) {
        String dateAsString = getField(fields, fieldName, String.class);
        return LocalDate.parse(dateAsString);
    }

    default <T> T getField(Map<String, Object> fields, String fieldName, Class<T> fieldType) {
        if (!fields.containsKey(fieldName)) {
            throw new InvalidRequestException("Bad request! Field '" + fieldName + "' is required and cannot be null.");
        }
        Object value = fields.get(fieldName);
        if (value == null) {
            throw new InvalidRequestException("Bad request! Field '" + fieldName + "' cannot be null.");
        }
        if (!fieldType.isInstance(value)) {
            throw new InvalidRequestException("Bad request! Field '" + fieldName + "' must be of type " + fieldType.getSimpleName() + ".");
        }
        return fieldType.cast(value);
    }
}