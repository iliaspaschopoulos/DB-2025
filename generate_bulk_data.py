import random
from datetime import datetime, timedelta

def random_date(start, end):
    return (start + timedelta(days=random.randint(0, (end - start).days))).strftime('%Y-%m-%d')

# Generate 1000 Visitors
with open('bulk_visitors.sql', 'w', encoding='utf-8') as f:
    for i in range(1, 1001):
        f.write(
            f"INSERT INTO Visitor (first_name, last_name, contact, age) VALUES "
            f"('First{i}', 'Last{i}', 'contact{i}@example.com', {random.randint(18, 65)});\n"
        )

# Generate 30 Events (festival_id and scene_id assumed to exist)
with open('bulk_events.sql', 'w', encoding='utf-8') as f:
    for i in range(1, 31):
        festival_id = random.randint(1, 5)
        scene_id = random.randint(1, 5)
        event_date = random_date(datetime(2020, 1, 1), datetime(2024, 12, 31))
        f.write(
            f"INSERT INTO Event (festival_id, scene_id, event_date) VALUES "
            f"({festival_id}, {scene_id}, '{event_date}');\n"
        )

# Generate 100 Performances (event_id, artist_id or band_id)
with open('bulk_performances.sql', 'w', encoding='utf-8') as f:
    for i in range(1, 101):
        event_id = random.randint(1, 30)
        # Randomly decide if this is an artist or a band performance
        if random.choice([True, False]):
            artist_id = random.randint(1, 20)
            band_id = 'NULL'
        else:
            artist_id = 'NULL'
            band_id = random.randint(1, 10)
        perf_type = random.choice(['headline', 'warm up', 'Special guest'])
        start_time = f"{random.randint(16, 22):02d}:00"  # e.g., '20:00'
        duration = f"0{random.randint(1, 2)}:00:00"
        break_duration = f"00:{random.randint(5, 20):02d}:00"
        f.write(
            f"INSERT INTO Performance (event_id, artist_id, band_id, performance_type, start_time, duration, break_duration) VALUES "
            f"({event_id}, {artist_id}, {band_id}, '{perf_type}', '{start_time}', '{duration}', '{break_duration}');\n"
        )

# Generate 1100 Tickets (event_id, visitor_id, performance_id assumed to exist)
with open('bulk_tickets.sql', 'w', encoding='utf-8') as f:
    for i in range(1, 1101):
        event_id = random.randint(1, 30)
        visitor_id = random.randint(1, 1000)
        purchase_date = random_date(datetime(2020, 1, 1), datetime(2024, 12, 31))
        cost = round(random.uniform(20, 100), 2)
        payment_method = random.choice(['credit card', 'debit card', 'bank transfer', 'not cash'])
        ean = 1000000000000 + i
        ticket_category = random.choice(['VIP', 'Regular', 'Student'])  # Use 'Regular' instead of 'Standard'
        used = random.randint(0, 1)
        performance_id = random.randint(1, 100)
        f.write(
            f"INSERT INTO Ticket (event_id, visitor_id, purchase_date, cost, payment_method, ean, ticket_category, used, performance_id) VALUES "
            f"({event_id}, {visitor_id}, '{purchase_date}', {cost}, '{payment_method}', {ean}, '{ticket_category}', {used}, {performance_id});\n"
        )
