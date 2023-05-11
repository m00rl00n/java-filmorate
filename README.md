# java-filmorate

![Untitled-2](https://github.com/m00rl00n/java-filmorate/assets/119290856/5c64d55f-537e-4d98-b623-a6168afae57d)

Проект базы данных. 

Примеры запросов:

1. Получить список всех фильмов, упорядоченных по дате выхода:
SELECT * 
FROM Film 
ORDER BY releaseDate;

2.Получить список друзей пользователя с определенным id:
SELECT User.* 
FROM User JOIN Friends ON User.id_user = Friends.id_friend 
WHERE Friends.id_user = {user_id};


