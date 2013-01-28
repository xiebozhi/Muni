CREATE DATABASE IF NOT EXISTS minecraft;
GRANT ALL PRIVILEGES ON minecraft.* TO user@host BY 'password';

CREATE TABLE IF NOT EXISTS muni_towns (
    townName VARCHAR(25),
    townRank INTEGER,
    bankBal DOUBLE,
    taxRate DOUBLE,
    townCenterX DOUBLE,
    townCenterY DOUBLE,
    townCenterZ DOUBLE,
    PRIMARY KEY (townName)
);

CREATE TABLE IF NOT EXISTS muni_citizens (
    playerName VARCHAR(16),
    townName VARCHAR(25),
    mayor BINARY,
    deputy BINARY,
    applicant BINARY,
    invitee BINARY,
    PRIMARY KEY (playerName)
);

CREATE TABLE IF NOT EXISTS muni_transactions (
    id INT AUTO_INCREMENT,
    playerName VARCHAR(16),
    townName VARCHAR(25),
    trans_date DATE, 
    trans_time TIME,
    trans_type VARCHAR(30),
    trans_amount DOUBLE,
    notes VARCHAR(350),
    PRIMARY KEY (id)
);