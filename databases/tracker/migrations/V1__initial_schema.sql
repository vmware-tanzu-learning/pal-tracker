CREATE TABLE time_entries (
  id         BIGINT(20) NOT NULL AUTO_INCREMENT,
  project_id BIGINT(20),
  user_id    BIGINT(20),
  date       VARCHAR(255),
  hours      INT,

  PRIMARY KEY (id)
)
  ENGINE = innodb
  DEFAULT CHARSET = utf8;