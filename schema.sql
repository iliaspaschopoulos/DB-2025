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
    continent VARCHAR(50) NOT NULL
);

-- Create Festivals table
CREATE TABLE Festival
(
    festival_id INT IDENTITY(1,1) PRIMARY KEY,
    year INT NOT NULL CHECK (year >= 1900),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    location_id INT NOT NULL,
    FOREIGN KEY (location_id) REFERENCES Location(location_id)
);

-- Create Scenes table
CREATE TABLE Scene
(
    scene_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    max_capacity INT NOT NULL CHECK (max_capacity > 0),
    equipment_info TEXT
);

-- Create Artists table
CREATE TABLE Artist
(
    artist_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    stage_name VARCHAR(100),
    date_of_birth DATE NOT NULL,
    website VARCHAR(255),
    instagram_profile VARCHAR(255),
    consecutive_years_appearing INT NOT NULL
);

-- Create Events table
CREATE TABLE Event
(
    event_id INT IDENTITY(1,1) PRIMARY KEY,
    festival_id INT NOT NULL,
    scene_id INT NOT NULL,
    event_date DATE NOT NULL,
    FOREIGN KEY (festival_id) REFERENCES Festival(festival_id),
    FOREIGN KEY (scene_id) REFERENCES Scene(scene_id)
);

-- Fix for Performance table
CREATE TABLE Performance
(
    performance_id INT IDENTITY(1,1) PRIMARY KEY,
    event_id INT NOT NULL,
    artist_id INT NOT NULL,
    performance_type VARCHAR(50) CHECK (performance_type IN ('warm up','headline','Special guest')),
    start_time TIME NOT NULL,
    duration TIME NOT NULL CHECK (DATEDIFF(MINUTE, '00:00:00', duration) <= 180),
    -- Max 3 hours
    break_duration TIME CHECK (DATEDIFF(MINUTE, '00:05:00', break_duration) BETWEEN 0 AND 25),
    -- 5 to 30 minutes
    FOREIGN KEY (event_id) REFERENCES Event(event_id),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id)
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
    formation_date DATE,
    website VARCHAR(255)
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
    visitor_id INT NOT NULL,
    purchase_date DATE NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) CHECK (payment_method IN ('credit card','debit card','bank transfer','not cash')),
    ean BIGINT NOT NULL UNIQUE,
    ticket_category VARCHAR(50),
    used BIT DEFAULT 0,
    -- Use BIT (0 for FALSE, 1 for TRUE)
    CONSTRAINT unique_ticket_per_visitor_event UNIQUE (event_id, visitor_id),
    FOREIGN KEY (event_id) REFERENCES Event(event_id),
    FOREIGN KEY (visitor_id) REFERENCES Visitor(visitor_id)
);

-- Create Staff table
CREATE TABLE Staff
(
    staff_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT CHECK(age > 0),
    role VARCHAR(50) NOT NULL,
    experience_level VARCHAR(20) CHECK (experience_level IN ('ειδικευόμενος', 'αρχάριος', 'μέσος', 'έμπειρος', 'πολύ έμπειρος'))
);

-- Create Event_Staff table
CREATE TABLE Event_Staff
(
    event_id INT NOT NULL,
    scene_id INT NOT NULL,
    staff_id INT NOT NULL,
    staff_category VARCHAR(50) CHECK (staff_category IN ('technical', 'security', 'auxiliary')),
    PRIMARY KEY (event_id, scene_id, staff_id, staff_category),
    FOREIGN KEY (event_id) REFERENCES Event(event_id),
    FOREIGN KEY (scene_id) REFERENCES Scene(scene_id),
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
    rating_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id),
    FOREIGN KEY (performance_id) REFERENCES Performance(performance_id)
);

-- Create Website table
CREATE TABLE Website
(
    website_id INT IDENTITY(1,1) PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    festival_id INT,
    image_url VARCHAR(255),
    description NVARCHAR(MAX),
    FOREIGN KEY (festival_id) REFERENCES Festival(festival_id)
);

-- Create Resale_Queue table
CREATE TABLE Resale_Queue
(
    ticket_id INT NOT NULL PRIMARY KEY,
    seller_id INT NOT NULL,
    buyer_id INT NOT NULL,
    listing_date DATE NOT NULL,
    resale_status VARCHAR(20) CHECK (resale_status IN ('Pending', 'Completed')),
    fifo_order INT,
    FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id),
    FOREIGN KEY (seller_id) REFERENCES Visitor(visitor_id),
    FOREIGN KEY (buyer_id) REFERENCES Visitor(visitor_id)
);


-- Indexes (sample)
CREATE INDEX idx_festival_year ON Festival(year);
CREATE INDEX idx_ticket_purchase_date ON Ticket(purchase_date);
