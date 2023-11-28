package pl.task.personalities.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.task.personalities.model.dto.request.PersonEditRequest;
import pl.task.personalities.model.dto.request.PersonQuery;
import pl.task.personalities.model.dto.request.PersonRequest;
import pl.task.personalities.model.dto.response.PersonResponse;
import pl.task.personalities.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonResponse>> getPersonByParameters(PersonQuery personQuery, @RequestParam(name = "page", defaultValue = "0") int page,
                                                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        List<PersonResponse> personResponseList = personService.findPersonByParameters(personQuery, page, size);
        return new ResponseEntity<>(personResponseList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PersonResponse> createPerson(@RequestBody @Valid PersonRequest personRequest) {
        PersonResponse personResponse = personService.addPerson(personRequest);
        return new ResponseEntity<>(personResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> updatePerson(@PathVariable Long id, @RequestBody PersonEditRequest personEditRequest) throws NoSuchFieldException, IllegalAccessException {
        PersonResponse editedPerson = personService.edit(id, personEditRequest);
        return new ResponseEntity<>(editedPerson, HttpStatus.OK);
    }

}