create table public.user
(
    user_id        bigserial      not null
        constraint pk_user
            primary key,
    user_full_name varchar(50) not null,
    user_email     varchar(50) not null,
    user_login     varchar(20) not null,
    user_karma     integer     not null
);

alter table public._user
    owner to postgres;

create unique index user_pk
    on public._user (user_id);

create unique index user_login_idx
    on public._user (user_login);

