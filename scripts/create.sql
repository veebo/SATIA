CREATE TABLE roles (
  role_id bigserial NOT NULL,
  role varchar(20) NOT NULL,
  PRIMARY KEY (role_id)
);

CREATE TABLE users (
  username varchar(60) NOT NULL,
  password varchar(60) NOT NULL,
  role_id bigint NOT NULL,
  enabled boolean NOT NULL DEFAULT true,
  first_name varchar(60),
  last_name varchar(60),
  email varchar(100) NOT NULL,
  PRIMARY KEY (username),
  FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

CREATE TABLE generators (
  gen_id bigserial NOT NULL,
  impl varchar(120) NOT NULL,
  PRIMARY KEY (gen_id)
);

CREATE TABLE fields (
  field_id bigserial NOT NULL,
  gen_id bigint NOT NULL,
  name varchar(60) NOT NULL,
  type integer NOT NULL,
  "order" integer NOT NULL,
  multiple boolean NOT NULL,
  PRIMARY KEY (field_id),
  FOREIGN KEY (gen_id) REFERENCES generators(gen_id)
);

CREATE TABLE field_values (
  field_value_id bigserial NOT NULL,
  field_id bigint NOT NULL,
  task_id bigint NOT NULL,
  value varchar (400),
  PRIMARY KEY (field_value_id),
  FOREIGN KEY (field_id) REFERENCES fields(field_id),
  FOREIGN KEY (task_id) REFERENCES tasks(task_id)
);

CREATE TABLE langs (
  lang char(3) NOT NULL,
  PRIMARY KEY (lang)
);

CREATE TABLE phrases (
  phrase_id bigserial NOT NULL,
  value varchar (400),
  lang char(3),
  PRIMARY KEY (phrase_id),
  FOREIGN KEY (lang) REFERENCES langs(lang)
);


CREATE TABLE translations (
  translation_id bigserial NOT NULL,
  phrase1_id bigint NOT NULL,
  phrase2_id bigint NOT NULL,
  PRIMARY KEY (translation_id),
  FOREIGN KEY (phrase1_id) REFERENCES phrases(phrase_id),
  FOREIGN KEY (phrase2_id) REFERENCES phrases(phrase_id)
);


CREATE TABLE tests (
  test_id bigserial NOT NULL,
  title varchar(40) NOT NULL,
  username varchar(60) NOT NULL,
  description varchar(400),
  gen_id bigint,
  created_when date NOT NULL,
  source_lang  char(3) NOT NULL,
  target_lang char(3) NOT NULL,
  PRIMARY KEY (test_id),
  FOREIGN KEY (username) REFERENCES users(username),
  FOREIGN KEY (gen_id) REFERENCES generators(gen_id),
  FOREIGN KEY (source_lang) REFERENCES langs(lang),
  FOREIGN KEY (target_lang) REFERENCES langs(lang)
);

CREATE TABLE tasks (
  task_id bigserial NOT NULL,
  translation_id bigint NOT NULL,
  source_num numeric(1) NOT NULL CHECK (source_num = 1 OR source_num = 2),
  gen_id bigint,
  PRIMARY KEY (task_id),
  FOREIGN KEY (translation_id) REFERENCES translations(translation_id),
  FOREIGN KEY (gen_id) REFERENCES generators(gen_id)
);

CREATE TABLE test_tasks (
  test_id bigint NOT NULL,
  task_id bigint NOT NULL,
  PRIMARY KEY (test_id, task_id),
  FOREIGN KEY (test_id) REFERENCES tests(test_id),
  FOREIGN KEY (task_id) REFERENCES tasks(task_id)
);

CREATE TABLE results (
  username varchar(60),
  fullname varchar(60),
  test_id bigint NOT NULL,
  start_time date NOT NULL,
  session_key varchar (50) NOT NULL,
  value double precision,
  PRIMARY KEY (test_id, start_time, session_key),
  FOREIGN KEY (username) REFERENCES users (username),
  FOREIGN KEY (test_id) REFERENCES tests (test_id)
);


INSERT INTO roles (role_id, role)
VALUES (1, 'ROLE_USER');
INSERT INTO roles (role_id, role)
VALUES (2, 'ROLE_ADMIN');

insert into users values('guest','$2a$10$Nee6WVwEWJOymQMfNhRGXOnW95DmklikX5zG0Q1awSciSYEJxrLTy',1,true);
insert into users values('sadmin','$2a$10$zHIPwot66.X/t9bf0fE2tuFEsPV6rgaDE893suQ8hRnOG3uUrpaai',2,true);