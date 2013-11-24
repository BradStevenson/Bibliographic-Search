-- Page Rank
-- PR(A) = (1-d) + d(PR(C1)/C(C1) + ... PR(Cn)/C(Cn))

-- Methods implementer in MySQL

-- |P| = All papers which cite paper P.
-- N = Number of paper cited from paper P.
-- |P| = (C1 ... Cn) =  SELECT * FROM Papers INNER JOIN Citations ON Papers.paperID=Citations.citedPaperID WHERE Papers.paperID=P;
-- N(P) = SELECT COUNT(*) FROM Citations WHERE paperID=P;

-- PageRank value for each paper is added programmatically into pageRank column.
-- Each cluster we identify (research field) is then assigned an id which is added to clusterID column.

DROP TABLE IF EXISTS Papers;
DROP TABLE IF EXISTS Citations;

CREATE TABLE Papers
(
	paperID int NOT NULL AUTO_INCREMENT,
	title varchar(255),
	author varchar(50),
	year int NOT NULL,
	url varchar(255),
	pageRank int,
	clusterID int,
	CONSTRAINT
		PRIMARY KEY (paperID)
	-- CONSTRAINT
	-- 	FOREIGN KEY (citedPaperID)
	-- 	REFERENCES Citations (citationID)
)ENGINE=InnoDB;

CREATE TABLE Citations (
	citationID int NOT NULL auto_increment,
	paperID int NOT NULL,
	citedPaperID int NOT NULL,
	CONSTRAINT
		PRIMARY KEY (citationID)
	-- CONSTRAINT
	-- 	FOREIGN KEY (paperID) 
 --        REFERENCES Papers(paperID)
)ENGINE=InnoDB;

-- INSERT INTO Papers (title, author, year) VALUES (
-- 	"aTitle", "aAuthor", 1994
-- );

-- INSERT INTO Citations (paperID, citedPaperID) VALUES (
-- 	1, 2
-- );

-- INSERT INTO Citations (paperID, citedPaperID) VALUES (
-- 	1, 3
-- );

-- INSERT INTO Citations (paperID, citedPaperID) VALUES (
-- 	2, 3
-- );

-- INSERT INTO Citations (paperID, citedPaperID) VALUES (
-- 	3, 1
-- );

-- INSERT INTO Papers (title, author, year) VALUES (
-- 	"bTitle", "bAuthor", 1995
-- );

-- INSERT INTO Papers (title, author, year) VALUES (
-- 	"cTitle", "cAuthor", 2002
-- );