package pl.task.personalities.model.dto.request;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonQuery {
    private String type;
    private String firstName;
    private String lastName;
    private Integer age;
    private String pesel;
    private String gender;
    private Integer heightFrom;
    private Integer heightTo;
    private Integer height;
    private Double weightFrom;
    private Double weightTo;
    private Double weight;
    private String emailAddress;
    private Double salaryFrom;
    private Double salaryTo;
    private String universityName;
    private Integer yearStudy;
    private String fieldOfStudy;
    private Double scholarshipAmountFrom;
    private Double scholarshipAmountTo;
    private Double amountOfPensionFrom;
    private Double amountOfPensionTo;
    private Integer yearsWorkedFrom;
    private Integer yearsWorkedTo;
    private String employmentStartDateFrom;
    private String employmentStartDateTo;
    private String position;
}
