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

INSERT INTO roles (role)
VALUES ('ROLE_USER');
INSERT INTO roles (role)
VALUES ('ROLE_ADMIN');

insert into users values('guest','$2a$10$Nee6WVwEWJOymQMfNhRGXOnW95DmklikX5zG0Q1awSciSYEJxrLTy',1,true);
insert into users values('sadmin','$2a$10$.UZrW33sUKybo1US35wbDuv/s/CnJGfDHS52l4NDqLMgnTEWYNK86',2,true);