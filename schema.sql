-- Create Locations table
CREATE TABLE Location (
    location_id SERIAL PRIMARY KEY,
    address VARCHAR(255) NOT NULL,
    latitude  DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    continent VARCHAR(50) NOT NULL
);

-- Create Festivals table
CREATE TABLE Festival (
    festival_id SERIAL PRIMARY KEY,
    year INT NOT NULL CHECK (year >= 1900),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    location_id INT NOT NULL,
    FOREIGN KEY (location_id) REFERENCES Location(location_id)
);

-- Create Scenes table (Κτίρια / Μουσικές Σκηνές)
CREATE TABLE Scene (
    scene_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    max_capacity INT NOT NULL CHECK (max_capacity > 0),
    equipment_info TEXT
);

-- Create Events table (Παραστάσεις)
CREATE TABLE Event (
    event_id SERIAL PRIMARY KEY,
    festival_id INT NOT NULL,
    scene_id INT NOT NULL,
    event_date DATE NOT NULL,
    FOREIGN KEY (festival_id) REFERENCES Festival(festival_id),
    FOREIGN KEY (scene_id) REFERENCES Scene(scene_id)
);

-- Create Performances table
CREATE TABLE Performance (
    performance_id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    artist_id INT NOT NULL,
    performance_type VARCHAR(50) CHECK (performance_type IN ('warm up','headline','Special guest')),
    start_time TIME NOT NULL,
    duration INTERVAL NOT NULL CHECK (duration <= INTERVAL '3 hours'),
    break_duration INTERVAL CHECK (break_duration BETWEEN INTERVAL '5 minutes' AND INTERVAL '30 minutes'),
    FOREIGN KEY (event_id) REFERENCES Event(event_id)
    -- FOREIGN KEY (artist_id) to be defined after Artist table
);

-- Create Artists table
CREATE TABLE Artist (
    artist_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    stage_name VARCHAR(100),
    date_of_birth DATE NOT NULL,
    website VARCHAR(255),
    instagram_profile VARCHAR(255)
    -- Additional columns can be stored (genres, sub-genres) via a join table.
);

-- Create table for artist genres (many-to-many relationship)
CREATE TABLE Artist_Genre (
    artist_id INT NOT NULL,
    genre VARCHAR(50) NOT NULL,
    subgenre VARCHAR(50),
    PRIMARY KEY (artist_id, genre, subgenre),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id)
);

-- Create table for Band membership (if artist is member of a band)
CREATE TABLE Band (
    band_id SERIAL PRIMARY KEY,
    band_name VARCHAR(100) NOT NULL,
    formation_date DATE,
    website VARCHAR(255)
);
CREATE TABLE Band_Member (
    band_id INT NOT NULL,
    artist_id INT NOT NULL,
    PRIMARY KEY (band_id, artist_id),
    FOREIGN KEY (band_id) REFERENCES Band(band_id),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id)
);

-- Create Visitors table
CREATE TABLE Visitor (
    visitor_id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    contact VARCHAR(255),
    age INT CHECK (age > 0)
);

-- Create Tickets table
CREATE TABLE Ticket (
    ticket_id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    visitor_id INT NOT NULL,
    purchase_date DATE NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) CHECK (payment_method IN ('credit card','debit card','bank transfer','not cash')),
    ean BIGINT NOT NULL UNIQUE,
    ticket_category VARCHAR(50),
    used BOOLEAN DEFAULT FALSE,
    CONSTRAINT unique_ticket_per_visitor_event UNIQUE (event_id, visitor_id),
    FOREIGN KEY (event_id) REFERENCES Event(event_id),
    FOREIGN KEY (visitor_id) REFERENCES Visitor(visitor_id)
);

-- Create Staff table
CREATE TABLE Staff (
    staff_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT CHECK(age>0),
    role VARCHAR(50) NOT NULL,
    experience_level VARCHAR(20) CHECK (experience_level IN ('εικικευόμενος', 'αρχάριος', 'μέσος', 'έμπειρος', 'πολύ έμπειρος'))
);

-- Create Event_Staff table
CREATE TABLE Event_Staff (
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
CREATE TABLE Rating (
    rating_id SERIAL PRIMARY KEY,
    ticket_id INT NOT NULL,
    artist_id INT NOT NULL,
    interpretation_score INT CHECK (interpretation_score BETWEEN 1 AND 5),
    overall_score INT CHECK (overall_score BETWEEN 1 AND 5),
    -- additional rating criteria columns...
    FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id)
);

-- Indexes (sample)
CREATE INDEX idx_festival_year ON Festival(year);
CREATE INDEX idx_ticket_purchase_date ON Ticket(purchase_date);