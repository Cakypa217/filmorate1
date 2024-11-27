# Схема базы данных Filmorate

Схема базы данных для приложения Filmorate разработана для эффективного хранения и управления информацией о фильмах, пользователях и их взаимодействиях. Она включает в себя следующие основные компоненты:

1. **Таблица Films**: Хранит информацию о фильмах, включая название, описание, дату выпуска и продолжительность.
2. **Таблица Users**: Хранит данные пользователей, такие как email, логин, имя и дата рождения.
3. **Таблица Genres** и **Связующая таблица Film Genres**: Реализует отношение многие-ко-многим между фильмами и жанрами.
4. **Таблица MPA**: Хранит рейтинги MPA, связанная с таблицей фильмов через таблицу `film_mpa`.
5. **Таблица Likes**: Отслеживает лайки пользователей к фильмам.
6. **Таблица Friendships**: Управляет отношениями дружбы между пользователями.
7. **Таблица Reviews**: Хранит информацию об отзывах к фильмам, включая пользователя, который написал, описание, тип отзыва(позитивный/негативный) и полезность отзыва.
8. **Таблица Useful**: Хранит оценки пользователей к отзывам, благодаря оценкам рассчитывается полезный ли отзыв или нет.

## Примеры SQL-запросов для основных операций приложения

### Получение всех фильмов
```sql
SELECT * FROM films;
Copy
Insert

Получение всех пользователей
SELECT * FROM users;
Copy
Insert

Получение топ N наиболее популярных фильмов
SELECT f.*, COUNT(l.user_id) AS likes_count
FROM films f
LEFT JOIN likes l ON f.film_id = l.film_id
GROUP BY f.film_id
ORDER BY likes_count DESC
LIMIT N;
Copy
Insert

Получение списка общих друзей с другим пользователем
SELECT friend_id
FROM friendships
WHERE user_id = ? AND friend_id IN (SELECT friend_id FROM friendships WHERE user_id = ?);
Copy
Insert

Добавление лайка фильму
INSERT INTO likes (film_id, user_id)
VALUES (:film_id, :user_id);
Copy
Insert

Добавление друга
INSERT INTO friendships (user_id, friend_id)
VALUES (:user_id, :friend_id);

![ER-диаграмма базы данных](./diagrams/er_diagram.png)
[Ссылка на онлайн-версию ER-диаграммы](https://dbdiagram.io/d/671bbd6d97a66db9a34a32d1)