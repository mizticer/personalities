package pl.task.personalities.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Student extends Person {
    @NotBlank(message = "Current position is required")
    private String universityName;
    @Min(value = 1, message = "Year of study must be a positive number")
    private int yearStudy;
    @NotBlank(message = "Field of study is required")
    private String fieldOfStudy;
    @DecimalMin(value = "0.0", message = "Scholarship amount must be a positive number")
    private double scholarshipAmount;

    public Student(String firstName, String lastName, String pesel, int height, double weight, String emailAddress, String universityName, int yearStudy, String fieldOfStudy, double scholarshipAmount) {
        super(firstName, lastName, pesel, height, weight, emailAddress);
        this.universityName = universityName;
        this.yearStudy = yearStudy;
        this.fieldOfStudy = fieldOfStudy;
        this.scholarshipAmount = scholarshipAmount;
    }
}
