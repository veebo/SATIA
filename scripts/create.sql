CREATE TABLE roles (
  role_id serial NOT NULL,
  role varchar(20) NOT NULL,
  PRIMARY KEY (role_id)
);

CREATE TABLE users (
  username varchar(60) NOT NULL,
  password varchar(60) NOT NULL,
  role_id integer NOT NULL,
  enabled boolean NOT NULL DEFAULT true,
  PRIMARY KEY (username),
  FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

CREATE TABLE generators (
  gen_id bigserial NOT NULL,
  impl varchar(120) NOT NULL,
  PRIMARY KEY (gen_id)
);

CREATE TABLE langs (
  lang char(3) NOT NULL,
  PRIMARY KEY (lang)
);

CREATE TABLE tests (
  test_id bigserial NOT NULL,
  title varchar(40) NOT NULL,
  username varchar(60) NOT NULL,
  description varchar(400),
  gen_id bigint,
  created_when date NOT NULL,
  PRIMARY KEY (test_id),
  FOREIGN KEY (username) REFERENCES users(username),
  FOREIGN KEY (gen_id) REFERENCES generators(gen_id)
);

CREATE TABLE tasks (
  task_id bigserial NOT NULL,
  translation_id bigint NOT NULL,
  source_lang char(3) NOT NULL,
  target_land char(3) NOT NULL,
  gen_id bigint,
  PRIMARY KEY (task_id),
  FOREIGN KEY (gen_id) REFERENCES generators(gen_id),
  FOREIGN KEY (source_lang) REFERENCES langs(lang),
  FOREIGN KEY (target_land) REFERENCES langs(lang)
);

CREATE TABLE test_tasks (
  test_id bigint NOT NULL,
  task_id bigint NOT NULL,
  PRIMARY KEY (test_id, task_id),
  FOREIGN KEY (test_id) REFERENCES tests(test_id),
  FOREIGN KEY (task_id) REFERENCES tasks(task_id)
);

CREATE TABLE translations (
  translation_id bigserial NOT NULL,
  PRIMARY KEY (translation_id)
);

CREATE TABLE phrases (
  phrase_id bigserial NOT NULL,
  translation_id bigint NOT NULL,
  value varchar (400),
  lang char(3),
  PRIMARY KEY (phrase_id),
  FOREIGN KEY (translation_id) REFERENCES translations(translation_id),
  FOREIGN KEY (lang) REFERENCES langs(lang)
);

CREATE TABLE results (
  username varchar(60) NOT NULL,
  test_id bigint NOT NULL,
  start_time date NOT NULL,
  session_key varchar (50) NOT NULL,
  value double precision,
  PRIMARY KEY (username, test_id, start_time, session_key),
  FOREIGN KEY (username) REFERENCES users (username),
  FOREIGN KEY (test_id) REFERENCES tests (test_id)
);



INSERT INTO roles (role)
VALUES ('ROLE_USER');
INSERT INTO roles (role)
VALUES ('ROLE_ADMIN');

insert into users values('guest','$2a$10$Nee6WVwEWJOymQMfNhRGXOnW95DmklikX5zG0Q1awSciSYEJxrLTy',1,true);
insert into users values('sadmin','$2a$10$.UZrW33sUKybo1US35wbDuv/s/CnJGfDHS52l4NDqLMgnTEWYNK86',2,true);