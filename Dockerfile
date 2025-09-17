# pg-sandbox/Dockerfile
FROM postgres:16-alpine

# Значения по умолчанию (их всё равно можно переопределить в compose)
ENV POSTGRES_DB=training_db \
    POSTGRES_USER=app \
    POSTGRES_PASSWORD=app123

# Авто-инициализация БД при первом старте контейнера
COPY initdb/ /docker-entrypoint-initdb.d/