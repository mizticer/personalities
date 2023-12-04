package pl.task.personalities.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.task.personalities.exceptions.EntityNotFoundException;
import pl.task.personalities.exceptions.PositionDateException;
import pl.task.personalities.model.Employee;
import pl.task.personalities.model.Position;
import pl.task.personalities.model.dto.request.PositionRequest;
import pl.task.personalities.model.dto.response.PositionResponse;
import pl.task.personalities.repository.PersonRepository;
import pl.task.personalities.repository.PositionRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;
    private final PersonRepository personRepository;

    @Transactional
    public PositionResponse addPosition(Long employeeId, PositionRequest positionRequest) {
        Employee employee = (Employee) personRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Employee doesnt exists"));
        validatePositionDates(employee.getPositions(), positionRequest);
        Position position = new Position(positionRequest.getName(), positionRequest.getStartDate(), positionRequest.getEndDate(), positionRequest.getSalary(), employee);
        Position positionAdded = positionRepository.saveAndFlush(position);
        PositionResponse positionResponse = PositionResponse.builder()
                .id(positionAdded.getId())
                .salary(positionAdded.getSalary())
                .name(positionAdded.getName())
                .startDate(positionAdded.getStartDate())
                .endDate(positionAdded.getEndDate())
                .build();
        return positionResponse;
    }

    @Transactional
    public PositionResponse editPosition(Long positionId, PositionRequest positionRequest) {
        Position positionToEdit = positionRepository.findById(positionId)
                .orElseThrow(() -> new EntityNotFoundException("Position not found"));

        Employee employee = positionToEdit.getEmployee();

        validateEditPositionDates(positionToEdit.getEmployee().getPositions(), positionRequest);
        boolean wasCurrentPosition = positionToEdit.getEndDate() == null;
        positionToEdit.setName(positionRequest.getName());
        positionToEdit.setStartDate(positionRequest.getStartDate());
        positionToEdit.setEndDate(positionRequest.getEndDate());
        positionToEdit.setSalary(positionRequest.getSalary());

        Position editedPosition = positionRepository.saveAndFlush(positionToEdit);

        if (wasCurrentPosition && (positionRequest.getEndDate() != null || editedPosition.getEndDate() != null)) {
            employee.setCurrentPosition(null);
            employee.setCurrentEmploymentStartDate(null);
            employee.setCurrentSalary(null);
            personRepository.saveAndFlush(employee);
        }

        if (positionRequest.getEndDate() == null && employee.getCurrentPosition() == null) {
            employee.setCurrentPosition(positionRequest.getName());
            employee.setCurrentEmploymentStartDate(positionRequest.getStartDate());
            employee.setCurrentSalary(positionRequest.getSalary());
            personRepository.saveAndFlush(employee);
        } else {
            throw new PositionDateException("Edited position overlaps with existing current position");
        }

        PositionResponse positionResponse = PositionResponse.builder()
                .id(editedPosition.getId())
                .salary(editedPosition.getSalary())
                .name(editedPosition.getName())
                .startDate(editedPosition.getStartDate())
                .endDate(editedPosition.getEndDate())
                .build();
        return positionResponse;
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

            if (existingEndDate == null && isDateRangeOverlap(existingStartDate, LocalDate.now(), newStartDate, newEndDate)) {
                throw new PositionDateException("New position overlaps with current employment");
            }

            if (newEndDate == null && existingEndDate == null) {
                throw new PositionDateException("There is already an active position");
            }
            if (isDateRangeOverlap(existingStartDate, existingEndDate, newStartDate, newEndDate)) {
                throw new PositionDateException("New position overlaps with existing position");
            }
            if (isDateRangeOverlap(newStartDate, newEndDate, existingStartDate, existingEndDate)) {
                throw new PositionDateException("Existing position overlaps with new position");
            }
            if (newEndDate == null && existingEndDate == null && isDateRangeOverlap(existingStartDate, LocalDate.now(), newStartDate, LocalDate.now())) {
                throw new PositionDateException("New position overlaps with existing current position");
            }
            if (newEndDate == null && existingEndDate == null && isDateRangeOverlap(newStartDate, LocalDate.now(), existingStartDate, LocalDate.now())) {
                throw new PositionDateException("New position overlaps with existing current position");
            }
            if (newEndDate == null && existingEndDate != null && isDateRangeOverlap(newStartDate, LocalDate.now(), existingStartDate, existingEndDate)) {
                throw new PositionDateException("New position overlaps with existing position in the given period");
            }
        }
    }

    private boolean isDateRangeOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1 != null && end1 != null && start2 != null && end2 != null &&
                (start1.isBefore(end2) || start1.isEqual(end2)) &&
                (start2.isBefore(end1) || start2.isEqual(end1));
    }

    private void validateEditPositionDates(List<Position> existingPositions, PositionRequest newPosition) {
        LocalDate newStartDate = newPosition.getStartDate();
        LocalDate newEndDate = newPosition.getEndDate();

        for (Position existingPosition : existingPositions) {
            LocalDate existingStartDate = existingPosition.getStartDate();
            LocalDate existingEndDate = existingPosition.getEndDate();
            if (isDateRangeOverlap(existingStartDate, existingEndDate, newStartDate, newEndDate)) {
                throw new PositionDateException("Edited position overlaps with existing positions");
            }

        }
    }
}


