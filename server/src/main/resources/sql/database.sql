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
