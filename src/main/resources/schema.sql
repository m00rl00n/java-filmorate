
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS film_genre CASCADE;
DROP TABLE IF EXISTS directors CASCADE;
DROP TABLE IF EXISTS film_director CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS reviews_evaluation CASCADE;
DROP TABLE IF EXISTS user_events CASCADE;
DROP TABLE IF EXISTS operations CASCADE;
DROP TABLE IF EXISTS event_types CASCADE;

CREATE TABLE IF NOT EXISTS Mpa (
 id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
 id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  description VARCHAR(200),
  release_date DATE NOT NULL,
  duration INTEGER NOT NULL,
  mpa_id INTEGER REFERENCES Mpa(id),
    CHECK(duration > 0)
);

CREATE TABLE IF NOT EXISTS users (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email VARCHAR(64) NOT NULL,
  login VARCHAR(64) NOT NULL,
  name VARCHAR(64) NOT NULL,
  birthday DATE
  CHECK(birthday <= CAST(now() AS DATE))
);

CREATE TABLE IF NOT EXISTS friends (
  id_user INTEGER REFERENCES users(id) NOT NULL,
  friend_id INTEGER REFERENCES users(id) NOT NULL,
UNIQUE(id_user, friend_id)
);

CREATE TABLE IF NOT EXISTS film_genre (
  id_films INTEGER REFERENCES films(id) NOT NULL,
  id_genre INTEGER REFERENCES genres(id) NOT NULL,
  UNIQUE(id_films,  id_genre)
);

CREATE TABLE IF NOT EXISTS likes (
  id_films INTEGER REFERENCES films(id) NOT NULL,
  id_user INTEGER REFERENCES users(id) NOT NULL,
  UNIQUE(id_films, id_user)
);

CREATE TABLE IF NOT EXISTS directors (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_director (
    id_film INTEGER REFERENCES films(id) ON DELETE CASCADE NOT NULL,
    id_director INTEGER REFERENCES directors(id) ON DELETE CASCADE NOT NULL,
    UNIQUE(id_film, id_director)
);

CREATE TABLE IF NOT EXISTS reviews (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR(100),
    is_positive BOOLEAN NOT NULL,
    id_user INTEGER REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    id_film INTEGER REFERENCES films(id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews_evaluation(
    id_review INTEGER REFERENCES reviews(id) ON DELETE CASCADE NOT NULL,
    id_user INTEGER REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    evaluation INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS event_types(
    id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS operations(
    id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_events(
    id              INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_date      TIMESTAMP NOT NULL,
    user_id         INTEGER NOT NULL REFERENCES users(id),
    entity_id       INTEGER NOT NULL,
    event_type_id   INTEGER REFERENCES event_types(id),
    operation_id    INTEGER REFERENCES operations(id)
);
