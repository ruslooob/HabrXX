create table public.tag
(
    tag_id   bigint      not null
        constraint pk_tag
            primary key,
    tag_name varchar(20) not null
);

alter table public.tag
    owner to postgres;

create unique index tag_pk
    on public.tag (tag_id);

create index tag_name_idx
    on public.tag (tag_name);

