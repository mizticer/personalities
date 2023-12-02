package pl.task.personalities.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Pensioner extends Person {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @Column(unique = true)
    @NotBlank(message = "PESEL is required")
    @Size(min = 11, max = 11, message = "PESEL must be exactly 11 characters long")
    @Pattern(regexp = "\\d+", message = "PESEL must consist only of digits")
    private String pesel;
    @NotBlank(message = "Gender is required")
    private String gender;
    @Min(value = 0, message = "Height must be a positive number")
    private Integer height;
    @DecimalMin(value = "0.0", message = "Weight must be a positive number")
    private Double weight;
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String emailAddress;
    @DecimalMin(value = "0.0", message = "Pension amount must be a positive number")
    private Double amountOfPension;
    @PositiveOrZero(message = "Years worked must be a non-negative number")
    private Integer yearsWorked;

    public Pensioner(String firstName, String lastName, String pesel, String gender, Integer height, Double weight, String emailAddress, Double amountOfPension, Integer yearsWorked) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.emailAddress = emailAddress;
        this.amountOfPension = amountOfPension;
        this.yearsWorked = yearsWorked;
    }
}
