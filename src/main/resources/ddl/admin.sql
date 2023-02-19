create table admin
(
    id      bigserial primary key,
    user_id bigint references _user (user_id) not null
)