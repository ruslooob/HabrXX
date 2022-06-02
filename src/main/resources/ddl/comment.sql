create table public.comment
(
    comment_id       bigint         not null
        constraint pk_comment
            primary key,
    user_id          bigint
        constraint fk_comment_writing_user
            references public._user
            on update cascade on delete set null,
    publication_id   bigint
        constraint fk_comment_consists__publicat
            references public.publication
            on update cascade on delete cascade,
    comment_content  varchar(10000) not null,
    comment_datetime date           not null,
    comment_karma    integer        not null
);

alter table public.comment
    owner to postgres;

create unique index comment_pk
    on public.comment (comment_id);

create index writing_fk
    on public.comment (user_id);

create index consists_under_fk
    on public.comment (publication_id);

