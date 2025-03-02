![yet-another-chat](https://github.com/Sapog87/yet-another-chat/actions/workflows/yet-another-chat.yml/badge.svg)

# Многопользовательский чат

Многопользовательский чат для личного и группового общения в реальном времени.

## Технологии

- **Java 17**
- **Maven**
- **Spring Boot 3**
    - **Spring Web**
    - **Spring WebSocket**
    - **Spring Security**
    - **Spring Data JPA**
    - **SpringDoc OpenAPI**
- **RabbitMQ**
- **PostgreSQL**
- **Liquibase**
- **Thymeleaf**
- **Micrometer**
- **Prometheus**
- **Grafana**
- **Docker**

## Функционал

- 🔐 **Регистрация и авторизация пользователей**
- 🔍 **Поиск пользователей**
- 💬 **Отправка личных сообщений**
- 👥 **Создание/поиск групп**
- ➕ **Вступление/выход из группы**
- 📢 **Отправка сообщений в группу**
- 🟢 **Мониторинг онлайн статуса пользователей**

## Примеры работы

### Личный чат

![personal](gif/personal.gif)

### Групповой чат

![group](gif/group.gif)

## Docker

```
docker pull ghcr.io/sapog87/yet-another-chat
```

## Environment Variables

| Переменная            | Описание                            | Пример значений |
|-----------------------|-------------------------------------|-----------------|
| `DB_HOST`             | Хост сервера с базой данных         | `example.com`   |
| `DB_PORT`             | Порт базы данных                    | `5432`          |
| `DB_NAME`             | Имя базы данных                     | `postgres`      |
| `DB_USERNAME`         | Имя пользователя базы данных        | `postgres`      |
| `DB_PASSWORD`         | Пароль пользователя базы данных     | `postgres`      |
| `SERVER_PORT`         | Порт для приложение                 | `8080`          |
| `PROMETHEUS_USERNAME` | Username для подключения Prometheus | `prometheus`    |
| `PROMETHEUS_PASSWORD` | Пароль для подключения Prometheus   | `prometheus`    |
| `RABBITMQ_HOST`       | Хост для подключения к RabbitMQ     | `example.com`   |
| `RABBITMQ_PORT`       | Порт для подключения к RabbitMQ     | `61613`         |
| `RABBITMQ_USERNAME`   | Username для подключения к RabbitMQ | `rabbitmq`      |
| `RABBITMQ_PASSWORD`   | Пароль для подключения к RabbitMQ   | `rabbitmq`      |