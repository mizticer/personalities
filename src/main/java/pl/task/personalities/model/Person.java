package pl.task.personalities.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.task.personalities.common.Identification;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class Person implements Serializable, Identification {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Version
    private int version;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @Column(unique = true)
    @NotBlank(message = "PESEL is required")
    @Size(min = 11, max = 11, message = "PESEL must be exactly 11 characters long")
    @Pattern(regexp = "\\d+", message = "PESEL must consist only of digits")
    private String pesel;
    @Min(value = 0, message = "Height must be a positive number")
    private Integer height;
    @DecimalMin(value = "0.0", message = "Weight must be a positive number")
    private Double weight;
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String emailAddress;

    public Person(String firstName, String lastName, String pesel, Integer height, Double weight, String emailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.height = height;
        this.weight = weight;
        this.emailAddress = emailAddress;
    }
}
