drop table if exists users, items, booking, comments, requests;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL primary key,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL primary key,
    description varchar NOT NULL,
    requestor_id  BIGINT references users(id) on delete cascade,
    created TIMESTAMP WITHOUT TIME ZONE  NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL primary key,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    available BOOL NOT NULL,
    user_id BIGINT references users(id) on delete cascade,
    request_id BIGINT references requests(id) on delete cascade
);

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL primary key,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT references items(id) on delete cascade,
    booker_id BIGINT references users(id) on delete cascade,
    status varchar(10)
);

CREATE TABLE IF NOT EXISTS comments (
	id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL primary key,
	text varchar NOT NULL,
	item_id BIGINT references items(id) on delete cascade,
	author_id bigint references users(id) on delete cascade,
	created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);



