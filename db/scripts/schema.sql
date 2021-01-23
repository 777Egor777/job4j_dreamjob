create table if not exists user(
    id serial primary key,
    name text,
    email text,
    password text
);
create table if not exists photo(
    id serial primary key,
    name text
);
create table if not exists post (
    id serial primary key,
    name text
);
create table if not exists candidate (
    id serial primary key,
    name text,
    photo_id int references photo(id),
    city_id int
);