package pl.task.personalities.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Pensioner extends Person {
    @DecimalMin(value = "0.0", message = "Pension amount must be a positive number")
    private double amountOfPension;
    @Positive(message = "Years worked must be a positive number")
    private int yearsWorked;
    public Pensioner(String firstName, String lastName, String pesel, Integer height, double weight, String emailAddress, double amountOfPension, int yearsWorked) {
        super(firstName, lastName, pesel, height, weight, emailAddress);
        this.amountOfPension = amountOfPension;
        this.yearsWorked = yearsWorked;
    }
}
