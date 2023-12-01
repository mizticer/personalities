package pl.task.personalities.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @Column(unique = true)
    //@NotBlank(message = "PESEL is required")
    private String pesel;
    @NotNull(message = "Gender is required")
    private String gender;
    @Min(value = 0, message = "Height must be a positive number")
    @NotNull(message = "Height is required")
    private Integer height;
    @DecimalMin(value = "0.0", message = "Weight must be a positive number")
    private Double weight;
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String emailAddress;

    private LocalDate currentEmploymentStartDate;

    private String currentPosition;

    private Double currentSalary;
    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Position> positions = new ArrayList<>();

    public Employee(String firstName, String lastName, String pesel, String gender, Integer height, Double weight, String emailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.emailAddress = emailAddress;
    }

}
