create table public.genre
(
    genre_id   bigint      not null
        constraint pk_genre
            primary key,
    genre_name varchar(20) not null
);

alter table public.genre
    owner to postgres;

create unique index genre_pk
    on public.genre (genre_id);

create unique index genre_name_idx
    on public.genre (genre_name);

