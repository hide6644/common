INSERT INTO app_user VALUES (-1,'administrator','{bcrypt}$2a$10$yaQ597EfbNuAUARw28gp4OfMLPgFI4xHD/6k.fOHepTa8bywIKuhC','admin',null,'admin@foo.bar',true,false,null,null,0,null,null,null,null);
INSERT INTO app_user VALUES (-2,'normaluser','{bcrypt}$2a$10$/VCEQ8D6Pl1fbHGU3A2x4eVYQ/4zbmpu8lT8/76V0Pet653qyAfwG','user',null,'user@foo.bar',true,false,null,null,0,null,null,null,null);

INSERT INTO role VALUES (-1,'ROLE_ADMIN','Administrator role (can edit Users)',1,null,null,null,null);
INSERT INTO role VALUES (-2,'ROLE_USER','Default role for all Users',1,null,null,null,null);

INSERT INTO user_role VALUES (-1,-1);
INSERT INTO user_role VALUES (-2,-2);
