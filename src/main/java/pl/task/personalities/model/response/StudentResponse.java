package pl.task.personalities.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class StudentResponse implements PersonResponse {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private int version;

    private String firstName;

    private String lastName;
    //raczej zakryc
    private String pesel;

    private int height;

    private double weight;

    private String emailAddress;
    private String universityName;
    private int yearStudy;
    private String fieldOfStudy;
    private double scholarshipAmount;
}
