CREATE TABLE IF NOT EXISTS Accounts (id UUID not NULL, name VARCHAR not NULL, balance DECIMAL not NULL default 0, PRIMARY KEY ( id ));
CREATE TABLE IF NOT EXISTS History (id UUID not NULL, account_from UUID not NULL, account_to UUID not NULL, amount DECIMAL not NULL, PRIMARY KEY ( id ));
ALTER TABLE History ADD FOREIGN KEY (account_from) REFERENCES Accounts(id);
ALTER TABLE History ADD FOREIGN KEY (account_to) REFERENCES Accounts(id);
