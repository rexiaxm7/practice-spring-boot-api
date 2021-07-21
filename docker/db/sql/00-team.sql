create table team
(
    id serial not null
        constraint team_pk
            primary key,
    name varchar(20) not null
);

alter table team owner to test;

