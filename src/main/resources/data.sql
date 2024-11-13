MERGE INTO mpa (mpa_id, name) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

MERGE INTO genres (genre_id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

MERGE INTO films (film_id, name, description, release_date, duration) VALUES
(1, 'Тестовый фильм 1', 'Описание тестового фильма 1', '2023-01-01', 120),
(2, 'Тестовый фильм 2', 'Описание тестового фильма 2', '2023-02-01', 110);

MERGE INTO users (user_id, email, login, name, birthday) VALUES
(1, 'user1@test.com', 'user1', 'Пользователь 1', '1990-01-01'),
(2, 'user2@test.com', 'user2', 'Пользователь 2', '1995-05-05');

MERGE INTO film_genres (film_id, genre_id) VALUES
(1, 1),
(1, 2),
(2, 3);

MERGE INTO film_mpa (film_id, mpa_id) VALUES
(1, 1),
(2, 2);

MERGE INTO likes (film_id, user_id) VALUES
(1, 1),
(1, 2),
(2, 1);

MERGE INTO friendships (user_id, friend_id) VALUES
(1, 2);