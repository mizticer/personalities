package pl.task.personalities.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
@Data
public class PositionRequest {
    @NotBlank(message = "Position name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @DecimalMin(value = "0.0", message = "Salary must be a positive number")
    private Double salary;
}
