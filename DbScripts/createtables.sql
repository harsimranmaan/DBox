DROP TABLE IF EXISTS Client;
CREATE TABLE  Client  (
   username  VARCHAR(20) NOT NULL,
   userpassword  VARCHAR(40) NOT NULL,
   emailId  VARCHAR(40) NOT NULL,
   pairhash VARCHAR(40) NOT NULL,
   quota INT default '10',
	PRIMARY KEY (username)
	
);

INSERT INTO Client VALUES('maan','4aaaf3c285f100eb4c3bf3318694b035bcec79c5','maan.harry@gmail.com','4aaaf3c285',10);
INSERT INTO Client VALUES('kuntal','9db9a1a96d85f598a2ed4f77a17258c45aa9854b','kuntalce@gmail.com','9db9a1a96d',10);
INSERT INTO Client VALUES('prabal','19e580cd17664de1f84d705cf6be00ab638bdd6b','prabalsharma39@gmail.com','19e580cd17',10);

DROP TABLE IF EXISTS ServerDetails;
CREATE TABLE  ServerDetails  (
	servername  VARCHAR(80) NOT NULL,
	portNumber INT NOT NULL,
	serverIndex  int NOT NULL,
	lastCheck TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (servername),
	UNIQUE KEY ServerDetailsUNIQserverIndex (serverIndex)
);