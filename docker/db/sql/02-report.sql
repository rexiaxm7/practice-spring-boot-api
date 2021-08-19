create table report
(
    id      serial               not null
        constraint report_pk
            primary key,
    user_id integer              not null
        constraint report_user__fk
            references user,
    year    integer  not null,
    month   integer  not null,
    content varchar
);

alter table report
    owner to test;
