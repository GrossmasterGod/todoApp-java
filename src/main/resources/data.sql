INSERT INTO users(id, username, first_name, last_name, registration_date, last_update, password, email) VALUES
        (4, 'test', 'test', 'test', '2021-05-03 00:00:00','2021-05-03 00:00:00', '$2a$10$UL0oHA21nLWCrUknMKamR.sstth5KgFA6qukhoNbRH4pKXPlLYDXO', 'test@gmail.com'),
        (5, 'test1', 'test', 'test', '2021-05-03 00:00:00','2021-05-03 00:00:00', '$2a$10$UL0oHA21nLWCrUknMKamR.sstth5KgFA6qukhoNbRH4pKXPlLYDXO', 'test5@gmail.com'),
        (6, 'nazar', 'test', 'test', '2021-05-03 00:00:00','2021-05-03 00:00:00', '$2a$10$UL0oHA21nLWCrUknMKamR.sstth5KgFA6qukhoNbRH4pKXPlLYDXO', 'nazar@gmail.com');

INSERT INTO project(id, name)
VALUES(1, 'Java Project'),
       (2, 'SQL Project'),
       (3, 'Angular project');

INSERT INTO task(id, title, description, create_date, finish_date, active, project_id, priority)
VALUES (1, 'Create ToDoApp', 'First description', '2021-05-03 00:00:00', '2021-05-03 00:00:00', 0, 1, 1),
       (2, 'Learn Angular', 'Second description', '2021-05-03 00:00:00', '2021-05-03 00:00:00', 0, 2, 2),
       (3, 'Go for a walk', 'Third description', '2021-05-03 00:00:00', '2021-05-03 00:00:00', 1, 3, 3),
       (4, 'Buy chocolate', 'Fourth description', '2021-05-03 00:00:00', '2021-05-03 00:00:00', 1, 1, 2);

INSERT INTO task(id,title, create_date,  active, project_id, priority)
VALUES (5, 'Buy pepsi', '2021-05-03 00:00:00', 1, 1, 2);


INSERT INTO user_project(user_id, project_id)
VALUES (4, 1),
       (4, 3),
       (4, 2);


