BEGIN TRANSACTION;
CREATE TABLE `libavtor` (
  `BookId` integer NOT NULL DEFAULT '0',
  `AvtorId` integer NOT NULL DEFAULT '0'
);
CREATE TABLE "libaannotations" (
    "AvtorId" INTEGER PRIMARY KEY NOT NULL,
    "Title" TEXT NOT NULL DEFAULT '',
    "Body" TEXT
);
CREATE TABLE "libapics" (
    "AvtorId" INTEGER PRIMARY KEY NOT NULL,
    "File" TEXT
);
CREATE TABLE "libbannotations" (
    "BookId" INTEGER PRIMARY KEY NOT NULL,
    "Title" TEXT NOT NULL DEFAULT '',
    "Body" TEXT
);
CREATE TABLE "libbpics" (
    "BookId" INTEGER PRIMARY KEY NOT NULL,
    "File" TEXT
);
CREATE TABLE `libavtorname` (
  `AvtorId` integer PRIMARY KEY  AUTOINCREMENT,
  `FirstName` varchar(99)  NOT NULL DEFAULT '',
  `MiddleName` varchar(99)  NOT NULL DEFAULT '',
  `LastName` varchar(99)  NOT NULL DEFAULT '',
  `NickName` varchar(33)  NOT NULL DEFAULT '',
  `NoDonate` char(1) NOT NULL DEFAULT '',
  `uid` integer NOT NULL DEFAULT '0',
  `WebPay` varchar(255) NOT NULL DEFAULT '',
  `Email` varchar(255)  NOT NULL,
  `Homepage` varchar(255)  NOT NULL,
  `Source` char(1) NOT NULL,
  `Blocked` char(1) NOT NULL,
  `SourceId` integer NOT NULL
);
CREATE TABLE `libbook` (
  `BookId` integer PRIMARY KEY AUTOINCREMENT,
  `FileSize` integer NOT NULL DEFAULT '0',
  `Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Title` varchar(254)  NOT NULL DEFAULT '',
  `Title1` varchar(254) NOT NULL,
  `Lang` char(2) NOT NULL DEFAULT 'ru',
  `FileType` char(4) NOT NULL,
  `Year` integer NOT NULL DEFAULT '0',
  `Deleted` char(1)  NOT NULL DEFAULT '',
  `Ver` varchar(8) NOT NULL DEFAULT '',
  `FileAuthor` varchar(64) NOT NULL,
  `N` integer NOT NULL DEFAULT '0',
  `keywords` varchar(255) NOT NULL,
  `md5` char(32) NOT NULL,
  `Broken` char(1)  NOT NULL,
  `Modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Source` char(1) NOT NULL,
  `Blocked` char(1)  NOT NULL,
  `SourceId` integer NOT NULL
);
CREATE INDEX avtor on libavtor(AvtorId);
CREATE INDEX "aannotations" on libaannotations (AvtorId ASC);
CREATE INDEX "apics" on libapics (AvtorId ASC);
CREATE INDEX "bannotations" on libbannotations (BookId ASC);
CREATE INDEX "bpics" on libbpics (BookId ASC);
CREATE INDEX del on libbook(Deleted);
CREATE INDEX brok on libbook(Broken);
CREATE INDEX block on libbook(Blocked);
COMMIT;
