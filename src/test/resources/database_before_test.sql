INSERT INTO roles(name)
VALUES ('ROLE_ADMIN');
INSERT INTO roles(name)
VALUES ('ROLE_IMPORTER');
INSERT INTO roles(name)
VALUES ('ROLE_EMPLOYEE');
INSERT INTO roles(name)
VALUES ('ROLE_USER');

INSERT INTO Pensioner (id, created_at, updated_at, version, type_of_person, first_name, last_name, pesel, height,
                       weight,
                       email_address, amount_of_pension, years_worked)
VALUES (3, '2023-12-03 23:58:56.147539', '2023-12-03 23:58:56.147539', 0, 'Pensioner', 'Sylwek', 'Doe', '99022401765',
        190, 95.5, 'Sylwek.doe@example.com', 2300.0, 5),
       (4, '2023-12-03 23:58:56.147539', '2023-12-03 23:58:56.147539', 0, 'Pensioner', 'John', 'Doe', '99022401264',
        180, 85.5, 'john.doe@example.com', 2000.0, 10),
       (5, '2023-12-03 23:58:56.147539', '2023-12-03 23:58:56.147539', 0, 'Pensioner', 'Andrew', 'Smith', '99022401751',
        170, 75.0, 'andrew.smith@example.com', 1800.0, 8);

INSERT INTO Student (id, created_at, updated_at, version, type_of_person, first_name, last_name, pesel, height, weight,
                     email_address, university_name, year_study, field_of_study, scholarship_amount)
VALUES (6, '2023-12-03 23:58:56.147539', '2023-12-03 23:58:56.147539', 0, 'Student', 'Helen', 'Maily', '99022401261',
        170, 65.5, 'helen.maily@example.com', 'Warsaw', 3, 'IT', 1233.5),
       (7, '2023-12-03 23:58:56.147539', '2023-12-03 23:58:56.147539', 0, 'Student', 'Michael', 'Johnson',
        '99022401333',
        175, 70.0, 'michael.johnson@example.com', 'Krakow', 2, 'Business', 1500.0),
       (8, '2023-12-03 23:58:56.147539', '2023-12-03 23:58:56.147539', 0, 'Student', 'Sophie', 'White', '99022401444',
        160, 55.0, 'sophie.white@example.com', 'Gdansk', 4, 'Mathematics', 2000.0);

INSERT INTO Employee (id, created_at, updated_at, version, type_of_person, first_name, last_name, pesel, height, weight,
                      email_address)
VALUES (9, '2023-12-03 23:58:56.147539', '2023-12-03 23:58:56.147539', 0, 'Employee', 'Robert', 'Smith', '12345678901',
        170, 85.5, 'Robert.Smith@example.com'),
       (10, '2023-12-03 23:58:56.147539', '2023-12-03 23:58:56.147539', 0, 'Employee', 'Alice', 'Brown', '12345678902',
        165, 60.0, 'alice.brown@example.com'),
       (11, '2023-12-03 23:58:56.147539', '2023-12-03 23:58:56.147539', 0, 'Employee', 'James', 'Taylor', '12345678903',
        180, 75.0, 'james.taylor@example.com');
