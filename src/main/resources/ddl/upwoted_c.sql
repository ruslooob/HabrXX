create table public.upwoted_c
(
    comment_id bigint not null
        constraint fk_upwoted__upwoted_c_comment
            references public.comment
            on update cascade on delete cascade,
    user_id    bigint not null
        constraint fk_upwoted__upwoted_c_user
            references public._user
            on update cascade on delete cascade,
    constraint pk_upwoted_k
        primary key (comment_id, user_id)
);

alter table public.upwoted_c
    owner to postgres;

create unique index upwoted_k_pk
    on public.upwoted_c (comment_id, user_id);

create index upwoted_k_fk
    on public.upwoted_c (comment_id);

