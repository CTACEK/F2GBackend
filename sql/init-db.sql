create table avatars
(
    id    serial primary key,
    image text not null
);
insert into avatars (image)
values ('images/avatar1.jpg');

create table users
(
    user_id       text primary key,
    username      text not null,
    password_hash text,
    auth_provider text not null default 'local',
    avatar        int references avatars (id)
);

create table refresh_tokens
(
    id         serial primary key,
    user_id    text   not null references users (user_id) on delete cascade,
    client_id  text   not null,
    token      text   not null unique,
    expires_at bigint not null
);

create table rooms
(
    id             text primary key,
    name           text                                              not null,
    short_name     text                                              not null,
    owner_id       text references users (user_id) on delete cascade not null,
    date           date,
    game_started   boolean default false
);

create table room_members
(
    room_id   text references rooms (id) on delete cascade not null,
    user_id   text references users (user_id)              not null,
);