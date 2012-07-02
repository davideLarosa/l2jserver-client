CREATE TABLE actions (
  category varchar(200) default '' NOT NULL,
  id decimal(11,0) default 0 NOT NULL,
  name varchar(200) default '' NOT NULL,
  image varchar(255) default '',
  description varchar(255) default '',
  type varchar(20) default '',
  actionId decimal(11,0) default 0,
  displayOrder decimal(11,0) default 0,
  PRIMARY KEY (id)
);