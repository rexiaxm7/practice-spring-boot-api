create table team
(
    id               serial             not null
        constraint team_pk
            primary key,
    name             varchar(20)        not null,
    input_start_date integer default 20 not null,
    alert_start_days integer default 25 not null
);

alter table team
    owner to test;

