package pl.task.personalities.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference
    private Employee employee;
    @NotBlank(message = "Position name is required")
    private String name;
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    private LocalDate endDate;
    @DecimalMin(value = "0.0", message = "Salary must be a positive number")
    private Double salary;

    public Position(String name, LocalDate startDate, LocalDate endDate, Double salary, Employee employee) {
        this.employee = employee;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
        this.employee.getPositions().add(this);
        this.employee.setCurrentPosition(name);
        this.employee.setCurrentEmploymentStartDate(startDate);
        this.employee.setCurrentSalary(salary);
    }

}
