create database habr;

---
create table public._user
(
    user_id        bigserial    not null
        constraint pk_user
            primary key,
    user_full_name varchar(50),
    user_email     varchar(50)  not null,
    user_login     varchar(20)  not null,
    user_karma     integer      not null default 0,
    user_password  varchar(100) not null
);

alter table _user
    owner to postgres;

create unique index user_pk
    on _user (user_id);

create unique index user_login_idx
    on _user (user_login);

create table admin
(
    id      bigserial primary key,
    user_id bigint references _user (user_id) not null
);

---
create table publication
(
    publication_id          bigserial                            not null primary key,

    user_id                 bigserial references _user (user_id) not null,
    publication_views_count integer                                       default 0,
    publication_header      varchar(100)                         not null,
    publication_content     varchar(100000)                      not null,
    publication_datetime    timestamp                            not null default now(),
    publication_karma       integer                              not null default 0,
    publication_preview_image_path varchar(200)
);

---
create table genre
(
    genre_id   bigint      not null
        constraint pk_genre
            primary key,
    genre_name varchar(20) not null
);

alter table genre
    owner to postgres;

create unique index genre_pk
    on genre (genre_id);

create unique index genre_name_idx
    on genre (genre_name);

---
create table tag
(
    tag_id   bigint      not null
        constraint pk_tag
            primary key,
    tag_name varchar(20) not null
);

alter table tag
    owner to postgres;

create unique index tag_pk
    on tag (tag_id);

create index tag_name_idx
    on tag (tag_name);

---
create table relates_to
(
    genre_id       bigint not null
        constraint fk_relates__relates_t_genre
            references genre
            on update cascade on delete cascade,
    publication_id bigint not null
        constraint fk_relates__relates_t_publicat
            references publication
            on update cascade on delete cascade,
    constraint pk_relates_to
        primary key (genre_id, publication_id)
);

alter table relates_to
    owner to postgres;

create unique index relates_to_pk
    on relates_to (genre_id, publication_id);

create index relates_to2_fk
    on relates_to (publication_id);

create index relates_to_fk
    on relates_to (genre_id);

---
create table marked
(
    publication_id bigint not null
        constraint fk_marked_marked_publicat
            references publication
            on update cascade on delete cascade,
    tag_id         bigint not null
        constraint fk_marked_marked2_tag
            references tag
            on update cascade on delete cascade,
    constraint pk_marked
        primary key (publication_id, tag_id)
);

alter table marked
    owner to postgres;

create unique index marked_pk
    on marked (publication_id, tag_id);

create index marked2_fk
    on marked (tag_id);

create index marked_fk
    on marked (publication_id);

---
create table comment
(
    comment_id       bigserial
        constraint pk_comment
            primary key,
    user_id          bigint
        constraint fk_comment_writing_user
            references _user
            on update cascade on delete set null,
    publication_id   bigint
        constraint fk_comment_consists__publicat
            references publication
            on update cascade on delete cascade,
    comment_content  varchar(10000)        not null,
    comment_datetime date    default now() not null,
    comment_karma    integer default 0     not null
);

alter table comment
    owner to postgres;

create unique index comment_pk
    on comment (comment_id);

create index writing_fk
    on comment (user_id);

create index consists_under_fk
    on comment (publication_id);

---
create table upwoted_p
(
    publication_id bigint not null
        constraint fk_upwoted__upwoted_p_publicat
            references publication
            on update cascade on delete cascade,
    user_id        bigint not null
        constraint fk_upwoted__upwoted_p_user
            references _user
            on update cascade on delete cascade,
    constraint pk_upwoted_p
        primary key (publication_id, user_id)
);

alter table upwoted_p
    owner to postgres;

create unique index upwoted_p_pk
    on upwoted_p (publication_id, user_id);

create index upwoted_3_fk
    on upwoted_p (user_id);

create index upwoted_1_fk
    on upwoted_p (publication_id);

---
create table upwoted_c
(
    comment_id bigint not null
        constraint fk_upwoted__upwoted_c_comment
            references comment
            on update cascade on delete cascade,
    user_id    bigint not null
        constraint fk_upwoted__upwoted_c_user
            references _user
            on update cascade on delete cascade,
    constraint pk_upwoted_k
        primary key (comment_id, user_id)
);

alter table upwoted_c
    owner to postgres;

create unique index upwoted_k_pk
    on upwoted_c (comment_id, user_id);

create index upwoted_k_fk
    on upwoted_c (comment_id);

---
create procedure delete_publication(id bigint)
    language plpgsql
as
$$
DECLARE
    cmnt_id     bigint;
    -- id комментариев, принадлежащей этой публикации
    comment_ids bigint[];
BEGIN
    comment_ids := ARRAY(SELECT comment_id FROM comment WHERE publication_id = id);
    -- удалить все лайки с комментариев
    FOREACH cmnt_id IN ARRAY comment_ids
        LOOP
            DELETE FROM upwoted_c uc WHERE uc.comment_id = cmnt_id;
        END LOOP;
    -- удалить все комментарии под публикацией
    DELETE FROM comment WHERE publication_id = id;
    -- удалить все лайки с публикации
    DELETE FROM upwoted_p WHERE publication_id = id;
    -- удалить саму публикацию
    DELETE FROM publication WHERE publication_id = id;
END;
$$;

---
CREATE FUNCTION check_pub_karma_less_views()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF NEW.publication_karma > NEW.publication_views_count THEN
        RAISE EXCEPTION 'Карма не может быть больше количества просмотров!';
    END IF;

    RETURN NEW;
END;
$$;

---
CREATE TRIGGER pub_karma_less_views_check
    AFTER INSERT OR UPDATE
    ON publication
    FOR EACH ROW
EXECUTE PROCEDURE check_pub_karma_less_views();

----
insert into genre (genre_id, genre_name)
values  (1, 'Наука'),
        (2, 'Научпоп'),
        (3, 'Технологии'),
        (4, 'Математика'),
        (6, 'Политика');

insert into tag (tag_id, tag_name)
values  (1, 'Программирование'),
        (2, 'Тестирование'),
        (3, 'Quality Assurance'),
        (4, 'Дизайн'),
        (5, 'Маркетинг');