package pl.task.personalities.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PositionEditRequest {
    @NotEmpty(message = "Position name is required")
    private String name;
    @NotEmpty(message = "Start date is required")
    private LocalDate startDate;
    private LocalDate endDate;
    @DecimalMin(value = "0.0", message = "Salary must be a positive number")
    private Double salary;
}
