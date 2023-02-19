create table publication
(
    publication_id          bigserial                            not null primary key,

    user_id                 bigserial references _user (user_id) not null,
    publication_views_count integer                                       default 0,
    publication_header      varchar(100)                         not null,
    publication_content     varchar(100000)                      not null,
    publication_datetime    timestamp                            not null default now(),
    publication_karma       integer                              not null default 0

);

alter table publication
    add column publication_preview_image_path varchar(200)