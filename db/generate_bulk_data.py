import random
from datetime import datetime, timedelta

INITIAL_SQL_COMMANDS = """\
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

"""

def random_date(start, end):
    return (start + timedelta(days=random.randint(0, (end - start).days))).strftime('%Y-%m-%d')

with open('load.sql', 'w', encoding='utf-8') as f:
    f.write(INITIAL_SQL_COMMANDS)  # Write initial cleanup and reseed commands

    # Initialize lists to store generated IDs for FK relationships
    generated_tickets = []  # Stores (ticket_id, event_id, visitor_id)
    generated_performances = []  # Stores (performance_id, event_id)
    scene_capacities = {}  # Stores scene_id: max_capacity
    event_to_scene_map = {}  # Stores event_id: scene_id
    
    # Initialize ID counters for tables with IDENTITY columns
    current_ticket_id = 1
    current_performance_id = 1

    # Location Data
    f.write('\n-- Locations\n')
    for i in range(1, 11):
        f.write(
            f"INSERT INTO Location (address, latitude, longitude, city, country, continent) VALUES "
            f"('Address{i}', {random.uniform(-90,90):.6f}, {random.uniform(-180,180):.6f}, 'City{i}', 'Country{i}', 'Continent{i}');\n"
        )

    # Festival Data
    f.write('\n-- Festivals\n')
    for i in range(1, 11):
        year = 2016 + i
        start_date = f"{year}-07-01"
        end_date = f"{year}-07-05"
        location_id = i
        f.write(
            f"INSERT INTO Festival (year, start_date, end_date, location_id) VALUES "
            f"({year}, '{start_date}', '{end_date}', {location_id});\n"
        )

    # Scene Data
    f.write('\n-- Scenes\n')
    for i in range(1, 31):  # Scene IDs will be 1-30
        cap = random.choice([400, 500, 600, 800, 1000])
        scene_capacities[i] = cap  # Store capacity by scene_id
        f.write(
            f"INSERT INTO Scene (name, description, max_capacity, equipment_info) VALUES "
            f"('Scene{i}', 'Description for Scene{i}', {cap}, 'Equipment info {i}');\n"
        )

    # Artist Data
    f.write('\n-- Artists\n')
    for i in range(1, 41):
        dob = random_date(datetime(1980, 1, 1), datetime(2005, 12, 31))
        f.write(
            f"INSERT INTO Artist (name, stage_name, date_of_birth, website, instagram_profile) VALUES "
            f"('Artist{i}', 'Stage{i}', '{dob}', NULL, NULL);\n"
        )

    # Band Data
    f.write('\n-- Bands\n')
    for i in range(1, 11):
        formation = random_date(datetime(2000, 1, 1), datetime(2020, 12, 31))
        f.write(
            f"INSERT INTO Band (band_name, formation_date, website) VALUES "
            f"('Band{i}', '{formation}', NULL);\n"
        )

    # Artist Genres
    f.write('\n-- Artist Genres\n')
    genres = ['Rock', 'Pop', 'Electronic', 'Jazz', 'Classical', 'Hip Hop', 'Indie', 'Metal', 'Folk', 'Reggae']
    subgenres = {
        'Rock': ['Alternative Rock', 'Hard Rock', 'Punk Rock', 'Progressive Rock'],
        'Pop': ['Synth-pop', 'Indie Pop', 'Dance Pop'],
        'Electronic': ['Techno', 'House', 'Trance', 'Ambient', 'Drum and Bass'],
        'Metal': ['Heavy Metal', 'Thrash Metal', 'Death Metal', 'Black Metal'],
        'Hip Hop': ['Old School', 'Trap', 'Conscious Hip Hop']
    }
    # Artists are generated with IDs 1 to 40
    for artist_id_val in range(1, 41):
        # Assign 1 to 3 genres per artist
        num_genres_for_artist = random.randint(1, 3)
        assigned_genres_for_this_artist = set()  # To avoid assigning the same genre twice to one artist

        for _ in range(num_genres_for_artist):
            genre = random.choice(genres)
            while genre in assigned_genres_for_this_artist:  # Ensure unique genre for this artist
                genre = random.choice(genres)
            assigned_genres_for_this_artist.add(genre)

            subgenre_val = 'NULL'
            # 50% chance of having a subgenre if applicable
            if genre in subgenres and random.choice([True, False]):
                subgenre_val = f"'{random.choice(subgenres[genre])}'"
            
            f.write(
                f"INSERT INTO Artist_Genre (artist_id, genre, subgenre) VALUES "
                f"({artist_id_val}, '{genre}', {subgenre_val});\n"
            )

    # Visitor Data
    f.write('\n-- Visitors\n')
    for i in range(1, 51):
        f.write(
            f"INSERT INTO Visitor (first_name, last_name, contact, age) VALUES "
            f"('Visitor{i}', 'Last{i}', 'v{i}@example.com', {random.randint(18, 65)});\n"
        )

    # Event Data
    f.write('\n-- Events\n')
    for i in range(1, 31):  # Event IDs will be 1-30
        festival_id = random.randint(1, 10)
        scene_id_for_event = i  # Assign scene_id sequentially for simplicity
        event_to_scene_map[i] = scene_id_for_event  # Store event to scene mapping
        event_date = random_date(datetime(2017, 1, 1), datetime(2027, 12, 31))
        f.write(
            f"INSERT INTO Event (festival_id, scene_id, event_date) VALUES "
            f"({festival_id}, {scene_id_for_event}, '{event_date}');\n"
        )

    # Performance Data
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
        generated_performances.append((current_performance_id, event_id))
        current_performance_id += 1

    # Ticket Data
    f.write('\n-- Tickets\n')
    used_pairs = set()
    ticket_count = 0
    event_ticket_counts = {i: 0 for i in range(1, 31)}
    while ticket_count < 201:
        event_id = random.randint(1, 30)
        visitor_id = random.randint(1, 50)
        if (event_id, visitor_id) in used_pairs:
            continue
        used_pairs.add((event_id, visitor_id))
        event_ticket_counts[event_id] += 1
        purchase_date = random_date(datetime(2020, 1, 1), datetime(2027, 12, 31))
        cost = round(random.uniform(20, 120), 2)
        payment_method = random.choice(['credit card', 'debit card', 'bank transfer', 'not cash'])
        ean = 'NULL'
        ticket_category = random.choice(['VIP', 'Regular', 'Student'])
        used = random.randint(0, 1)
        f.write(
            f"INSERT INTO Ticket (event_id, visitor_id, purchase_date, cost, payment_method, ean, ticket_category, used) VALUES "
            f"({event_id}, {visitor_id}, '{purchase_date}', {cost}, '{payment_method}', {ean}, '{ticket_category}', {used});\n"
        )
        generated_tickets.append((current_ticket_id, event_id, visitor_id))
        current_ticket_id += 1
        ticket_count += 1

    # Staff Data
    f.write('\n-- Staff\n')
    roles = ['Technician', 'Security', 'Helper', 'Manager']
    experience_levels = ['ειδικευόμενος', 'αρχάριος', 'μέσος', 'έμπειρος', 'πολύ έμπειρος']
    total_staff_generated = 200  # Increased from 100
    for i in range(1, total_staff_generated + 1):
        f.write(
            f"INSERT INTO Staff (name, age, role, experience_level) VALUES "
            f"('Staff{i}', {random.randint(20, 60)}, N'{random.choice(roles)}', N'{random.choice(experience_levels)}');\n"
        )

    # Event Staff Data
    f.write('\n-- Event_Staff\n')
    master_event_staff_assignments = set()

    for event_id_loop_idx in range(1, 31):  # For Event IDs 1-30
        current_event_id = event_id_loop_idx
        current_scene_id = event_to_scene_map.get(current_event_id)

        if current_scene_id is None or current_scene_id not in scene_capacities:
            print(f"Warning: Scene ID for event {current_event_id} not found or capacity unknown. Skipping staff assignment.")
            continue
            
        capacity_of_scene = scene_capacities[current_scene_id]

        all_staff_for_this_event_scene_values = []  # To hold all formatted values like (event,scene,staff,category)

        # --- Security Staff ---
        security_staff_values_for_event = []
        num_security_raw = max(1, int(capacity_of_scene * 0.05 + 0.9999))  # Ensure at least 1, round up 5%
        total_security_needed = num_security_raw

        potential_staff_ids_pool = list(range(1, total_staff_generated + 1)) 
        random.shuffle(potential_staff_ids_pool)
        
        assigned_security_count_for_event = 0
        temp_assigned_staff_for_this_event_security = []

        for staff_id_for_security in potential_staff_ids_pool:
            if assigned_security_count_for_event >= total_security_needed:
                break
            
            assignment_tuple = (current_event_id, current_scene_id, staff_id_for_security, 'security')
            if assignment_tuple not in master_event_staff_assignments:
                temp_assigned_staff_for_this_event_security.append(staff_id_for_security)
                assigned_security_count_for_event += 1
        
        if assigned_security_count_for_event < total_security_needed:
            print(f"Warning: For Event {current_event_id} (Scene {current_scene_id}, Capacity {capacity_of_scene}), needed {total_security_needed} security, assigned {assigned_security_count_for_event} due to limited unique staff available from the pool for this specific assignment.")

        for staff_id_to_add in temp_assigned_staff_for_this_event_security:
            assignment_tuple = (current_event_id, current_scene_id, staff_id_to_add, 'security')
            master_event_staff_assignments.add(assignment_tuple)
            all_staff_for_this_event_scene_values.append(f"({current_event_id}, {current_scene_id}, {staff_id_to_add}, 'security')")

        # --- Auxiliary Staff ---
        num_auxiliary_raw = max(1, int(capacity_of_scene * 0.02 + 0.9999))  # Ensure at least 1, round up 2%
        total_auxiliary_needed = num_auxiliary_raw
        
        assigned_auxiliary_count_for_event = 0
        random.shuffle(potential_staff_ids_pool)  # Re-shuffle for variety
        temp_assigned_staff_for_this_event_auxiliary = []

        for staff_id_for_auxiliary in potential_staff_ids_pool:
            if assigned_auxiliary_count_for_event >= total_auxiliary_needed:
                break
            
            assignment_tuple = (current_event_id, current_scene_id, staff_id_for_auxiliary, 'auxiliary')
            if assignment_tuple not in master_event_staff_assignments:
                temp_assigned_staff_for_this_event_auxiliary.append(staff_id_for_auxiliary)
                assigned_auxiliary_count_for_event += 1

        if assigned_auxiliary_count_for_event < total_auxiliary_needed:
            print(f"Warning: For Event {current_event_id} (Scene {current_scene_id}, Capacity {capacity_of_scene}), needed {total_auxiliary_needed} auxiliary, assigned {assigned_auxiliary_count_for_event} due to limited unique staff.")

        for staff_id_to_add in temp_assigned_staff_for_this_event_auxiliary:
            assignment_tuple = (current_event_id, current_scene_id, staff_id_to_add, 'auxiliary')
            master_event_staff_assignments.add(assignment_tuple)
            all_staff_for_this_event_scene_values.append(f"({current_event_id}, {current_scene_id}, {staff_id_to_add}, 'auxiliary')")

        # --- Technical Staff ---
        num_technical_needed = random.randint(1, 3)
        assigned_technical_count_for_event = 0
        random.shuffle(potential_staff_ids_pool)  # Re-shuffle for variety
        temp_assigned_staff_for_this_event_technical = []

        for staff_id_for_technical in potential_staff_ids_pool:
            if assigned_technical_count_for_event >= num_technical_needed:
                break
            
            assignment_tuple = (current_event_id, current_scene_id, staff_id_for_technical, 'technical')
            if assignment_tuple not in master_event_staff_assignments:
                temp_assigned_staff_for_this_event_technical.append(staff_id_for_technical)
                assigned_technical_count_for_event += 1

        for staff_id_to_add in temp_assigned_staff_for_this_event_technical:
            assignment_tuple = (current_event_id, current_scene_id, staff_id_to_add, 'technical')
            master_event_staff_assignments.add(assignment_tuple)
            all_staff_for_this_event_scene_values.append(f"({current_event_id}, {current_scene_id}, {staff_id_to_add}, 'technical')")

        # After collecting all staff categories for the current event_id and scene_id
        if all_staff_for_this_event_scene_values:
            f.write(
                f"INSERT INTO Event_Staff (event_id, scene_id, staff_id, staff_category) VALUES "
                f"{', '.join(all_staff_for_this_event_scene_values)};\n"
            )

    # Website Data
    f.write('\n-- Websites\n')
    for i in range(1, 11):
        festival_id = i
        f.write(
            f"INSERT INTO Website (url, festival_id, image_url, description) VALUES "
            f"('http://festival{festival_id}.example.com', {festival_id}, 'http://festival{festival_id}.example.com/image.jpg', 'Official website for Festival {festival_id}');\n"
        )

    # Rating Data
    f.write('\n-- Ratings\n')
    if generated_tickets and generated_performances:
        event_to_performances = {}
        for perf_id, perf_event_id in generated_performances:
            if perf_event_id not in event_to_performances:
                event_to_performances[perf_event_id] = []
            event_to_performances[perf_event_id].append(perf_id)

        num_ratings_to_generate = 50
        ratings_generated_count = 0

        eligible_tickets_for_rating = [
            ticket for ticket in generated_tickets 
            if ticket[1] in event_to_performances and event_to_performances[ticket[1]]
        ]
        random.shuffle(eligible_tickets_for_rating)

        if not eligible_tickets_for_rating:
            f.write("-- No eligible ticket/performance pairs for ratings.\n")
        else:
            for _ in range(min(num_ratings_to_generate * 2, len(eligible_tickets_for_rating) * 5)): 
                if ratings_generated_count >= num_ratings_to_generate:
                    break

                ticket_info = random.choice(eligible_tickets_for_rating)
                ticket_id, event_id_for_ticket, visitor_id_for_ticket = ticket_info
                
                if event_id_for_ticket in event_to_performances and event_to_performances[event_id_for_ticket]:
                    performance_id_for_rating = random.choice(event_to_performances[event_id_for_ticket])
                    
                    inter_score = random.randint(1, 5)
                    sound_score = random.randint(1, 5)
                    stage_score = random.randint(1, 5)
                    org_score = random.randint(1, 5)
                    overall_score = random.randint(1, 5) 
                    rating_date_val = random_date(datetime(2021, 1, 1), datetime(2028, 12, 31))

                    f.write(
                        f"INSERT INTO Rating (ticket_id, performance_id, visitor_id, interpretation_score, sound_lighting_score, stage_presence_score, organization_score, overall_score, rating_date) VALUES "
                        f"({ticket_id}, {performance_id_for_rating}, {visitor_id_for_ticket}, {inter_score}, {sound_score}, {stage_score}, {org_score}, {overall_score}, '{rating_date_val}');\n"
                    )
                    ratings_generated_count += 1
    else:
        f.write("-- Not enough data (tickets/performances) to generate ratings.\n")

    # Resale Queue Data
    f.write('\n-- Resale_Queue\n')
    if generated_tickets:
        num_resales_to_generate = 20
        resales_generated_count = 0
        
        available_tickets_for_resale = list(generated_tickets)
        random.shuffle(available_tickets_for_resale)

        if not available_tickets_for_resale:
            f.write("-- No tickets available to list for resale.\n")
        else:
            for ticket_data in available_tickets_for_resale:
                if resales_generated_count >= num_resales_to_generate:
                    break

                ticket_id_for_resale, _, seller_id_for_resale = ticket_data
                
                buyer_id_for_resale = random.randint(1, 50)
                while buyer_id_for_resale == seller_id_for_resale:
                    buyer_id_for_resale = random.randint(1, 50)
                    if len(set(range(1,51)) - {seller_id_for_resale}) == 0:
                        break 
                if buyer_id_for_resale == seller_id_for_resale and len(set(range(1,51)) - {seller_id_for_resale}) == 0:
                    continue

                listing_date_val = random_date(datetime(2021, 1, 1), datetime(2028, 12, 31))
                resale_status_val = random.choice(['Pending', 'Completed'])
                
                f.write(
                    f"INSERT INTO Resale_Queue (ticket_id, seller_id, buyer_id, listing_date, resale_status, fifo_order) VALUES "
                    f"({ticket_id_for_resale}, {seller_id_for_resale}, {buyer_id_for_resale}, '{listing_date_val}', '{resale_status_val}', NULL);\n"
                )
                resales_generated_count += 1
    else:
        f.write("-- No tickets available to generate resale queue entries.\n")

    # Band Member Data
    f.write('\n-- Band_Member Data -- Corrected to include role and join_date\n')
    roles = ['Vocalist', 'Guitarist', 'Bassist', 'Drummer', 'Keyboardist']
    for i in range(1, 21): # Assuming 20 band members for 10 bands, 40 artists
        band_id = (i - 1) % 10 + 1
        artist_id = i # Assign first 20 artists to bands
        member_role = random.choice(roles)
        # Join date can be after band formation and artist DOB, for simplicity, a random recent date
        join_date_val = random_date(datetime(2010, 1, 1), datetime(2023, 12, 31))
        f.write(f"INSERT INTO Band_Member (band_id, artist_id, role, join_date) VALUES ({band_id}, {artist_id}, '{member_role}', '{join_date_val}');\n")

    f.write('\n-- End of bulk data generation.\n')

print("load.sql generated successfully.")
