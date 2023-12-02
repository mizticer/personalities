package pl.task.personalities.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionResponse {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double salary;
}
