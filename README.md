# java-explore-with-me

### Описание
Двухмодульное приложение-микросервис для размещения и поиска событий, в котором функционал различается в зависимости
от роли (публичный, авторизованный пользователь или администратор).

Публичный – общедоступен и доступен без регистрации любому пользователю сети. Второй — приватный и доступен только авторизованным пользователям. Третий — административный для администраторов сервисов.

Приложение состоит из основного сервиса, основной БД, сервиса статистики просмотров, БД для статистики.
Каждая часть поднимается в отдельном docker-контейнере.

[Ссылка на финальный PR](https://github.com/glebchik1995/java-explore-with-me/pull/11)

### Стек:
- Java 11 (Core, Collections, Stream)
- Spring Boot
- Hibernate
- PostgreSQL
- Maven
- Lombok, MapStruct
- Postman
- Docker

## Endpoints
- [API основного сервиса](./ewm-main-service-spec.json)
- [API сервиса статистики](./ewm-stats-service-spec.json)
- В качестве дополнительной фичи реализован функционал комментариев.

### Comments
- `[PATCH] /admin/comments/{commentId}` – обновить комментарий `commentId`
- `[DELETE] /admin/comments/{commentId}` – удалить комментарий `commentId`

- `[POST] /users/{userId}/comments?eventId={eventId}` – создать новый комментарий к событию `eventId`
  пользователем `userId`
- `[PATCH] /users/{userId}/comments/{commentId}` – обновить комментарий `commentId` пользователем `userId`
- `[GET] /users/{userId}/comments?from={from}&size={size}` - получить список всех комментариев пользователя `userId`
  с пагинацией
- `[DELETE] /users/{userId}/comments/{commentId}` - удалить комментарий `commentId` пользователем `userId`

- `[GET] /comments?eventId={eventId}&from={from}&size={size}` – получить список всех комментариев к событию `eventId`
  с пагинацией
- `[GET] /comments/{commentId}` – получить комментарий `commentId`