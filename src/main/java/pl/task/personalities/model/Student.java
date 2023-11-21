package pl.task.personalities.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Student extends Person {
    private String universityName;
    private int yearStudy;
    private String fieldOfStudy;
    private double scholarshipAmount;

    public Student(String universityName, int yearStudy, String fieldOfStudy, double scholarshipAmount) {
        this.universityName = universityName;
        this.yearStudy = yearStudy;
        this.fieldOfStudy = fieldOfStudy;
        this.scholarshipAmount = scholarshipAmount;
    }
}
