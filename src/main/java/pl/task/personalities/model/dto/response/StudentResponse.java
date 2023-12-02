package pl.task.personalities.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse implements PersonResponse {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private int version;
    private String firstName;
    private String lastName;
    private Integer height;
    private Double weight;
    private String emailAddress;
    private String universityName;
    private Integer yearStudy;
    private String fieldOfStudy;
    private Double scholarshipAmount;
}
