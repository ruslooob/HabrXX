create table public.marked
(
    publication_id bigint not null
        constraint fk_marked_marked_publicat
            references public.publication
            on update cascade on delete cascade,
    tag_id         bigint not null
        constraint fk_marked_marked2_tag
            references public.tag
            on update cascade on delete cascade,
    constraint pk_marked
        primary key (publication_id, tag_id)
);

alter table public.marked
    owner to postgres;

create unique index marked_pk
    on public.marked (publication_id, tag_id);

create index marked2_fk
    on public.marked (tag_id);

create index marked_fk
    on public.marked (publication_id);

