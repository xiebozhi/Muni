CREATE DATABASE IF NOT EXISTS minecraft;
GRANT ALL PRIVILEGES ON minecraft.* TO user@host BY 'password';

CREATE TABLE IF NOT EXISTS muni_towns (
    townName VARCHAR(64),
    townRank INTEGER,
    bankBal DOUBLE,
    taxRate DOUBLE,
    townCenterX DOUBLE,
    townCenterY DOUBLE,
    townCenterZ DOUBLE
);

CREATE TABLE IF NOT EXISTS muni_citizens (
    playerName VARCHAR(64),
    townName VARCHAR(64),
    mayor BINARY,
    deputy BINARY,
    applicant BINARY,
    invitee BINARY
);

CREATE TABLE IF NOT EXISTS muni_transactions (
    playerName VARCHAR(64),
    townName VARCHAR(64),
    trans_type VARCHAR(64),
    amount DOUBLE,
    notes VARCHAR(300)
);