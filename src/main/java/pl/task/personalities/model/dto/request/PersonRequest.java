package pl.task.personalities.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class PersonRequest {
    @NotNull
    @NotBlank(message = "Type of person is required")
    private String typeOfPerson;

    private Map<String, Object> fields;
}
