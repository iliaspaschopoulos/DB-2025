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

with open('load.sql', 'w', encoding='utf-8') as f:
    # 1_bulk_locations.sql
    f.write(HEADER_MAP['1_bulk_locations.sql'])
    for i in range(1, 11):
        f.write(
            f"INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES "
            f"('Address{i}', {random.uniform(-90,90):.6f}, {random.uniform(-180,180):.6f}, 'City{i}', 'Country{i}', 'Continent{i}');\n"
        )

    # 2_bulk_festivals.sql
    f.write('\n-- Festivals\n')
    # 10 festivals, location_id 1-10, festival_id will be 1-10
    for i in range(1, 11):  # i from 1 to 10
        year = 2016 + i  # years 2017-2026
        start_date = f"{year}-07-01"
        end_date = f"{year}-07-05"
        location_id = i  # 1-10, matches generated locations
        f.write(
            f"INSERT INTO Festival (year, start_date, end_date, location_id) VALUES "
            f"({year}, '{start_date}', '{end_date}', {location_id});\n"
        )

    # 3_bulk_scenes.sql
    f.write('\n-- Scenes\n')
    for i in range(1, 31):
        cap = random.choice([400, 500, 600, 800, 1000])
        f.write(
            f"INSERT INTO Scene (name, description, max_capacity, equipment_info) VALUES "
            f"('Scene{i}', 'Description for Scene{i}', {cap}, 'Equipment info {i}');\n"
        )

    # 4_bulk_artists.sql
    f.write('\n-- Artists\n')
    for i in range(1, 41):
        dob = random_date(datetime(1980, 1, 1), datetime(2005, 12, 31))
        f.write(
            f"INSERT INTO Artist (name, stage_name, date_of_birth, website, instagram_profile) VALUES "
            f"('Artist{i}', 'Stage{i}', '{dob}', NULL, NULL);\n"
        )

    # 5_bulk_bands.sql
    f.write('\n-- Bands\n')
    for i in range(1, 11):
        formation = random_date(datetime(2000, 1, 1), datetime(2020, 12, 31))
        f.write(
            f"INSERT INTO Band (band_name, formation_date, website) VALUES "
            f"('Band{i}', '{formation}', NULL);\n"
        )

    # 6_bulk_visitors.sql
    f.write('\n-- Visitors\n')
    for i in range(1, 51):
        f.write(
            f"INSERT INTO Visitor (first_name, last_name, contact, age) VALUES "
            f"('Visitor{i}', 'Last{i}', 'v{i}@example.com', {random.randint(18, 65)});\n"
        )

    # 7_bulk_events.sql
    f.write('\n-- Events\n')
    for i in range(1, 31):
        festival_id = random.randint(1, 10)  # ensure festival_id is 1-10 (matches number of festivals)
        scene_id = i  # ensure scene_id exists (1-30)
        event_date = random_date(datetime(2017, 1, 1), datetime(2027, 12, 31))
        f.write(
            f"INSERT INTO Event (festival_id, scene_id, event_date) VALUES "
            f"({festival_id}, {scene_id}, '{event_date}');\n"
        )

    # 8_bulk_performances.sql
    f.write('\n-- Performances\n')
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

    # 9_bulk_tickets.sql
    f.write('\n-- Tickets\n')
    used_pairs = set()
    ticket_count = 0
    event_ticket_counts = {i: 0 for i in range(1, 31)}
    while ticket_count < 201:
        event_id = random.randint(1, 30)
        visitor_id = random.randint(1, 50)
        if (event_id, visitor_id) in used_pairs:
            continue  # skip duplicates
        used_pairs.add((event_id, visitor_id))
        event_ticket_counts[event_id] += 1
        purchase_date = random_date(datetime(2020, 1, 1), datetime(2027, 12, 31))
        cost = round(random.uniform(20, 120), 2)
        payment_method = random.choice(['credit card', 'debit card', 'bank transfer', 'not cash'])
        ean = 'NULL'  # Let trigger generate EAN
        ticket_category = random.choice(['VIP', 'Regular', 'Student'])
        used = random.randint(0, 1)
        f.write(
            f"INSERT INTO Ticket (event_id, visitor_id, purchase_date, cost, payment_method, ean, ticket_category, used) VALUES "
            f"({event_id}, {visitor_id}, '{purchase_date}', {cost}, '{payment_method}', {ean}, '{ticket_category}', {used});\n"
        )
        ticket_count += 1

    # 10_bulk_staff.sql
    f.write('\n-- Staff\n')
    roles = ['Technician', 'Security', 'Helper', 'Manager']
    experience_levels = ['ειδικευόμενος', 'αρχάριος', 'μέσος', 'έμπειρος', 'πολύ έμπειρος']
    for i in range(1, 21):
        f.write(
            f"INSERT INTO Staff (name, age, role, experience_level) VALUES "
            f"('Staff{i}', {random.randint(20, 60)}, '{random.choice(roles)}', '{random.choice(experience_levels)}');\n"
        )

    # 11_bulk_event_staff.sql
    f.write('\n-- Event_Staff\n')
    staff_categories = ['technical', 'security', 'auxiliary']
    assigned_staff = set()  # (event_id, scene_id, staff_id)
    for event_id in range(1, 31):
        scene_id = event_id  # as above
        # At least 5% of total tickets for this event as security staff (rounded up, at least 1)
        num_security = max(1, int(event_ticket_counts[event_id] * 0.05 + 0.9999))
        security_staff_ids = set()
        # Ensure unique staff_id for security staff in this event/scene
        available_staff_ids = list(range(1, 21))
        random.shuffle(available_staff_ids)
        for idx in range(num_security):
            if not available_staff_ids:
                # If we run out of unique staff, reuse from the pool (should not happen with 20 staff)
                staff_id = random.randint(1, 20)
            else:
                staff_id = available_staff_ids.pop()
            security_staff_ids.add(staff_id)
            assigned_staff.add((event_id, scene_id, staff_id))
            f.write(
                f"INSERT INTO Event_Staff (event_id, scene_id, staff_id, staff_category) VALUES "
                f"({event_id}, {scene_id}, {staff_id}, 'security');\n"
            )
        # Add some technical and auxiliary staff for variety (optional)
        for _ in range(2):
            staff_id = random.randint(1, 20)
            # Avoid duplicating staff assignment for this event/scene
            while (event_id, scene_id, staff_id) in assigned_staff:
                staff_id = random.randint(1, 20)
            staff_category = random.choice(['technical', 'auxiliary'])
            assigned_staff.add((event_id, scene_id, staff_id))
            f.write(
                f"INSERT INTO Event_Staff (event_id, scene_id, staff_id, staff_category) VALUES "
                f"({event_id}, {scene_id}, {staff_id}, '{staff_category}');\n"
            )

    # 12_bulk_rating.sql
    f.write('\n-- Rating\n')
    for i in range(1, 51):
        ticket_id = i
        performance_id = random.randint(1, 100)
        visitor_id = random.randint(1, 50)
        interpretation_score = random.randint(1, 5)
        sound_lighting_score = random.randint(1, 5)
        stage_presence_score = random.randint(1, 5)
        organization_score = random.randint(1, 5)
        overall_score = random.randint(1, 5)
        rating_date = random_date(datetime(2021, 1, 1), datetime(2027, 12, 31))
        f.write(
            f"INSERT INTO Rating (ticket_id, performance_id, visitor_id, interpretation_score, sound_lighting_score, stage_presence_score, organization_score, overall_score, rating_date) VALUES "
            f"({ticket_id}, {performance_id}, {visitor_id}, {interpretation_score}, {sound_lighting_score}, {stage_presence_score}, {organization_score}, {overall_score}, '{rating_date}');\n"
        )

    # 13_bulk_website.sql
    f.write('\n-- Website\n')
    for i in range(1, 11):
        url = f"https://festival{i}.example.com"
        festival_id = i
        image_url = f"https://images.example.com/festival{i}.jpg"
        description = f"Description for Festival {i}"
        f.write(
            f"INSERT INTO Website (url, festival_id, image_url, description) VALUES "
            f"('{url}', {festival_id}, '{image_url}', N'{description}');\n"
        )

    # 14_bulk_resale_queue.sql
    f.write('\n-- Resale_Queue\n')
    for i in range(1, 21):
        ticket_id = random.randint(1, 201)
        seller_id = random.randint(1, 50)
        buyer_id = random.randint(1, 50)
        listing_date = random_date(datetime(2022, 1, 1), datetime(2027, 12, 31))
        resale_status = random.choice(['Pending', 'Completed'])
        fifo_order = i
        f.write(
            f"INSERT INTO Resale_Queue (ticket_id, seller_id, buyer_id, listing_date, resale_status, fifo_order) VALUES "
            f"({ticket_id}, {seller_id}, {buyer_id}, '{listing_date}', '{resale_status}', {fifo_order});\n"
        )

    # 15_bulk_artist_genre.sql
    f.write('\n-- Artist_Genre\n')
    genres = ['Rock', 'Pop', 'Jazz', 'Classical', 'Hip-Hop']
    for i in range(1, 41):
        genre = random.choice(genres)
        subgenre = random.choice(['', 'Alternative', 'Fusion', 'Modern', 'Classic'])
        f.write(
            f"INSERT INTO Artist_Genre (artist_id, genre, subgenre) VALUES "
            f"({i}, '{genre}', '{subgenre}');\n"
        )

    # 16_bulk_band_member.sql
    f.write('\n-- Band_Member\n')
    for i in range(1, 11):
        members = random.sample(range(1, 41), 4)
        for artist_id in members:
            f.write(
                f"INSERT INTO Band_Member (band_id, artist_id) VALUES ({i}, {artist_id});\n"
            )
