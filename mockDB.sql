CREATE TABLE Papers (
	paperID int NOT NULL auto_increment,
	title varchar(255),
	author varchar(50),
	year int NOT NULL,
	url varchar(255),
	citedPaperID int NOT NULL,
	PRIMARY KEY (paperID),
	FOREIGN KEY (citedPaperID)
		REFERENCES Citation(citationID)
);

CREATE TABLE Citation (
	citationID int NOT NULL auto_increment,
	paperID int,
	PRIMARY KEY (citationID),
	FOREIGN KEY (paperID) 
        REFERENCES Papers(paperID)
);

INSERT INTO Papers SET (
	title AS atitle,
	author AS aauthor,
	year AS 1994
);

INSERT INTO Citation SET (
	paperID = 1,
	citedPaperID = 2
);

INSERT INTO Citation SET (
	paperID = 1,
	citedPaperID = 3
);

INSERT INTO Papers SET (
	title AS btitle,
	author AS bauthor,
	year AS 1995
);

INSERT INTO Papers SET (
	title AS ctitle,
	author AS cauthor,
	year AS 2002
);