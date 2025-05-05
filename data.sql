-- Clear all data (respecting FK constraints)
DELETE FROM Rating;
DELETE FROM Event_Staff;
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
DELETE FROM Location;

-- Reset identity seeds to 0 for all tables with IDENTITY columns
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

-- 1. Location
INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES
('123 Main St', 37.9838, 23.7275, 'Athens', 'Greece', 'Europe'),      -- location_id = 1
('456 Elm St', 40.7128, -74.0060, 'New York', 'USA', 'North America');-- location_id = 2

-- 2. Festival
INSERT INTO Festival (year, start_date, end_date, location_id) VALUES
(2023, '2023-07-01', '2023-07-05', 1),
(2024, '2024-07-01', '2024-07-06', 2);

-- Scene
INSERT INTO Scene (name, description, max_capacity, equipment_info) VALUES
('Main Stage', 'The biggest stage', 1000, 'Full sound & light'),
('Indie Stage', 'For indie bands', 500, 'Basic sound');

-- Artist
INSERT INTO Artist (name, stage_name, date_of_birth, website, instagram_profile) VALUES
('John Doe', 'DJ John', '1990-05-15', 'http://johndoe.com', 'http://instagram.com/djjohn'),
('Maria Smith', NULL, '1985-11-23', NULL, NULL);

-- Band
INSERT INTO Band (band_name, formation_date, website) VALUES
('The Rockers', '2010-03-10', 'http://therockers.com'),
('Jazz Cats', '2015-06-20', NULL);

-- Band_Member
INSERT INTO Band_Member (band_id, artist_id) VALUES
(1, 1),
(2, 2);

-- Artist_Genre
INSERT INTO Artist_Genre (artist_id, genre, subgenre) VALUES
(1, 'Rock', 'Alternative'),
(2, 'Jazz', NULL);

-- Visitor
INSERT INTO Visitor (first_name, last_name, contact, age) VALUES
('Alice', 'Brown', 'alice@example.com', 25),
('Bob', 'Green', 'bob@example.com', 32),
('Charlie', 'White', NULL, 28);

-- Event
INSERT INTO Event (festival_id, scene_id, event_date) VALUES
(1, 1, '2023-07-02'),
(1, 2, '2023-07-03'),
(2, 1, '2024-07-02');

-- Performance
INSERT INTO Performance (event_id, artist_id, band_id, performance_type, start_time, duration, break_duration) VALUES
(1, 1, NULL, 'headline', '20:00', '01:30:00', '00:10:00'),
(2, NULL, 1, 'warm up', '18:00', '01:00:00', '00:05:00'),
(3, 2, NULL, 'Special guest', '21:00', '01:15:00', NULL);

-- Ticket
INSERT INTO Ticket (event_id, visitor_id, purchase_date, cost, payment_method, ean, ticket_category, used, performance_id) VALUES
(1, 1, '2023-06-15', 100.00, 'credit card', 1234567890123, 'VIP', 0, 1),
(2, 2, '2023-06-20', 50.00, 'debit card', 1234567890124, 'Regular', 1, 2),
(3, 3, '2024-06-10', 120.00, 'bank transfer', 1234567890125, 'VIP', 0, 3);

-- Staff
INSERT INTO Staff (name, age, role, experience_level) VALUES
('Dimitris Papadopoulos', 35, 'Technician', 'έμπειρος'),
('Eleni Georgiou', 28, 'Security', 'μέσος'),
('Nikos Ioannou', 40, 'Auxiliary', 'πολύ έμπειρος');

-- Event_Staff
INSERT INTO Event_Staff (event_id, scene_id, staff_id, staff_category) VALUES
(1, 1, 1, 'technical'),
(1, 1, 2, 'security'),
(1, 1, 3, 'auxiliary');

-- Rating
INSERT INTO Rating (ticket_id, performance_id, visitor_id, interpretation_score, sound_lighting_score, stage_presence_score, organization_score, overall_score, rating_date) VALUES
(1, 1, 1, 5, 4, 5, 4, 5, GETDATE()),
(2, 2, 2, 4, 4, 4, 5, 4, GETDATE());

-- Website
INSERT INTO Website (url, festival_id, image_url, description) VALUES
('http://festival2023.com', 1, 'http://img.com/fest2023.jpg', N'Annual music festival in Athens'),
('http://festival2024.com', 2, NULL, N'New York summer festival');

-- Resale_Queue
INSERT INTO Resale_Queue (ticket_id, seller_id, buyer_id, listing_date, resale_status, fifo_order) VALUES
(2, 2, 3, '2023-06-25', 'Pending', 1),
(3, 3, 1, '2024-06-15', 'Completed', 2);
