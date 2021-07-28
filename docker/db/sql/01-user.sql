create table "user"
(
    id serial not null
        constraint user_pk
            primary key,
    name varchar(20) not null,
    team_id integer not null
        constraint user_team__fk
            references team
);

alter table "user" owner to test;