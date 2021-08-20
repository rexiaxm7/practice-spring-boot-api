create table team
(
    id serial not null constraint team_pk primary key,
    name                varchar(20)        not null,
    input_start_date    integer not null,
    alert_start_days    integer not null,
    sending_message_url varchar
);

alter table team
    owner to test;

create unique index user_team__fk
    on team (id);