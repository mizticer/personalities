package pl.task.personalities.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.task.personalities.model.dto.request.PositionRequest;
import pl.task.personalities.model.dto.response.PositionResponse;
import pl.task.personalities.service.PositionService;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService positionService;

    @PostMapping("/{employeeId}/position")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity addPositionToEmployee(@PathVariable Long employeeId,
                                                @RequestBody PositionRequest positionRequest) {
        PositionResponse position = positionService.addPosition(employeeId, positionRequest);
        return new ResponseEntity<>(position, HttpStatus.CREATED);
    }

    @PutMapping("/position/{positionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<PositionResponse> editPosition(@PathVariable Long positionId,
                                                         @RequestBody PositionRequest positionRequest) {
        PositionResponse editedPosition = positionService.editPosition(positionId, positionRequest);
        return new ResponseEntity<>(editedPosition, HttpStatus.OK);
    }

}
