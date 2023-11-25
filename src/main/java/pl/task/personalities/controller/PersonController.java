package pl.task.personalities.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.task.personalities.common.creators.PersonFactory;
import pl.task.personalities.model.Person;
import pl.task.personalities.model.request.PersonRequest;
import pl.task.personalities.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;
    private final PersonFactory personFactory;

    @GetMapping
    public ResponseEntity<List<Person>> getAllPerson() {
        List<Person> personResponseList = personService.findAll();
        return new ResponseEntity<>(personResponseList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody @Valid PersonRequest personRequest) {
        Person person = personFactory.createPerson(personRequest);
        person = personService.add(person);
        return new ResponseEntity<>(person, HttpStatus.CREATED);
    }
}