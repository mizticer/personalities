package pl.task.personalities.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.task.personalities.common.components.ImportProgressHolder;
import pl.task.personalities.common.creators.PersonFactory;
import pl.task.personalities.exceptions.EntityNotFoundException;
import pl.task.personalities.exceptions.ModificationException;
import pl.task.personalities.model.ImportProgress;
import pl.task.personalities.model.Person;
import pl.task.personalities.model.dto.request.PersonEditRequest;
import pl.task.personalities.model.dto.request.PersonQuery;
import pl.task.personalities.model.dto.request.PersonRequest;
import pl.task.personalities.model.dto.response.PersonResponse;
import pl.task.personalities.repository.PersonRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PersonService extends GenericService<Person, PersonRepository> {
    private final PersonFactory personFactory;
    private final Lock uploadLock = new ReentrantLock();
    @PersistenceContext
    EntityManager entityManager;
    private PersonRepository personRepository;
    private ImportProgressHolder importProgressHolder;
    private boolean isUploadInProgress = false;
    private UUID actualIdUploadProgress;

    public PersonService(PersonRepository repository, PersonFactory personFactory, PersonRepository personRepository, ImportProgressHolder importProgressHolder) {
        super(repository);
        this.personFactory = personFactory;
        this.personRepository = personRepository;
        this.importProgressHolder = importProgressHolder;
    }

    @Transactional
    public PersonResponse addPerson(PersonRequest personRequest) {
        Person person = personFactory.createPerson(personRequest);
        person.setCreatedAt(LocalDateTime.now());
        Person addedPerson = repository.save(person);
        PersonResponse personResponse = personFactory.createResponse(addedPerson);
        return personResponse;
    }

    public List<PersonResponse> findPersonByParameters(PersonQuery params, int page, int size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> query = builder.createQuery(Person.class);
        Root<Person> root = query.from(Person.class);
        List<Predicate> predicates = new ArrayList<>();
        if (params.getType() != null) {
            String type = params.getType().toLowerCase(Locale.ROOT);
            predicates.add(builder.equal(builder.lower(root.type().as(String.class)), type));
        }
        if (params.getFirstName() != null) {
            predicates.add(builder.like(builder.upper(root.get("firstName")), "%" + params.getFirstName().toUpperCase(Locale.ROOT) + "%"));
        }

        if (params.getLastName() != null) {
            predicates.add(builder.like(builder.upper(root.get("lastName")), "%" + params.getLastName().toUpperCase(Locale.ROOT) + "%"));
        }

        if (params.getPesel() != null) {
            predicates.add(builder.equal(root.get("pesel"), params.getPesel()));
        }
        if (params.getHeight() != null) {
            predicates.add(builder.equal(root.get("height"), params.getHeight()));
        }
        if (params.getHeightFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("height"), params.getHeightFrom()));
        }

        if (params.getHeightTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("height"), params.getHeightTo()));
        }

        if (params.getWeightFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("weight"), params.getWeightFrom()));
        }

        if (params.getWeightTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("weight"), params.getWeightTo()));
        }
        if (params.getWeight() != null) {
            predicates.add(builder.equal(root.get("weight"), params.getWeight()));
        }
        if (params.getEmailAddress() != null) {
            predicates.add(builder.like(builder.upper(root.get("emailAddress")), "%" + params.getEmailAddress().toUpperCase(Locale.ROOT) + "%"));
        }
        if (params.getSalaryFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("currentSalary"), params.getSalaryFrom()));
        }

        if (params.getSalaryTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("currentSalary"), params.getSalaryTo()));
        }

        if (params.getEmploymentStartDateFrom() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(params.getEmploymentStartDateFrom(), formatter);
            LocalDateTime employmentStartDateFrom = date.atStartOfDay();
            predicates.add(builder.greaterThanOrEqualTo(root.get("currentEmploymentStartDate"), employmentStartDateFrom));
        }

        if (params.getEmploymentStartDateTo() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(params.getEmploymentStartDateTo(), formatter);
            LocalDateTime employmentStartDateTo = date.atStartOfDay();
            predicates.add(builder.lessThanOrEqualTo(root.get("currentEmploymentStartDate"), employmentStartDateTo));
        }

        if (params.getPosition() != null) {
            predicates.add(builder.like(builder.upper(root.get("currentPosition")), "%" + params.getPosition().toUpperCase(Locale.ROOT) + "%"));
        }
        if (params.getUniversityName() != null) {
            predicates.add(builder.like(builder.upper(root.get("universityName")), "%" + params.getUniversityName().toUpperCase(Locale.ROOT) + "%"));
        }

        if (params.getYearStudy() != null) {
            predicates.add(builder.equal(root.get("yearStudy"), params.getYearStudy()));
        }

        if (params.getFieldOfStudy() != null) {
            predicates.add(builder.like(builder.upper(root.get("fieldOfStudy")), "%" + params.getFieldOfStudy().toUpperCase(Locale.ROOT) + "%"));
        }

        if (params.getScholarshipAmountFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("scholarshipAmount"), params.getScholarshipAmountFrom()));
        }

        if (params.getScholarshipAmountTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("scholarshipAmount"), params.getScholarshipAmountTo()));
        }
        if (params.getAmountOfPensionFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("amountOfPension"), params.getAmountOfPensionFrom()));
        }

        if (params.getAmountOfPensionTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("amountOfPension"), params.getAmountOfPensionTo()));
        }

        if (params.getYearsWorkedFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("yearsWorked"), params.getYearsWorkedFrom()));
        }

        if (params.getYearsWorkedTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("yearsWorked"), params.getYearsWorkedTo()));
        }
        if (predicates.size() != 0) {
            query.where(predicates.toArray(new Predicate[predicates.size()]));
        }
        TypedQuery<Person> typedQuery = entityManager.createQuery(query);
        if (params.getGender() != null) {
            List<Person> filteredGenderPersons = typedQuery.getResultList()
                    .stream()
                    .filter(person -> {
                        String pesel = person.getPesel();
                        int genderNumber = Integer.parseInt(pesel.substring(9, 10));
                        String gender = (genderNumber % 2 == 0) ? "Female" : "Male";
                        return gender.equalsIgnoreCase(params.getGender());
                    })
                    .collect(Collectors.toList());
            typedQuery = entityManager.createQuery(query.where(root.in(filteredGenderPersons)));

        }
        if (params.getAge() != null) {
            List<Person> filteredAgePersons = typedQuery.getResultList()
                    .stream()
                    .filter(person -> {
                        String pesel = person.getPesel();
                        LocalDate birthDate = getBirthDateFromPesel(pesel);
                        int age = Period.between(birthDate, LocalDate.now()).getYears();
                        return age == params.getAge();
                    })
                    .collect(Collectors.toList());
            typedQuery = entityManager.createQuery(query.where(root.in(filteredAgePersons)));
        }
        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);
        List<PersonResponse> personList = typedQuery.getResultList().stream().map(personFactory::createResponse).collect(Collectors.toList());

        return personList;
    }

    @Transactional
    public PersonResponse edit(Long id, PersonEditRequest personEditRequest) throws NoSuchFieldException, IllegalAccessException {
        Person personToEdit = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Person not found"));
        if (personToEdit.getVersion() != personEditRequest.getVersion()) {
            throw new ModificationException("Person has been modified by another transaction. Please refresh and try again.");
        }

        Class<?> currentClass = personToEdit.getClass();
        do {
            for (Field field : currentClass.getDeclaredFields()) {
                String fieldName = field.getName();
                if (personEditRequest.getUpdates().containsKey(fieldName)) {
                    field.setAccessible(true);
                    field.set(personToEdit, personEditRequest.getUpdates().get(fieldName));
                }
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null);

        personToEdit.setVersion(personToEdit.getVersion() + 1);
        personToEdit.setUpdatedAt(LocalDateTime.now());
        PersonResponse editedPerson = personFactory.createResponse(repository.saveAndFlush(personToEdit));
        return editedPerson;
    }

    public String addPersonFromCsv(MultipartFile file) {
        if (isUploadInProgress) {
            return "Upload is already in progress. Try again later. Check actual progress Check progress at /import-progress/" + actualIdUploadProgress;
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
            return "Import persons from csv started. Check progress at /import-progress/" + importProgress.getId();
        } catch (Exception e) {
            isUploadInProgress = false;
            uploadLock.unlock();
            throw e;
        }
    }

    @Async
    public void processCsvFile(MultipartFile file, ImportProgress importProgress) throws IOException {

        Stream<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream())).lines();
        lines.map(line -> line.split(","))
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
        fields.put("height", Integer.parseInt(args[4]));
        fields.put("weight", Double.parseDouble(args[5]));
        fields.put("emailAddress", args[6]);

        switch (personRequest.getTypeOfPerson()) {
            case "EMPLOYEE":
                break;
            case "PENSIONER":
                fields.put("amountOfPension", Double.parseDouble(args[7]));
                fields.put("yearsWorked", Integer.parseInt(args[8]));
                break;
            case "STUDENT":
                fields.put("universityName", args[7]);
                fields.put("yearStudy", Integer.parseInt(args[8]));
                fields.put("fieldOfStudy", args[9]);
                fields.put("scholarshipAmount", Double.parseDouble(args[10]));
                break;
            default:
                throw new IllegalArgumentException("Invalid person type: " + personRequest.getTypeOfPerson());
        }

        personRequest.setFields(fields);

        return personRequest;
    }

    private LocalDate getBirthDateFromPesel(String pesel) {
        int year = Integer.parseInt(pesel.substring(0, 2));
        int month = Integer.parseInt(pesel.substring(2, 4));
        int day = Integer.parseInt(pesel.substring(4, 6));

        if (month > 0 && month < 13) {
            year += 1900;
        } else if (month > 20 && month < 33) {
            year += 2000;
            month -= 20;
        } else if (month > 40 && month < 53) {
            year += 2100;
            month -= 40;
        }

        return LocalDate.of(year, month, day);
    }

}
