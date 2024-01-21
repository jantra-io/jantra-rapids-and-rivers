CREATE TABLE eventstore
(
    id						        VARCHAR(256),
    origin_river                    varchar(256),
    application_key                 varchar(256),
    event_name                      VARCHAR(50),
    event_time                       TIMESTAMP   NOT NULL DEFAULT now(),
    message							TEXT						NOT NULL
);

create table event_to_river(
    id          serial primary key,
    event_id    VARCHAR(256),
    river_id    VARCHAR(256)
);


create table riverstore(
    id                     serial primary key,
    river_id               VARCHAR(256) NOT NULL,
    event_name             VARCHAR(50),
    behov_name             VARCHAR(50),
    is_fail                boolean,
    event_time            TIMESTAMP   NOT NULL DEFAULT now(),
    message                TEXT
)