drop table if exists user_role cascade;
drop table if exists role cascade;
drop table if exists app_user cascade;

create table app_user (
    id integer not null auto_increment,
    username varchar(16) not null,
    password varchar(80) not null,
    first_name varchar(64) not null,
    last_name varchar(64) default null,
    email varchar(64) not null,
    account_enabled boolean not null,
    account_locked boolean not null,
    account_expired_date date default null,
    credentials_expired_date date default null,
    version integer not null,
    create_user varchar(16) default null,
    create_date timestamp default '0000-00-00 00:00:00',
    update_user varchar(16) default null,
    update_date timestamp default '0000-00-00 00:00:00',
    primary key (id),
    unique (username),
    unique (email)
) engine = InnoDB default character set utf8;

create table role (
    id integer not null auto_increment,
    name varchar(64) not null,
    description varchar(64) not null,
    version integer not null,
    create_user varchar(16) default null,
    create_date timestamp default '0000-00-00 00:00:00',
    update_user varchar(16) default null,
    update_date timestamp default '0000-00-00 00:00:00',
    primary key (id)
) engine = InnoDB default character set utf8;

create table user_role (
    user_id integer not null,
    role_id integer not null,
    primary key (
        user_id,
        role_id
    ),
    foreign key (user_id) references app_user (id),
    foreign key (role_id) references role (id)
) engine = InnoDB default character set utf8;
