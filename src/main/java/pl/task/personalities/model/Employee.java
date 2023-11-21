package pl.task.personalities.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Employee extends Person {
    @NotNull(message = "Employment start date is required")
    @Past(message = "Employment start date must be in the past")
    private LocalDate employmentStartDate;
    @NotBlank(message = "Current position is required")
    private String position;
    @DecimalMin(value = "0.0", message = "Current salary must be a positive number")
    private double salary;

    public Employee(LocalDate employmentStartDate, String position, double salary) {
        this.employmentStartDate = employmentStartDate;
        this.position = position;
        this.salary = salary;
    }
}
