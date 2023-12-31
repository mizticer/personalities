package pl.task.personalities.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DiscriminatorValue("Pensioner")
public class Pensioner extends Person {
    @DecimalMin(value = "0.0", message = "Pension amount must be a positive number")
    private Double amountOfPension;
    @PositiveOrZero(message = "Years worked must be a non-negative number")
    private Integer yearsWorked;

    public Pensioner(String typeOfPerson, String firstName, String lastName, String pesel, Integer height, Double weight, String emailAddress, Double amountOfPension, Integer yearsWorked) {
        super(typeOfPerson, firstName, lastName, pesel, height, weight, emailAddress);
        this.amountOfPension = amountOfPension;
        this.yearsWorked = yearsWorked;
    }
}
