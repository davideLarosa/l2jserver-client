CREATE TABLE etcitem (
  item_id decimal(11,0) default 0 NOT NULL,
  name varchar(100) default '' NOT NULL,
  crystallizable varchar(5) default 'false'NOT NULL,
  item_type varchar(14) default 'none' NOT NULL,
  weight decimal(4,0) default 0 NOT NULL,
  consume_type varchar(9) default 'normal' NOT NULL,
  material varchar(11) default 'wood' NOT NULL,
  crystal_type varchar(4) default 'none' NOT NULL,
  duration integer default -1 NOT NULL, -- duration in minutes for shadow items
  time integer default -1 NOT NULL,     -- duration in minutes for time limited items
  price decimal(11,0) default 0 NOT NULL,
  crystal_count integer default 0 NOT NULL,
  sellable varchar(5) default 'false' NOT NULL,
  dropable varchar(5) default 'false' NOT NULL,
  destroyable varchar(5) default 'false' NOT NULL,
  tradeable varchar(5) default 'false' NOT NULL,
  handler varchar(70) DEFAULT 'none' NOT NULL,
  skill varchar(70) DEFAULT '0-0;' NOT NULL,
  PRIMARY KEY (item_id)
);