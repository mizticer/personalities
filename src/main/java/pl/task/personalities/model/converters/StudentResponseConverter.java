package pl.task.personalities.model.converters;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import pl.task.personalities.model.Student;
import pl.task.personalities.model.dto.response.StudentResponse;

public class StudentResponseConverter implements Converter<Student, StudentResponse> {
    @Override
    public StudentResponse convert(MappingContext<Student, StudentResponse> mappingContext) {
        Student student = mappingContext.getSource();
        return StudentResponse.builder()
                .id(student.getId())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .version(student.getVersion())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .height(student.getHeight())
                .weight(student.getWeight())
                .emailAddress(student.getEmailAddress())
                .universityName(student.getUniversityName())
                .yearStudy(student.getYearStudy())
                .fieldOfStudy(student.getFieldOfStudy())
                .scholarshipAmount(student.getScholarshipAmount())
                .build();
    }
}
