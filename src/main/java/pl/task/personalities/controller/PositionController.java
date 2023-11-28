package pl.task.personalities.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.task.personalities.model.Employee;
import pl.task.personalities.model.Position;
import pl.task.personalities.model.dto.request.PositionRequest;
import pl.task.personalities.model.dto.response.PersonResponse;
import pl.task.personalities.service.PositionService;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService positionService;

    @PostMapping("/{employeeId}/position")
    public ResponseEntity addPositionToEmployee(@PathVariable Long employeeId,
                                                @RequestBody PositionRequest positionRequest) {
        PersonResponse employee = positionService.addPosition(employeeId, positionRequest);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity findAll() {
       List<Position> list = positionService.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
