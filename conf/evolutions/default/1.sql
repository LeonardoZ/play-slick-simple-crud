# --- !Ups

create table "tasks" (
  "id" int primary key not null,
  "name" varchar(110) not null,
  "description" varchar(255) not null
)

# --- !Downs

drop table "tasks" if exists;
