CREATE TABLE skills (
  skill_id decimal(11,0) default 0 NOT NULL,
  levels decimal(4,0) default 100 NOT NULL,
  name varchar(200) default '' NOT NULL,
  PRIMARY KEY (skill_id)
);