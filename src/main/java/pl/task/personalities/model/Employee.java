package pl.task.personalities.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Employee extends Person {
    private LocalDate currentEmploymentStartDate;
    private String currentPosition;
    @PositiveOrZero(message = "Salary must be a non-negative number")
    private Double currentSalary;
    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Position> positions = new ArrayList<>();

    public Employee(String firstName, String lastName, String pesel, Integer height, Double weight, String emailAddress) {
        super(firstName, lastName, pesel, height, weight, emailAddress);
    }

}
