CREATE TABLE eventstore
(
    id						        VARCHAR(256),
    uuid                            VARCHAR(256) NOT NULL,
    origin_uuid                     varchar(256),
    application_key                 varchar(256),
    event_name                       VARCHAR(50),
    behov_name                       VARCHAR(50),
    eventtime                       TIMESTAMP   NOT NULL DEFAULT now(),
    message							TEXT						NOT NULL
);