import random
from datetime import datetime, timedelta

HEADER_MAP = {
    '1_bulk_locations.sql': """\
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

""",
    '2_bulk_festivals.sql': "",
    '3_bulk_scenes.sql': "",
    '4_bulk_artists.sql': "",
    '5_bulk_bands.sql': "",
    '6_bulk_visitors.sql': "",
    '7_bulk_events.sql': "",
    '8_bulk_performances.sql': "",
    '9_bulk_tickets.sql': ""
}

def random_date(start, end):
    return (start + timedelta(days=random.randint(0, (end - start).days))).strftime('%Y-%m-%d')

# Generate 10 Locations (for 10 festivals)
with open('1_bulk_locations.sql', 'w', encoding='utf-8') as f:
    f.write(HEADER_MAP['1_bulk_locations.sql'])
    for i in range(1, 11):
        f.write(
            f"INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES "
            f"('Address{i}', {random.uniform(-90,90):.6f}, {random.uniform(-180,180):.6f}, 'City{i}', 'Country{i}', 'Continent{i}');\n"
        )

# Generate 10 Festivals (2 future, location_id 1-10)
with open('2_bulk_festivals.sql', 'w', encoding='utf-8') as f:
    f.write(HEADER_MAP['2_bulk_festivals.sql'])
    for i in range(1, 11):
        year = 2025 + (i - 9) if i > 8 else 2016 + i  # last two are future
        start_date = f"{year}-07-01"
        end_date = f"{year}-07-05"
        location_id = i  # 1-10, matches generated locations
        f.write(
            f"INSERT INTO Festival (year, start_date, end_date, location_id) VALUES "
            f"({year}, '{start_date}', '{end_date}', {location_id});\n"
        )

# Generate 30 Scenes
with open('3_bulk_scenes.sql', 'w', encoding='utf-8') as f:
    f.write(HEADER_MAP['3_bulk_scenes.sql'])
    for i in range(1, 31):
        cap = random.choice([400, 500, 600, 800, 1000])
        f.write(
            f"INSERT INTO Scene (name, description, max_capacity, equipment_info) VALUES "
            f"('Scene{i}', 'Description for Scene{i}', {cap}, 'Equipment info {i}');\n"
        )

# Generate 40 Artists (solo)
with open('4_bulk_artists.sql', 'w', encoding='utf-8') as f:
    f.write(HEADER_MAP['4_bulk_artists.sql'])
    for i in range(1, 41):
        dob = random_date(datetime(1980, 1, 1), datetime(2005, 12, 31))
        f.write(
            f"INSERT INTO Artist (name, stage_name, date_of_birth, website, instagram_profile) VALUES "
            f"('Artist{i}', 'Stage{i}', '{dob}', NULL, NULL);\n"
        )

# Generate 10 Bands
with open('5_bulk_bands.sql', 'w', encoding='utf-8') as f:
    f.write(HEADER_MAP['5_bulk_bands.sql'])
    for i in range(1, 11):
        formation = random_date(datetime(2000, 1, 1), datetime(2020, 12, 31))
        f.write(
            f"INSERT INTO Band (band_name, formation_date, website) VALUES "
            f"('Band{i}', '{formation}', NULL);\n"
        )

# Generate 50 Visitors (for 200 tickets)
with open('6_bulk_visitors.sql', 'w', encoding='utf-8') as f:
    f.write(HEADER_MAP['6_bulk_visitors.sql'])
    for i in range(1, 51):
        f.write(
            f"INSERT INTO Visitor (first_name, last_name, contact, age) VALUES "
            f"('Visitor{i}', 'Last{i}', 'v{i}@example.com', {random.randint(18, 65)});\n"
        )

# Generate 30 Events (festival_id 1-10, scene_id 1-30)
with open('7_bulk_events.sql', 'w', encoding='utf-8') as f:
    f.write(HEADER_MAP['7_bulk_events.sql'])
    for i in range(1, 31):
        festival_id = random.randint(1, 10)
        scene_id = i  # ensure scene_id exists (1-30)
        event_date = random_date(datetime(2017, 1, 1), datetime(2027, 12, 31))
        f.write(
            f"INSERT INTO Event (festival_id, scene_id, event_date) VALUES "
            f"({festival_id}, {scene_id}, '{event_date}');\n"
        )

# Generate 100 Performances (event_id 1-30, artist_id 1-40 or band_id 1-10)
with open('8_bulk_performances.sql', 'w', encoding='utf-8') as f:
    f.write(HEADER_MAP['8_bulk_performances.sql'])
    for i in range(1, 101):
        event_id = random.randint(1, 30)
        if random.choice([True, False]):
            artist_id = random.randint(1, 40)
            band_id = 'NULL'
        else:
            artist_id = 'NULL'
            band_id = random.randint(1, 10)
        perf_type = random.choice(['headline', 'warm up', 'Special guest'])
        start_time = f"{random.randint(16, 22):02d}:00"
        duration = f"0{random.randint(1, 2)}:00:00"
        break_duration = f"00:{random.randint(5, 20):02d}:00"
        f.write(
            f"INSERT INTO Performance (event_id, artist_id, band_id, performance_type, start_time, duration, break_duration) VALUES "
            f"({event_id}, {artist_id}, {band_id}, '{perf_type}', '{start_time}', '{duration}', '{break_duration}');\n"
        )

# Generate 200 Tickets (event_id 1-30, visitor_id 1-50, performance_id 1-100)
with open('9_bulk_tickets.sql', 'w', encoding='utf-8') as f:
    f.write(HEADER_MAP['9_bulk_tickets.sql'])
    for i in range(1, 201):
        event_id = random.randint(1, 30)
        visitor_id = random.randint(1, 50)
        purchase_date = random_date(datetime(2020, 1, 1), datetime(2027, 12, 31))
        cost = round(random.uniform(20, 120), 2)
        payment_method = random.choice(['credit card', 'debit card', 'bank transfer', 'not cash'])
        ean = 'NULL'  # Let trigger generate EAN
        ticket_category = random.choice(['VIP', 'Regular', 'Student'])
        used = random.randint(0, 1)
        performance_id = random.randint(1, 100)
        f.write(
            f"INSERT INTO Ticket (event_id, visitor_id, purchase_date, cost, payment_method, ean, ticket_category, used, performance_id) VALUES "
            f"({event_id}, {visitor_id}, '{purchase_date}', {cost}, '{payment_method}', {ean}, '{ticket_category}', {used}, {performance_id});\n"
        )
