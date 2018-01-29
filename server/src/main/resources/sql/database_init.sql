DROP TABLE IF EXISTS Transaction;
DROP TABLE IF EXISTS BillShare;
DROP TABLE IF EXISTS Event;
DROP TABLE IF EXISTS Bill;
DROP TABLE IF EXISTS Tag;
DROP TABLE IF EXISTS GroupInvitation;
DROP TABLE IF EXISTS UserGroup;
DROP TABLE IF EXISTS PasswordResetCode;
DROP TABLE IF EXISTS User;

CREATE TABLE IF NOT EXISTS User (
  id       INT AUTO_INCREMENT PRIMARY KEY,
  name     VARCHAR(255),
  email    VARCHAR(255),
  password VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS PasswordResetCode (
  id         INT      AUTO_INCREMENT PRIMARY KEY,
  user_id    INT          NOT NULL REFERENCES User (id),
  code       VARCHAR(255) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS UserGroup (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(255),
  base_currency INT
);

CREATE TABLE IF NOT EXISTS GroupInvitation (
  id       INT AUTO_INCREMENT PRIMARY KEY,
  accepted BOOLEAN,
  user_id  INT,
  group_id INT
);

CREATE TABLE IF NOT EXISTS Tag (
  id       INT AUTO_INCREMENT PRIMARY KEY,
  name     VARCHAR(255),
  group_id INT
);

CREATE TABLE IF NOT EXISTS Bill (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  amount          DECIMAL(13, 2),
  share_technique INT,
  title           VARCHAR(255),
  description     TEXT,
  created_at      DATETIME,
  updated_at      DATETIME,
  deleted         BOOLEAN,
  currency        INT,
  exchange_rate   DOUBLE,
  user_id         INT,
  group_id        INT,
  tag_id          INT
);

CREATE TABLE IF NOT EXISTS Event (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  type           INT,
  undone         BOOLEAN,
  occured_at     DATETIME,
  description    TEXT,
  user_id        INT,
  user_caused_id INT,
  bill_before_id INT,
  bill_after_id  INT,
  group_id       INT
);

CREATE TABLE IF NOT EXISTS BillShare (
  id      INT AUTO_INCREMENT PRIMARY KEY,
  amount  DECIMAL(13, 2) NOT NULL,
  user_id INT,
  bill_id INT            NOT NULL
);

CREATE TABLE IF NOT EXISTS Transaction (
  id               INT      AUTO_INCREMENT PRIMARY KEY,
  created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
  amount           DECIMAL(13, 2),
  bill_id          INT,
  user_paid_id     INT,
  user_received_id INT
);


ALTER TABLE GroupInvitation
  ADD CONSTRAINT fk_groupinvitation_user_id FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE GroupInvitation
  ADD CONSTRAINT fk_groupinvitation_group_id FOREIGN KEY (group_id) REFERENCES UserGroup (id);

ALTER TABLE Tag
  ADD CONSTRAINT fk_tag_group_id FOREIGN KEY (group_id) REFERENCES UserGroup (id);

ALTER TABLE Bill
  ADD CONSTRAINT fk_bill_user_id FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE Bill
  ADD CONSTRAINT fk_bill_group_id FOREIGN KEY (group_id) REFERENCES UserGroup (id);

ALTER TABLE Bill
  ADD CONSTRAINT fk_bill_tag_id FOREIGN KEY (tag_id) REFERENCES Tag (id);

ALTER TABLE Event
  ADD CONSTRAINT fk_event_user_id FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE Event
  ADD CONSTRAINT fk_event_user_caused_id FOREIGN KEY (user_caused_id) REFERENCES User (id);

ALTER TABLE Event
  ADD CONSTRAINT fk_event_bill_before_id FOREIGN KEY (bill_before_id) REFERENCES Bill (id);

ALTER TABLE Event
  ADD CONSTRAINT fk_event_bill_after_id FOREIGN KEY (bill_after_id) REFERENCES Bill (id);

ALTER TABLE Event
  ADD CONSTRAINT fk_event_group_id FOREIGN KEY (group_id) REFERENCES UserGroup (id);

ALTER TABLE BillShare
  ADD CONSTRAINT fk_billshare_user_id FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE BillShare
  ADD CONSTRAINT fk_billshare_bill_id FOREIGN KEY (bill_id) REFERENCES Bill (id);

ALTER TABLE Transaction
  ADD CONSTRAINT fk_transaction_bill_id FOREIGN KEY (bill_id) REFERENCES Bill (id);

ALTER TABLE Transaction
  ADD CONSTRAINT fk_transaction_user_paid_id FOREIGN KEY (user_paid_id) REFERENCES User (id);

ALTER TABLE Transaction
  ADD CONSTRAINT fk_transaction_user_received_id FOREIGN KEY (user_received_id) REFERENCES User (id);

INSERT INTO User VALUES (1, 'Anna', 'qsefridget+anna@gmail.com',
                         '$2a$11$fbtBdjgpgxH2YqVwMwrbPuKb03RwL0xECB9j/EE5KpZdGqjLhntqq'); -- Plaintext: fridgetanna
INSERT INTO User VALUES (2, 'Bob', 'qsefridget+bob@gmail.com',
                         '$2a$11$tNnZ/KGGqiQuSILbS6AVgOzZgEhLdAJgHDGlaL4Xl1AcIK3O8LDe2'); -- Plaintext: fridgetbob
INSERT INTO User VALUES (3, 'Clara', 'qsefridget+clara@gmail.com',
                         '$2a$11$X5nf1XpWsksNRTub6cje7.5fqQC2eBSLLvyeT0b5m4wjQA/qSbLhm'); -- Plaintext: fridgetclara
INSERT INTO User VALUES (4, 'Dora', 'qsefridget+dora@gmail.com',
                         '$2a$11$eZm6ZwWXW9ceh1dfeO6n/OyGrGS1IO/0RsTCSbzR.LGyxi7K/niYe'); -- Plaintext: fridgetdora
INSERT INTO User VALUES (5, 'Erik', 'qsefridget+erik@gmail.com',
                         '$2a$11$3csSxlPvkzA7F8AwrZfK5.RB1q3XCam85ZPRejuSdgClbVGh2Pre2'); -- Plaintext: fridgeterik
INSERT INTO User VALUES (6, 'Friedrich', 'qsefridget+friedrich@gmail.com',
                         '$2a$11$hlMh1jUxf/b7YyaXfRKiR.lR6jgJNiRCkphby5VzGiuyeTeHs1XS2'); -- Plaintext: fridgetfriedrich
INSERT INTO User VALUES (7, 'Grete', 'qsefridget+grete@gmail.com',
                         '$2a$11$/Qg2zXbIIV1TLANJ2kc2ueSkMhx/QmvHHl7ytaUsP1vwehZ6I.p2.'); -- Plaintext: fridgetgrete
INSERT INTO User VALUES (8, 'Hans', 'qsefridget+hans@gmail.com',
                         '$2a$11$f8SDpuKiLwRpHBujpzSQlOtNLZpjbjQXWP3FPstIvF9C83kK3lXt.'); -- Plaintext: fridgethans
INSERT INTO User VALUES (9, 'Ida', 'qsefridget+ida@gmail.com',
                         '$2a$11$CKURqN7jcL6mxSzHPUy4wuq7kpKg90iE29DyFJ6UcYcEcUiHeYHiu'); -- Plaintext: fridgetida
INSERT INTO User VALUES (10, 'Jakob', 'qsefridget+jakob@gmail.com',
                         '$2a$11$LsvT86/VNrkfFjpwuC6Gl.rBy2Ridvxj9BILw89PQ5xYxOkIhJgMC'); -- Plaintext: fridgetjakob
--dummy
INSERT INTO User VALUES (12, 'Test', 'tester@test.at',
                         '$2a$11$f4x3/BF1PioeIjZOcyExw.714k59xrqhzlIzsr6BmWbMGN2oBEJKi'); -- Plaintext: test

INSERT INTO UserGroup VALUES (1, 'boys flat sharing', 1);
INSERT INTO UserGroup VALUES (2, 'girls flat sharing', 1);
INSERT INTO UserGroup VALUES (3, 'flat sharing', 1);
INSERT INTO UserGroup VALUES (4, 'students flat sharing', 1);
INSERT INTO UserGroup VALUES (5, 'trip to Switzerland', 1);
INSERT INTO UserGroup VALUES (6, 'skiing weekend', 1);
INSERT INTO UserGroup VALUES (7, 'trip to Italy', 1);
INSERT INTO UserGroup VALUES (8, 'clubbing', 1);
INSERT INTO UserGroup VALUES (9, 'dinner', 1);
INSERT INTO UserGroup VALUES (10, 'lunch', 1);

INSERT INTO GroupInvitation VALUES (1, TRUE, 2, 1);
INSERT INTO GroupInvitation VALUES (2, TRUE, 5, 1);
INSERT INTO GroupInvitation VALUES (3, TRUE, 6, 1);
INSERT INTO GroupInvitation VALUES (4, TRUE, 8, 1);
INSERT INTO GroupInvitation VALUES (5, TRUE, 10, 1);
INSERT INTO GroupInvitation VALUES (6, TRUE, 1, 9);
INSERT INTO GroupInvitation VALUES (7, TRUE, 2, 9);
INSERT INTO GroupInvitation VALUES (8, TRUE, 8, 6);
INSERT INTO GroupInvitation VALUES (9, FALSE, 9, 6);
INSERT INTO GroupInvitation VALUES (10, FALSE, 10, 6);

INSERT INTO Tag VALUES (1, 'Rent', 1);
INSERT INTO Tag VALUES (2, 'Groceries', 2);
INSERT INTO Tag VALUES (3, 'Electricity', 1);
INSERT INTO Tag VALUES (4, 'Gas', 1);
INSERT INTO Tag VALUES (5, 'Fuel', 2);
INSERT INTO Tag VALUES (6, 'Food', 6);
INSERT INTO Tag VALUES (7, 'Drinks', 8);
INSERT INTO Tag VALUES (8, 'Furniture', 2);
INSERT INTO Tag VALUES (9, 'Tickets', 7);
INSERT INTO Tag VALUES (10, 'Activities', 5);
INSERT INTO Tag VALUES (11, 'Outdoor', 9);

INSERT INTO Bill VALUES (1, 1000, 1, 'Groceries', 'for the weekend', now(), now(), FALSE, 1, 10000, 2, 1, 1);
INSERT INTO Bill VALUES (2, 1000, 2, 'Groceries', 'for the weekend', now(), now(), FALSE, 1, 10000, 2, 1, 4);
INSERT INTO Bill VALUES (3, 1000, 3, 'Groceries', 'for the weekend', now(), now(), FALSE, 1, 10000, 2, 1, 3);
INSERT INTO Bill VALUES (4, 9999, 1, 'Rent', 'includes electricity and gas', now(), now(), FALSE, 2, 11896, 5, 1, 1);
INSERT INTO Bill VALUES (5, 9999, 2, 'Rent', 'includes electricity and gas', now(), now(), FALSE, 2, 11896, 5, 1, 1);
INSERT INTO Bill VALUES (6, 9999, 3, 'Rent', 'includes electricity and gas', now(), now(), FALSE, 2, 11896, 5, 1, 1);
INSERT INTO Bill
VALUES (7, 4565, 1, 'Vacation', 'for hotel and transportation', now(), now(), FALSE, 3, 08832, 10, 6, 6);
INSERT INTO Bill
VALUES (8, 4565, 2, 'Vacation', 'for hotel and transportation', now(), now(), FALSE, 3, 08862, 10, 6, 6);
INSERT INTO Bill
VALUES (9, 4565, 3, 'Vacation', 'for hotel and transportation', now(), now(), FALSE, 3, 08832, 10, 6, 6);
INSERT INTO Bill
VALUES (10, 10001, 1, 'Electronic device', 'new Samsung 3D TV', now(), now(), TRUE, 3, 5, 10, 1, 3);

INSERT INTO Event VALUES (1, 1, FALSE, now(), 'creation of first bill', 2, 2, 1, 1, 1);
INSERT INTO Event VALUES (2, 2, FALSE, now(), 'edition of first bill', 5, 5, 1, 1, 1);
INSERT INTO Event VALUES (3, 3, FALSE, now(), 'deletion of first bill', 2, 2, 1, 1, 1);
INSERT INTO Event VALUES (4, 2, FALSE, now(), 'first group invitation', 5, 2, 1, 1, 1);
INSERT INTO Event VALUES (5, 1, FALSE, now(), 'creation of second bill', 2, 2, 1, 1, 1);
INSERT INTO Event VALUES (6, 2, FALSE, now(), 'edition of second bill', 5, 5, 1, 1, 1);
INSERT INTO Event VALUES (7, 3, TRUE, now(), 'wrong deletion of second bill', 5, 5, 1, 1, 1);
INSERT INTO Event VALUES (8, 3, FALSE, now(), 'second group invitation', 9, 8, 2, 2, 6);
INSERT INTO Event VALUES (9, 1, FALSE, now(), 'creation of third bill', 6, 6, 1, 2, 1);
INSERT INTO Event VALUES (10, 2, FALSE, now(), 'edition of third bill', 8, 8, 2, 2, 1);

INSERT INTO BillShare VALUES (1, 0, 2, 1);
INSERT INTO BillShare VALUES (2, 0, 5, 1);
INSERT INTO BillShare VALUES (3, 0, 6, 1);
INSERT INTO BillShare VALUES (4, 0, 8, 1);
INSERT INTO BillShare VALUES (5, 0, 10, 1);
INSERT INTO BillShare VALUES (6, 0, 2, 10);
INSERT INTO BillShare VALUES (7, 0, 5, 10);
INSERT INTO BillShare VALUES (8, 0, 8, 7);
INSERT INTO BillShare VALUES (9, 0, 9, 7);
INSERT INTO BillShare VALUES (10, 0, 10, 7);

INSERT INTO Transaction VALUES (1, now(), 100, 1, 5, 2);
INSERT INTO Transaction VALUES (2, now(), 200, 2, 6, 2);
INSERT INTO Transaction VALUES (3, now(), 500, 3, 8, 2);
INSERT INTO Transaction VALUES (4, now(), 1000, 4, 6, 5);
INSERT INTO Transaction VALUES (5, now(), 2000, 5, 6, 5);
INSERT INTO Transaction VALUES (6, now(), 5000, 6, 8, 5);
INSERT INTO Transaction VALUES (7, now(), 1000, 7, 8, 10);
INSERT INTO Transaction VALUES (8, now(), 2000, 8, 9, 10);
INSERT INTO Transaction VALUES (9, now(), 50000, 9, 9, 10);
INSERT INTO Transaction VALUES (10, now(), 1, 10, 6, 2);

-- Presentation Testdata
INSERT INTO User VALUES (15, 'Bob', 'bob@fridget.com',
                         '$2a$11$tNnZ/KGGqiQuSILbS6AVgOzZgEhLdAJgHDGlaL4Xl1AcIK3O8LDe2'); -- Plaintext: fridgetbob
INSERT INTO User VALUES (16, 'Anna', 'anna@fridget.com',
                         '$2a$11$fbtBdjgpgxH2YqVwMwrbPuKb03RwL0xECB9j/EE5KpZdGqjLhntqq'); -- Plaintext: fridgetanna