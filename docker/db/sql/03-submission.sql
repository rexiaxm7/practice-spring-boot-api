create table submission
(
    id      serial  not null
        constraint submission_pk
            primary key,
    team_id integer not null
        constraint submission_team_id_fk
            references team,
    year    integer not null,
    month   integer not null
);

alter table submission
    owner to test;

create unique index submission_id
    on submission (id);
