CREATE TABLE BOOK 
(
  ID NUMBER(18,0) NOT NULL,
  TITLE VARCHAR2(100) NOT NULL, 
  PRICE DECIMAL(4,2), 
  ISBN VARCHAR2(20), 
  NOOFPAGES INTEGER ,
 CONSTRAINT BOOK_PK PRIMARY KEY (ID)  ENABLE 
);