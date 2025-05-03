INSERT INTO Location
VALUES('Main Street 12', 37.9838, 23.7275, 'Athens', 'Greece', 'Europe'),
    ('20 Rue de Rivoli', 48.8566, 2.3522, 'Paris', 'France', 'Europe'),
    ('Times Square', 40.7580, -73.9855, 'New York', 'USA', 'North America'),
    ('Potsdamer Platz', 52.5096, 13.3760, 'Berlin', 'Germany', 'Europe'),
    ('Shibuya Crossing', 35.6595, 139.7005, 'Tokyo', 'Japan', 'Asia'),
    ('Federation Square', -37.8170, 144.9670, 'Melbourne', 'Australia', 'Oceania'),
    ('Plaza de Mayo', -34.6083, -58.3700, 'Buenos Aires', 'Argentina', 'South America'),
    ('Dam Square', 52.3732, 4.8921, 'Amsterdam', 'Netherlands', 'Europe'),
    ('Piccadilly Circus', 51.5101, -0.1340, 'London', 'UK', 'Europe'),
    ('Gran Via', 40.4203, -3.7058, 'Madrid', 'Spain', 'Europe');


INSERT INTO Festival
VALUES(2020, '2020-06-10', '2020-06-13', 1),
    (2021, '2021-07-15', '2021-07-17', 2),
    (2022, '2022-08-05', '2022-08-08', 3),
    (2023, '2023-06-20', '2023-06-23', 4),
    (2024, '2024-07-01', '2024-07-04', 5),
    (2019, '2019-05-10', '2019-05-12', 6),
    (2018, '2018-06-02', '2018-06-05', 7),
    (2017, '2017-04-25', '2017-04-28', 8),
    (2025, '2025-07-10', '2025-07-13', 9),-- Future
    (2026, '2026-06-12', '2026-06-15', 10);-- Future

INSERT INTO Scene
VALUES('Underground Stage', 'Alternative scene for indie artists.', 2500, 'Speakers, Microphones, Basic Lights'),
    ('VIP Lounge Stage', 'For VIP guest performances.', 800, 'Premium Lights, Wireless Mics, Special FX'),
    ('Jazz Corner', 'Jazz & blues corner.', 1000, 'Jazz equipment, acoustic tuning'),
    ('Rooftop Stage', 'Rooftop open-air experience.', 2000, 'Standard sound system, Lighting'),
    ('Main Stage', 'Main stage for headline acts.', 5000, 'Full sound system, Lighting, Video screens'),
    ('Chill Out Zone', 'Relaxing area with acoustic performances.', 1500, 'Acoustic setup, Ambient lights'),
    ('Food Truck Stage', 'Stage near food trucks for casual performances.', 2000, 'Basic sound system, Ambient lights'),
    ('Dance Arena', 'Electronic music and dance performances.', 3000, 'DJ setup, LED lights'),
    ('Acoustic Stage', 'Intimate acoustic performances.', 1000, 'Acoustic setup, Basic lights'),
    ('Comedy Corner', 'Stand-up comedy and light entertainment.', 800, 'Microphone, Basic lights'),
    ('Kids Zone', 'Family-friendly performances and activities.', 1200, 'Children-friendly equipment'),
    ('Cultural Stage', 'Showcasing local culture and traditions.', 1500, 'Traditional instruments, Basic lights'),
    ('Theater Stage', 'Theatrical performances and plays.', 2000, 'Stage setup, Lighting'),
    ('Art Installation Stage', 'Artistic performances and installations.', 1000, 'Artistic equipment, Ambient lights'),
    ('Silent Disco', 'Headphone party experience.', 500, 'Headphones, DJ setup'),
    ('Outdoor Cinema', 'Movies under the stars.', 3000, 'Projector, Screen'),
    ('Wellness Stage', 'Yoga and wellness activities.', 800, 'Yoga mats, Ambient music'),
    ('Interactive Stage', 'Audience participation performances.', 1200, 'Interactive equipment'),
    ('Pet Zone', 'Pet-friendly performances and activities.', 1000, 'Pet-friendly equipment'),
    ('Vintage Stage', 'Retro-themed performances.', 1500, 'Vintage equipment'),
    ('Gastronomy Stage', 'Cooking shows and food-related performances.', 2000, 'Cooking equipment'),
    ('Fashion Show Stage', 'Fashion shows and related events.', 2500, 'Runway setup'),
    ('Sports Arena', 'Sports-related performances and events.', 3000, 'Sports equipment'),
    ('Virtual Reality Stage', 'VR experiences and performances.', 1000, 'VR equipment'),
    ('Gaming Zone', 'Video game tournaments and related events.', 1500, 'Gaming setup'),
    ('Photography Corner', 'Photography exhibitions and workshops.', 800, 'Photography equipment'),
    ('Book Corner', 'Literary readings and book signings.', 600, 'Reading setup'),
    ('Craft Corner', 'DIY workshops and craft activities.', 700, 'Crafting equipment'),
    ('Dance Workshop Stage', 'Dance classes and workshops.', 1200, 'Dance floor setup');


