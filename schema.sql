DROP TABLE IF EXISTS Rating;
DROP TABLE IF EXISTS Event_Staff;
DROP TABLE IF EXISTS Staff;
DROP TABLE IF EXISTS Ticket;
DROP TABLE IF EXISTS Visitor;
DROP TABLE IF EXISTS Band_Member;
DROP TABLE IF EXISTS Band;
DROP TABLE IF EXISTS Artist_Genre;
DROP TABLE IF EXISTS Performance;
DROP TABLE IF EXISTS Event;
DROP TABLE IF EXISTS Artist;
DROP TABLE IF EXISTS Scene;
DROP TABLE IF EXISTS Festival;
DROP TABLE IF EXISTS Location;

-- Create Locations table
CREATE TABLE Location
(
    location_id INT IDENTITY(1,1) PRIMARY KEY,
    address VARCHAR(255) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    continent VARCHAR(50) NOT NULL,
    image_url VARCHAR(255),
    description TEXT
);

-- Create Festivals table
<<<<<<< HEAD
CREATE TABLE Festival (
    festival_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
=======
CREATE TABLE Festival
(
    festival_id INT IDENTITY(1,1) PRIMARY KEY,
>>>>>>> 5c40fba31adc24920dbf93425ab679722eac1847
    year INT NOT NULL CHECK (year >= 1900),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    poster_image VARCHAR(255),
    description TEXT,
    location_id INT NOT NULL,
    FOREIGN KEY (location_id) REFERENCES Location(location_id)
);

<<<<<<< HEAD
-- Create Stages table (Κτίρια / Μουσικές Σκηνές)
CREATE TABLE Stage (
    stage_id SERIAL PRIMARY KEY,
=======
-- Create Scenes table
CREATE TABLE Scene
(
    scene_id INT IDENTITY(1,1) PRIMARY KEY,
>>>>>>> 5c40fba31adc24920dbf93425ab679722eac1847
    name VARCHAR(100) NOT NULL,
    description TEXT,
    max_capacity INT NOT NULL CHECK (max_capacity > 0),
    technical_equipment TEXT,
    location_id INT,
    image_url VARCHAR(255),
    FOREIGN KEY (location_id) REFERENCES Location(location_id)
);

-- Create Artists table
CREATE TABLE Artist
(
    artist_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    stage_name VARCHAR(100),
    date_of_birth DATE NOT NULL,
    website VARCHAR(255),
    instagram_profile VARCHAR(255)
);

-- Create Events table
CREATE TABLE Event
(
    event_id INT IDENTITY(1,1) PRIMARY KEY,
    festival_id INT NOT NULL,
    stage_id INT NOT NULL,
    event_date DATE NOT NULL,
    event_time TIME,
    image_url VARCHAR(255),
    description TEXT,
    FOREIGN KEY (festival_id) REFERENCES Festival(festival_id),
    FOREIGN KEY (stage_id) REFERENCES Stage(stage_id)
);

-- Fix for Performance table
CREATE TABLE Performance
(
    performance_id INT IDENTITY(1,1) PRIMARY KEY,
    event_id INT NOT NULL,
    artist_id INT,
    band_id INT,
    performance_type VARCHAR(50) CHECK (performance_type IN ('warm up','headline','Special guest')),
    start_time TIME NOT NULL,
<<<<<<< HEAD
    duration INTERVAL NOT NULL CHECK (duration <= INTERVAL '3 hours'),
    break_duration INTERVAL CHECK (break_duration BETWEEN INTERVAL '5 minutes' AND INTERVAL '30 minutes'),
    stage_id INT,
    image_url VARCHAR(255),
    description TEXT,
    FOREIGN KEY (event_id) REFERENCES Event(event_id),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id),
    FOREIGN KEY (band_id) REFERENCES Band(band_id),
    FOREIGN KEY (stage_id) REFERENCES Stage(stage_id)
);

-- Create Artists table
CREATE TABLE Artist (
    artist_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    alias VARCHAR(100),
    date_of_birth DATE NOT NULL,
    website VARCHAR(255),
    instagram_profile VARCHAR(255),
    image_url VARCHAR(255),
    description TEXT
=======
    duration TIME NOT NULL CHECK (DATEDIFF(MINUTE, '00:00:00', duration) <= 180),
    -- Max 3 hours
    break_duration TIME CHECK (DATEDIFF(MINUTE, '00:05:00', break_duration) BETWEEN 0 AND 25),
    -- 5 to 30 minutes
    FOREIGN KEY (event_id) REFERENCES Event(event_id),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id)
>>>>>>> 5c40fba31adc24920dbf93425ab679722eac1847
);

-- Create table for artist genres (many-to-many relationship)
CREATE TABLE Artist_Genre
(
    artist_id INT NOT NULL,
    genre VARCHAR(50) NOT NULL,
    subgenre VARCHAR(50),
    PRIMARY KEY (artist_id, genre, subgenre),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id)
);

-- Create table for Band membership (if artist is member of a band)
CREATE TABLE Band
(
    band_id INT IDENTITY(1,1) PRIMARY KEY,
    band_name VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    formation_date DATE,
    website VARCHAR(255),
    image_url VARCHAR(255),
    description TEXT
);


