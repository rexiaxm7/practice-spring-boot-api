create table "user"
(
    id serial not null
        constraint user_pk
            primary key,
    name varchar(20) not null,
    team_id integer not null
        constraint user_team__fk
            references team,
    email varchar(100) not null unique ,
    password varchar(255) not null,
    admin boolean not null

);

alter table "user" owner to test;