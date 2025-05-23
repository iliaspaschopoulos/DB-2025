-- Clear all data (respecting FK constraints)
DELETE FROM Rating;
DELETE FROM Resale_Queue;
DELETE FROM Website;
DELETE FROM Event_Staff;
DELETE FROM Ticket;
DELETE FROM Band_Member;
DELETE FROM Artist_Genre;
DELETE FROM Performance;
DELETE FROM Event;
DELETE FROM Staff;
DELETE FROM Visitor;
DELETE FROM Band;
DELETE FROM Artist;
DELETE FROM Scene;
DELETE FROM Festival;
DELETE FROM Location;

DBCC CHECKIDENT ('Location', RESEED, 0);
DBCC CHECKIDENT ('Festival', RESEED, 0);
DBCC CHECKIDENT ('Scene', RESEED, 0);
DBCC CHECKIDENT ('Event', RESEED, 0);
DBCC CHECKIDENT ('Artist', RESEED, 0);
DBCC CHECKIDENT ('Band', RESEED, 0);
DBCC CHECKIDENT ('Performance', RESEED, 0);
DBCC CHECKIDENT ('Visitor', RESEED, 0);
DBCC CHECKIDENT ('Ticket', RESEED, 0);
DBCC CHECKIDENT ('Staff', RESEED, 0);
DBCC CHECKIDENT ('Rating', RESEED, 0);
DBCC CHECKIDENT ('Website', RESEED, 0);
DBCC CHECKIDENT ('Resale_Queue', RESEED, 0);
