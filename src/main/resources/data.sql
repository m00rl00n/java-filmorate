INSERT INTO mpa (id, name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');
MERGE INTO genres
USING (VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик')) AS new_values (id, name)
ON (genres.id = new_values.id)
WHEN MATCHED THEN UPDATE SET genres.name = new_values.name
WHEN NOT MATCHED THEN INSERT (id, name) VALUES (new_values.id, new_values.name);

INSERT INTO operations(id, name)
VALUES (1, 'REMOVE'),
    (2, 'ADD'),
    (3, 'UPDATE');

INSERT INTO event_types(id, name)
VALUES (1, 'LIKE'),
    (2, 'REVIEW'),
    (3, 'FRIEND');