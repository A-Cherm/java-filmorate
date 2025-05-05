CREATE TABLE "films" (
  "film_id" integer PRIMARY KEY,
  "name" varchar,
  "description" varchar,
  "release_date" date,
  "duration" integer,
  "MPA_rating_id" integer
);

CREATE TABLE "films_likes" (
  "id" integer PRIMARY KEY,
  "film_id" integer,
  "user_id" integer
);

CREATE TABLE "films_genres" (
  "id" integer PRIMARY KEY,
  "film_id" integer,
  "genre_id" integer
);

CREATE TABLE "genres" (
  "genre_id" integer PRIMARY KEY,
  "genre" varchar
);

CREATE TABLE "MPA_ratings" (
  "rating_id" integer PRIMARY KEY,
  "name" varchar
);

CREATE TABLE "users" (
  "user_id" integer PRIMARY KEY,
  "email" varchar,
  "login" varchar,
  "name" varchar,
  "birthday" date
);

CREATE TABLE "users_friends" (
  "id" integer PRIMARY KEY,
  "user_id" integer,
  "friend_id" integer,
  "status" bool
);

ALTER TABLE "films_genres" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "films_likes" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "films_genres" ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("genre_id");

ALTER TABLE "films_likes" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "films" ADD FOREIGN KEY ("MPA_rating_id") REFERENCES "MPA_ratings" ("rating_id");

ALTER TABLE "users_friends" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "users_friends" ADD FOREIGN KEY ("friend_id") REFERENCES "users" ("user_id");
