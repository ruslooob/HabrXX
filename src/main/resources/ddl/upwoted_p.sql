create table public.upwoted_p
(
    publication_id bigint not null
        constraint fk_upwoted__upwoted_p_publicat
            references public.publication
            on update cascade on delete cascade,
    user_id        bigint not null
        constraint fk_upwoted__upwoted_p_user
            references public._user
            on update cascade on delete cascade,
    constraint pk_upwoted_p
        primary key (publication_id, user_id)
);

alter table public.upwoted_p
    owner to postgres;

create unique index upwoted_p_pk
    on public.upwoted_p (publication_id, user_id);

create index upwoted_3_fk
    on public.upwoted_p (user_id);

create index upwoted_1_fk
    on public.upwoted_p (publication_id);