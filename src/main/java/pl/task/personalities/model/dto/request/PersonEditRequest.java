package pl.task.personalities.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonEditRequest {
    private Map<String, Object> updates;
    private int version;
}
