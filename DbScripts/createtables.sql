DROP TABLE IF EXISTS Client;
CREATE TABLE  Client  (
   username  VARCHAR(20) NOT NULL,
   userpassword  VARCHAR(40) NOT NULL,
   pairhash VARCHAR(40) NOT NULL,
   quota INT default '10',
	PRIMARY KEY (username)
)

DROP TABLE IF EXISTS ServerDetails;
CREATE TABLE  ServerDetails  (
	servername  VARCHAR(80) NOT NULL,
	portNumber INT NOT NULL,
	serverIndex  int NOT NULL,
	lastCheck TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (servername),
	UNIQUE KEY ServerDetailsUNIQserverIndex (serverIndex)
);S