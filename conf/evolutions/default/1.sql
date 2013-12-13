# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table game (
  id                        bigint not null,
  title                     varchar(255),
  owned                     boolean,
  created                   timestamp,
  constraint pk_game primary key (id))
;

create table vote (
  id                        bigint not null,
  game_id                   bigint not null,
  created                   timestamp,
  constraint pk_vote primary key (id))
;

create sequence game_seq;

create sequence vote_seq;

alter table vote add constraint fk_vote_game_1 foreign key (game_id) references game (id) on delete restrict on update restrict;
create index ix_vote_game_1 on vote (game_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists game;

drop table if exists vote;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists game_seq;

drop sequence if exists vote_seq;