CREATE TABLE Band_Member
(
    band_id INT NOT NULL,
    artist_id INT NOT NULL,
    PRIMARY KEY (band_id, artist_id),
    FOREIGN KEY (band_id) REFERENCES Band(band_id),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id)
);

-- Create Visitors table
CREATE TABLE Visitor
(
    visitor_id INT IDENTITY(1,1) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    contact VARCHAR(255),
    age INT CHECK (age > 0)
);

-- Ticket table
CREATE TABLE Ticket
(
    ticket_id INT IDENTITY(1,1) PRIMARY KEY,
    event_id INT NOT NULL,
    stage_id INT NOT NULL,
    visitor_id INT NOT NULL,
    purchase_date DATE NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) CHECK (payment_method IN ('credit card','debit card','bank transfer','not cash')),
    ean BIGINT NOT NULL UNIQUE,
<<<<<<< HEAD
    category VARCHAR(50) CHECK (category IN ('General','VIP','Backstage')),
    used BOOLEAN DEFAULT FALSE,
    resale_status BOOLEAN,
=======
    ticket_category VARCHAR(50),
    used BIT DEFAULT 0,
    -- Use BIT (0 for FALSE, 1 for TRUE)
>>>>>>> 5c40fba31adc24920dbf93425ab679722eac1847
    CONSTRAINT unique_ticket_per_visitor_event UNIQUE (event_id, visitor_id),
    FOREIGN KEY (event_id) REFERENCES Event(event_id),
    FOREIGN KEY (visitor_id) REFERENCES Visitor(visitor_id),
    FOREIGN KEY (stage_id) REFERENCES Stage(stage_id)
);

-- Create Staff table
CREATE TABLE Staff
(
    staff_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT CHECK(age > 0),
    role VARCHAR(50) NOT NULL,
    experience_level VARCHAR(20) CHECK (experience_level IN ('εικικευόμενος', 'αρχάριος', 'μέσος', 'έμπειρος', 'πολύ έμπειρος')),
    image_url VARCHAR(255)
);

-- Create Event_Staff table
CREATE TABLE Event_Staff
(
    event_id INT NOT NULL,
    stage_id INT NOT NULL,
    staff_id INT NOT NULL,
    staff_category VARCHAR(50) CHECK (staff_category IN ('technical', 'security', 'auxiliary')),
    PRIMARY KEY (event_id, stage_id, staff_id, staff_category),
    FOREIGN KEY (event_id) REFERENCES Event(event_id),
    FOREIGN KEY (stage_id) REFERENCES Stage(stage_id),
    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id)
);

-- Create Ratings table
CREATE TABLE Rating
(
    rating_id INT IDENTITY(1,1) PRIMARY KEY,
    ticket_id INT NOT NULL,
    performance_id INT NOT NULL,
    interpretation_score INT CHECK (interpretation_score BETWEEN 1 AND 5),
    sound_lighting_score INT CHECK (sound_lighting_score BETWEEN 1 AND 5),
    stage_presence_score INT CHECK (stage_presence_score BETWEEN 1 AND 5),
    organization_score INT CHECK (organization_score BETWEEN 1 AND 5),
    overall_score INT CHECK (overall_score BETWEEN 1 AND 5),
<<<<<<< HEAD
=======
    rating_date DATETIME DEFAULT GETDATE(),
>>>>>>> 5c40fba31adc24920dbf93425ab679722eac1847
    FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id),
    FOREIGN KEY (performance_id) REFERENCES Performance(performance_id)
);

-- Create Review table
CREATE TABLE Review (
    review_id SERIAL PRIMARY KEY,
    visitor_id INT NOT NULL,
    performance_id INT NOT NULL,
    interpretation_rating INT CHECK (interpretation_rating BETWEEN 1 AND 5),
    sound_lighting_rating INT CHECK (sound_lighting_rating BETWEEN 1 AND 5),
    stage_presence_rating INT CHECK (stage_presence_rating BETWEEN 1 AND 5),
    organization_rating INT CHECK (organization_rating BETWEEN 1 AND 5),
    overall_impression_rating INT CHECK (overall_impression_rating BETWEEN 1 AND 5),
    review_date DATE NOT NULL,
    FOREIGN KEY (visitor_id) REFERENCES Visitor(visitor_id),
    FOREIGN KEY (performance_id) REFERENCES Performance(performance_id)
);

-- Create Website table
CREATE TABLE Website (
    website_id SERIAL PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    festival_id INT,
    image_url VARCHAR(255),
    description TEXT,
    FOREIGN KEY (festival_id) REFERENCES Festival(festival_id)
);

-- Create Resale_Queue table
CREATE TABLE Resale_Queue (
    ticket_id INT NOT NULL,
    seller_id INT NOT NULL,
    buyer_id INT NOT NULL,
    listing_date DATE NOT NULL,
    resale_status VARCHAR(20) CHECK (resale_status IN ('Pending','Completed')),
    fifo_order INT,
    PRIMARY KEY (ticket_id),
    FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id),
    FOREIGN KEY (seller_id) REFERENCES Visitor(visitor_id),
    FOREIGN KEY (buyer_id) REFERENCES Visitor(visitor_id)
);

-- Indexes (sample)
CREATE INDEX idx_festival_year ON Festival(year);
CREATE INDEX idx_ticket_purchase_date ON Ticket(purchase_date);