INSERT INTO Artist
    (name, stage_name, date_of_birth, website, instagram_profile,consecutive_years_appearing)
VALUES('John Smith', 'DJ Smitty', '1990-04-15', 'http://djsmitty.com', '@djsmitty',1),
    ('Maria Garcia', NULL, '1985-09-12', NULL, '@mariag',1),
    ('Lee Minho', 'Min', '1992-11-22', 'http://minbeats.kr', '@minbeats',1),
    ('Sophie Dubois', NULL, '1980-01-05', 'http://sophiedubois.fr', NULL,1),
    ('Carlos Ruiz', 'CR-Beats', '1988-03-10', NULL, '@crbeats',1),
    ('Emma Johnson', 'Emmy J', '1995-07-20', 'http://emmyj.com', '@emmyj',1),
    ('Aisha Khan', 'DJ Aisha', '1987-05-25', 'http://djaisha.com', '@djaisha',1),
    ('Lucas Silva', 'DJ L-Silva', '1989-02-14', NULL, '@djlucassilva',1),
    ('Tomoko Tanaka', NULL, '1991-08-18', NULL, '@tomokotanaka',1),
    ('Nina Petrova', NULL, '1994-10-10', NULL, '@ninapetrova',1),
    ('Omar Hassan', 'O-Hass', '1986-06-30', NULL, '@omarhassan',1),
    ('Zara Ali', NULL, '1992-03-03', NULL, '@zaraali',1),
    ('Isabella Rossi', NULL, '1984-07-07', NULL, '@isabellarossi',1),
    ('Fatima El-Sayed', NULL, '1988-04-20', NULL, '@fatimaelsayed',1),
    ('Raj Patel', 'DJ Raj P.', '1995-11-11', NULL, '@djrajpatel',1),
    ('Clara Schmidt', NULL, '1991-07-07', NULL, '@claraschmidt',1),
    ('David Kim', 'D-Kim', '1987-09-09', NULL, '@davidkim',1),
    ('Sofia Rossi', NULL, '1993-01-01', NULL, '@sofiarossi',1);
-- na ftiaxoyme realistiko cya


INSERT INTO Event
    (festival_id, scene_id, event_date)
VALUES(1, 5, '2020-06-10'),
    (2, 1, '2021-07-15'),
    (3, 4, '2022-08-06'),
    (4, 3, '2023-06-21'),
    (5, 2, '2024-07-02');

-- Test: Insert John Smith (artist_id=1) for 3 consecutive years (should succeed)
INSERT INTO Event (festival_id, scene_id, event_date) VALUES (6, 5, '2019-05-10'); -- 2019
INSERT INTO Performance (event_id, artist_id, performance_type, start_time, duration, break_duration)
VALUES (6, 1, 'headline', '20:00:00', '01:00:00', '00:10:00');

INSERT INTO Event (festival_id, scene_id, event_date) VALUES (1, 5, '2020-06-10'); -- 2020
INSERT INTO Performance (event_id, artist_id, performance_type, start_time, duration, break_duration)
VALUES (7, 1, 'headline', '20:00:00', '01:00:00', '00:10:00');

INSERT INTO Event (festival_id, scene_id, event_date) VALUES (2, 5, '2021-07-15'); -- 2021
INSERT INTO Performance (event_id, artist_id, performance_type, start_time, duration, break_duration)
VALUES (8, 1, 'headline', '20:00:00', '01:00:00', '00:10:00');

-- Test: Insert John Smith for a 4th consecutive year (should fail)
INSERT INTO Event (festival_id, scene_id, event_date) VALUES (3, 5, '2022-08-05'); -- 2022
INSERT INTO Performance (event_id, artist_id, performance_type, start_time, duration, break_duration)
VALUES (9, 1, 'headline', '20:00:00', '01:00:00', '00:10:00');
-- This last insert should be blocked by the trigger


