CREATE SCHEMA IF NOT EXISTS guard;

CREATE TABLE IF NOT EXISTS guard.users (
  id            SERIAL,
  name          VARCHAR(50) NOT NULL,
  email         VARCHAR(50) NOT NULL,
  last_check_in TIMESTAMP NOT NULL,
  break_days    INT NULL DEFAULT NULL,
);

CREATE TABLE IF NOT EXISTS guard.notifications (
  id            SERIAL,
  span_days     INTEGER NOT NULL,
  targets       TEXT NOT NULL,
  content       TEXT NOT NULL,
);
