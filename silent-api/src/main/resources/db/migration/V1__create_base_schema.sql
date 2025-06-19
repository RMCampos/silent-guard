CREATE TABLE IF NOT EXISTS sg_users (
  id            SERIAL,
  email         VARCHAR(50) NOT NULL,
  last_check_in TIMESTAMP NOT NULL,
  break_days    INTEGER NULL DEFAULT NULL,
  created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMP NULL DEFAULT NULL,
  disabled_at   TIMESTAMP NULL DEFAULT NULL,
  CONSTRAINT sg_users_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sg_messages (
  id            SERIAL,
  user_id       INTEGER NOT NULL,
  span_days     INTEGER NOT NULL,
  title         VARCHAR(300) NOT NULL,
  targets       VARCHAR(3000) NOT NULL,
  content       TEXT NOT NULL,
  created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMP NULL DEFAULT NULL,
  disabled_at   TIMESTAMP NULL DEFAULT NULL,
  CONSTRAINT sg_messages_pk PRIMARY KEY (id),
  CONSTRAINT sg_messages_user_id_fk FOREIGN KEY (user_id) REFERENCES sg_users
);
