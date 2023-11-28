package pl.task.personalities.model.converters;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import pl.task.personalities.model.Employee;
import pl.task.personalities.model.dto.response.EmployeeResponse;

public class EmployeeResponseConverter implements Converter<Employee, EmployeeResponse> {
    @Override
    public EmployeeResponse convert(MappingContext<Employee, EmployeeResponse> mappingContext) {
        Employee employee = mappingContext.getSource();
        return EmployeeResponse.builder()
                .id(employee.getId())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .version(employee.getVersion())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .gender(employee.getGender())
                .height(employee.getHeight())
                .weight(employee.getWeight())
                .emailAddress(employee.getEmailAddress())
                .currentPosition(employee.getCurrentPosition())
                .currentSalary(employee.getCurrentSalary())
                .currentEmploymentStartDate(employee.getCurrentEmploymentStartDate())
                .build();
    }
}