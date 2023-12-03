package pl.task.personalities.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.task.personalities.common.components.ImportProgressHolder;
import pl.task.personalities.model.ImportProgress;
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
    private final ImportProgressHolder importProgressHolder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PersonResponse>> getPersonByParameters(PersonQuery personQuery, @RequestParam(name = "page", defaultValue = "0") int page,
                                                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        List<PersonResponse> personResponseList = personService.findPersonByParameters(personQuery, page, size);
        return new ResponseEntity<>(personResponseList, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonResponse> createPerson(@RequestBody @Valid PersonRequest personRequest) {
        PersonResponse personResponse = personService.addPerson(personRequest);
        return new ResponseEntity<>(personResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonResponse> updatePerson(@PathVariable Long id, @RequestBody PersonEditRequest personEditRequest) throws NoSuchFieldException, IllegalAccessException {
        PersonResponse editedPerson = personService.edit(id, personEditRequest);
        return new ResponseEntity<>(editedPerson, HttpStatus.OK);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN') or hasRole('IMPORTER')")
    public ResponseEntity<String> addPersonFromCsv(@RequestParam("file") MultipartFile file) {
        String response = personService.addPersonFromCsv(file);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/import-progress/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('IMPORTER')")
    public ResponseEntity<ImportProgress> getImportProgress(@PathVariable String taskId) {
        ImportProgress importProgress = importProgressHolder.getImportProgress(taskId);
        return importProgress != null
                ? new ResponseEntity<>(importProgress, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

