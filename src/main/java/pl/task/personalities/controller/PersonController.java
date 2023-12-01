package pl.task.personalities.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.task.personalities.common.components.ImportProgressHolder;
import pl.task.personalities.common.creators.PersonFactory;
import pl.task.personalities.model.ImportProgress;
import pl.task.personalities.model.dto.request.PersonEditRequest;
import pl.task.personalities.model.dto.request.PersonQuery;
import pl.task.personalities.model.dto.request.PersonRequest;
import pl.task.personalities.model.dto.response.PersonResponse;
import pl.task.personalities.repository.PersonRepository;
import pl.task.personalities.service.PersonService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;
    private final ImportProgressHolder importProgressHolder;
    private final PersonRepository personRepository;
    private final PersonFactory personFactory;
    private final Lock uploadLock = new ReentrantLock();
    private boolean isUploadInProgress = false;
    private UUID actualIdUploadProgress;

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

    @PostMapping("/upload")
    public ResponseEntity<String> addPersonFromCsv(@RequestParam("file") MultipartFile file) {
        if (isUploadInProgress) {
            return new ResponseEntity<>("Upload is already in progress. Try again later. Check actual progress Check progress at /import-progress/" + actualIdUploadProgress, HttpStatus.CONFLICT);
        }
        uploadLock.lock();
        try {
            isUploadInProgress = true;
            ImportProgress importProgress = importProgressHolder.createImportProgress();
            importProgress.setStartDate(LocalDateTime.now());
            CompletableFuture.runAsync(() -> {
                try {
                    processCsvFile(file, importProgress);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    uploadLock.unlock();
                }
            });
            actualIdUploadProgress = UUID.fromString(importProgress.getId());
            return new ResponseEntity<>("Import persons from csv started. Check progress at /import-progress/" + importProgress.getId(), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            isUploadInProgress = false;
            uploadLock.unlock();
            throw e;
        }
    }

    @Async
    public void processCsvFile(MultipartFile file, ImportProgress importProgress) throws IOException {

        Stream<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream())).lines();
        lines.map(line -> line.split(";"))
                .forEach(args -> {
                    PersonRequest personRequest = createPersonRequestFromCsv(args);
                    personRepository.save(personFactory.createPerson(personRequest));
                    importProgress.incrementProcessedRows();
                });
        importProgress.setFinished(true);
        importProgress.setEndDate(LocalDateTime.now());

    }


    private PersonRequest createPersonRequestFromCsv(String[] args) {
        PersonRequest personRequest = new PersonRequest();
        personRequest.setTypeOfPerson(args[0].toUpperCase());

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", args[1]);
        fields.put("lastName", args[2]);
        fields.put("pesel", args[3]);
        fields.put("gender", args[4]);
        fields.put("height", Integer.parseInt(args[5]));
        fields.put("weight", Double.parseDouble(args[6]));
        fields.put("emailAddress", args[7]);

        switch (personRequest.getTypeOfPerson()) {
            case "EMPLOYEE":
                break;
            case "PENSIONER":
                fields.put("amountOfPension", Double.parseDouble(args[8]));
                fields.put("yearsWorked", Integer.parseInt(args[9]));
                break;
            case "STUDENT":
                fields.put("universityName", args[8]);
                fields.put("yearStudy", Integer.parseInt(args[9]));
                fields.put("fieldOfStudy", args[10]);
                fields.put("scholarshipAmount", Double.parseDouble(args[11]));
                break;
            default:
                throw new IllegalArgumentException("Invalid person type: " + personRequest.getTypeOfPerson());
        }

        personRequest.setFields(fields);

        return personRequest;
    }

    @GetMapping("/import-progress/{taskId}")
    public ResponseEntity<ImportProgress> getImportProgress(@PathVariable String taskId) {
        ImportProgress importProgress = importProgressHolder.getImportProgress(taskId);
        return importProgress != null
                ? new ResponseEntity<>(importProgress, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

