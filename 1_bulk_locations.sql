-- Clear all data (respecting FK constraints)
DELETE FROM Rating;
DELETE FROM Event_Staff;
DELETE FROM Resale_Queue;
DELETE FROM Staff;
DELETE FROM Ticket;
DELETE FROM Visitor;
DELETE FROM Band_Member;
DELETE FROM Band;
DELETE FROM Artist_Genre;
DELETE FROM Performance;
DELETE FROM Event;
DELETE FROM Artist;
DELETE FROM Scene;
DELETE FROM Festival;
DELETE FROM Website;
DELETE FROM Location;
DBCC CHECKIDENT ('Location', RESEED, 0);

INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES ('Address1', -25.002141, 129.205148, 'City1', 'Country1', 'Continent1');
INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES ('Address2', -55.679519, -99.518514, 'City2', 'Country2', 'Continent2');
INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES ('Address3', 53.700090, 160.382239, 'City3', 'Country3', 'Continent3');
INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES ('Address4', -69.721376, 104.953289, 'City4', 'Country4', 'Continent4');
INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES ('Address5', 20.423239, 106.757268, 'City5', 'Country5', 'Continent5');
INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES ('Address6', -23.463104, 171.679570, 'City6', 'Country6', 'Continent6');
INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES ('Address7', 75.281905, -142.073531, 'City7', 'Country7', 'Continent7');
INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES ('Address8', 74.006137, 124.195669, 'City8', 'Country8', 'Continent8');
INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES ('Address9', -0.489507, -136.103220, 'City9', 'Country9', 'Continent9');
INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES ('Address10', -80.666800, -103.326220, 'City10', 'Country10', 'Continent10');
