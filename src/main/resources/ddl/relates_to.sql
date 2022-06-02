create table public.relates_to
(
    genre_id       bigint not null
        constraint fk_relates__relates_t_genre
            references public.genre
            on update cascade on delete cascade,
    publication_id bigint not null
        constraint fk_relates__relates_t_publicat
            references public.publication
            on update cascade on delete cascade,
    constraint pk_relates_to
        primary key (genre_id, publication_id)
);

alter table public.relates_to
    owner to postgres;

create unique index relates_to_pk
    on public.relates_to (genre_id, publication_id);

create index relates_to2_fk
    on public.relates_to (publication_id);

create index relates_to_fk
    on public.relates_to (genre_id);

