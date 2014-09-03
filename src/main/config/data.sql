INSERT INTO app_user VALUES (-1,'administrator','$2a$10$DhTvVT74hGUfOU7qZuDAPuFpQwBZjbbjucdJjjtnPtenZFu49bkye','admin',null,'admin@foo.bar',true,false,null,null,0,1,null,null,null,null);
INSERT INTO app_user VALUES (-2,'normaluser','$2a$10$.l84tRL3HL1TVL22xk5UuO01/q4loiaYyIBFZg8cmAHbHUGI8ciCC','user',null,'user@foo.bar',true,false,null,null,0,1,null,null,null,null);

INSERT INTO role VALUES (-1,'ROLE_ADMIN','Administrator role (can edit Users)',1,null,null,null,null);
INSERT INTO role VALUES (-2,'ROLE_USER','Default role for all Users',1,null,null,null,null);

INSERT INTO user_role VALUES (-1,-1);
INSERT INTO user_role VALUES (-2,-2);
