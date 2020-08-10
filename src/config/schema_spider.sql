create database common;
grant all privileges on common.* to common identified by 'common_pass';

drop server common_backend1;
drop server common_backend2;
drop server common_backend1_rpl;
drop server common_backend2_rpl;
drop server common_monitor1;
drop server common_monitor2;

create server common_backend1 foreign data wrapper mysql options (
    host '192.168.0.69',
    database 'common',
    user 'common',
    password 'common_pass',
    port 3306
);
create server common_backend2 foreign data wrapper mysql options (
    host '192.168.0.70',
    database 'common',
    user 'common',
    password 'common_pass',
    port 3306
);
create server common_backend1_rpl foreign data wrapper mysql options (
    host '192.168.0.69',
    database 'common_rpl',
    user 'common',
    password 'common_pass',
    port 3306
);
create server common_backend2_rpl foreign data wrapper mysql options (
    host '192.168.0.70',
    database 'common_rpl',
    user 'common',
    password 'common_pass',
    port 3306
);
create server common_monitor1 foreign data wrapper mysql options (
    host '192.168.0.67',
    database 'common',
    user 'common',
    password 'common_pass',
    port 3306
);
create server common_monitor2 foreign data wrapper mysql options (
    host '192.168.0.68',
    database 'common',
    user 'common',
    password 'common_pass',
    port 3306
);
insert into mysql.spider_link_mon_servers
    (db_name, table_name, link_id, sid, server)
values
    ('common%','%','%',1,'common_monitor1'),
    ('common%','%','%',2,'common_monitor2');
select spider_flush_table_mon_cache();

drop table if exists common.user_role cascade;
drop table if exists common.role cascade;
drop table if exists common.app_user cascade;

create table common.app_user (
    id integer not null auto_increment,
    username varchar(16) not null,
    password varchar(80) not null,
    first_name varchar(64) not null,
    last_name varchar(64) default null,
    email varchar(64) not null,
    account_enabled boolean not null,
    account_locked boolean not null,
    account_expired_date datetime default null,
    credentials_expired_date datetime default null,
    version integer not null,
    create_user varchar(16) default null,
    create_date timestamp default '0000-00-00 00:00:00',
    update_user varchar(16) default null,
    update_date timestamp default '0000-00-00 00:00:00',
    primary key (id)
) default character set utf8 engine=spider comment='wrapper "mysql", table "app_user"'
    partition by key (id)
(
    partition pt1 comment = 'srv "common_backend1 common_backend2_rpl", mbk "2", mkd "2", link_status "1 1"',
    partition pt2 comment = 'srv "common_backend2 common_backend1_rpl", mbk "2", mkd "2", link_status "1 1" '
);

create table common.role (
    name varchar(64) not null,
    description varchar(64) not null,
    primary key (name)
) default character set utf8 engine=spider comment='wrapper "mysql", table "role"'
    partition by key (name)
(
    partition pt1 comment = 'srv "common_backend1 common_backend2_rpl", mbk "2", mkd "2", link_status "1 1"',
    partition pt2 comment = 'srv "common_backend2 common_backend1_rpl", mbk "2", mkd "2", link_status "1 1" '
);

create table common.user_role (
    user_id integer not null,
    role_name varchar(64) not null,
    primary key (
        user_id,
        role_name
    )
) default character set utf8 engine=spider comment='wrapper "mysql", table "user_role"'
    partition by key (user_id, role_name)
(
    partition pt1 comment = 'srv "common_backend1 common_backend2_rpl", mbk "2", mkd "2", link_status "1 1"',
    partition pt2 comment = 'srv "common_backend2 common_backend1_rpl", mbk "2", mkd "2", link_status "1 1" '
);
