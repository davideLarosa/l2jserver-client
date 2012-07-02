INSERT INTO actions VALUES
('basic',0,'sit/stand','sit','Toggel sit down/stand up','RequestActionUse',0,100),
('basic',1,'walk/run','walk','Toggel between walking and running','RequestActionUse',1,200),
('basic',10000,'attack','attack','unverified id so far','Action',0,300),
('basic',10002,'nexttarget','next','unverified id so far','Action',0,400),
('basic',10003,'pick','pick','unverified id so far','Action',0,600),
('companion',16,'companion attack','petattack','Companion attack my target','RequestActionUse',16,100),
('companion',17,'companion stop attack','petcancel','Companion stop attacking','RequestActionUse',17,200),
('companion',15,'companion follow/stop','petfollow','Toggel companions follow or stay back','RequestActionUse',15,300),
('social',12,'greeting','greet','Emote greetings','RequestActionUse',12,100);