-- For simplicity, one event and many performances
INSERT INTO Performance
    (event_id, artist_id, performance_type, start_time, duration, break_duration)
VALUES
    (1, 1, 'warm up', '18:00:00', '01:00:00', '00:10:00'),
    (1, 2, 'headline', '19:10:00', '02:00:00', '00:15:00'),
    (1, 3, 'Special guest', '21:25:00', '01:30:00', '00:05:00');


INSERT INTO Artist_Genre
    (artist_id, genre, subgenre)
VALUES(1, 'Electronic', 'House'),
    (2, 'Pop', 'Synthpop'),
    (3, 'Hip-Hop', 'Trap'),
    (4, 'Jazz', 'Bebop'),
    (5, 'Rock', 'Hard Rock');


INSERT INTO Band
    (band_name, formation_date, website)
VALUES
    ('The Sound Surfers', '2010-03-15', 'http://soundsurfers.com'),
    ('Jazz Vibes', '2005-06-22', 'http://jazzvibes.org'),
    ('Electric Pulse', '2018-11-01', NULL),
    ('Global Beats Collective', '2012-09-30', 'http://gbc.world'),
    ('Retro Groove', '2000-01-01', NULL);


INSERT INTO Band_Member
    (band_id, artist_id)
VALUES
    (1, 1),-- John Smith in The Sound Surfers
    (2, 4),
    (3, 5),
    (4, 8),
    (5, 14),
    (5, 20);

-- Insert random Visitors (if not already present)
INSERT INTO Visitor (first_name, last_name, contact, age)
VALUES
('Alice', 'Johnson', 'alice@example.com', 28),
('Bob', 'Smith', 'bob@example.com', 34),
('Charlie', 'Brown', 'charlie@example.com', 22),
('Diana', 'Prince', 'diana@example.com', 30),
('Ethan', 'Hunt', 'ethan@example.com', 40);

-- Insert random Tickets (if not already present)
INSERT INTO Ticket (event_id, visitor_id, purchase_date, cost, payment_method, ean, ticket_category)
VALUES
(1, 1, '2020-06-01', 50.00, 'credit card', 1000000000001, 'VIP'),
(1, 2, '2020-06-02', 40.00, 'debit card', 1000000000002, 'Standard'),
(1, 3, '2020-06-03', 35.00, 'bank transfer', 1000000000003, 'Standard'),
(1, 4, '2020-06-04', 60.00, 'credit card', 1000000000004, 'VIP'),
(1, 5, '2020-06-05', 30.00, 'debit card', 1000000000005, 'Standard');

-- Insert random Ratings
INSERT INTO Rating
    (ticket_id, performance_id, visitor_id, interpretation_score, sound_lighting_score, stage_presence_score, organization_score, overall_score, rating_date)
VALUES
(1, 1, 1, 5, 4, 5, 4, 5, GETDATE()),
(2, 2, 2, 4, 5, 4, 5, 4, GETDATE()),
(3, 3, 3, 3, 4, 3, 4, 3, GETDATE()),
(4, 1, 4, 5, 5, 5, 5, 5, GETDATE()),
(5, 2, 5, 4, 3, 4, 3, 4, GETDATE());

-- Insert random Staff
INSERT INTO Staff (name, age, role, experience_level) VALUES
('Giorgos Papadopoulos', 35, 'Ηχολήπτης', 'πολύ έμπειρος'),
('Maria Kotsou', 29, 'Ηλεκτρολόγος', 'μέσος'),
('Nikos Dellas', 42, 'Σκηνοθέτης', 'έμπειρος'),
('Eleni Papadaki', 23, 'Φωτιστής', 'αρχάριος'),
('Christos Mantas', 31, 'Τεχνικός Ήχου', 'μέσος'),
('Anna Markou', 27, 'Βοηθός Σκηνής', 'ειδικευόμενος');

-- Insert random Event Staff
INSERT INTO Event_Staff (event_id, scene_id, staff_id, staff_category) VALUES
(1, 1, 1, 'technical'),
(1, 1, 2, 'technical'),
(2, 2, 3, 'technical'),
(2, 2, 4, 'auxiliary'),
(3, 3, 5, 'technical'),
(3, 3, 6, 'auxiliary');
