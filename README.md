#Getting started with MyBatis CDI

This is a sample web application using play framework and mybatis. The main goal is to get you start with mybatis.
In this application you could register as an user and manage your beam (messages).

##Pre-requirments

- JRE 7+
- [Playframework] (https://downloads.typesafe.com/play/2.2.6/play-2.2.6.zip) 2.2.6
- Postgresql (use your favourite RDBMS by changing conf/mybatis-config.xml and build.sbt)


##How do I use it?

- Create two DBSchema with names demo, demo_users
- Run the following psql command at demo db:
```sql
CREATE SEQUENCE uk_seq;

CREATE TABLE DBShard(
	id bigint DEFAULT nextval('uk_seq') PRIMARY KEY,
	driver text NOT NULL,
	url text NOT NULL,
	usr text NOT NULL,
	passwd text NOT NULL,
	weight int NOT NULL);

CREATE TABLE UserAccount(
	id bigint DEFAULT nextval('uk_seq') PRIMARY KEY,
	uname varchar(255) NOT NULL unique,
	fname varchar(30) NOT NULL,
	lname varchar(30) NOT NULL,
	passwd text NOT NULL,
	shard bigint NOT NULL REFERENCES DBShard);

INSERT INTO DBShard (driver, url, usr, passwd, weight) 
VALUES ('org.postgresql.Driver', 'jdbc:postgresql://127.0.0.1:5432/demo_users',
'giridhar', 'giridhar', 0);
```
- Go to repository home directory and run the following bash command
```bash
play run
```

- Access your web application at localhost:9000 :-)
