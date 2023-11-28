package pl.task.personalities.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.task.personalities.common.creators.PersonFactory;
import pl.task.personalities.exceptions.EntityNotFoundException;
import pl.task.personalities.exceptions.PositionDateException;
import pl.task.personalities.model.Employee;
import pl.task.personalities.model.Position;
import pl.task.personalities.model.dto.request.PositionRequest;
import pl.task.personalities.model.dto.response.PersonResponse;
import pl.task.personalities.repository.PersonRepository;
import pl.task.personalities.repository.PositionRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;
    private final PersonRepository personRepository;
    private final PersonFactory personFactory;

    @Transactional
    public PersonResponse addPosition(Long employeeId, PositionRequest positionRequest) {
        Employee employee = (Employee) personRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Employee doesnt exists"));
        validatePositionDates(employee.getPositions(), positionRequest);
        Position position = new Position(positionRequest.getName(), positionRequest.getStartDate(), positionRequest.getEndDate(), positionRequest.getSalary(), employee);
        positionRepository.saveAndFlush(position);
        PersonResponse personResponse = personFactory.createResponse(employee);
        return personResponse;
    }

    @Transactional(readOnly = true)
    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    private void validatePositionDates(List<Position> existingPositions, PositionRequest newPosition) {
        LocalDate newStartDate = newPosition.getStartDate();
        LocalDate newEndDate = newPosition.getEndDate();
        if (newStartDate != null && newEndDate != null && newStartDate.isAfter(newEndDate)) {
            throw new PositionDateException("Start date must be before end date");
        }
        for (Position existingPosition : existingPositions) {
            LocalDate existingStartDate = existingPosition.getStartDate();
            LocalDate existingEndDate = existingPosition.getEndDate();
            if (isDateRangeOverlap(existingStartDate, existingEndDate, newStartDate, newEndDate)) {
                throw new PositionDateException("Date range overlap detected");
            }
        }
    }

    private boolean isDateRangeOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1 != null && end1 != null && start2 != null && end2 != null &&
                (start1.isBefore(end2) || start1.isEqual(end2)) &&
                (start2.isBefore(end1) || start2.isEqual(end1));
    }

}


