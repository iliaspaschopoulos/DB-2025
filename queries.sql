-- b1: Βρείτε τα έσοδα του φεστιβάλ, ανά έτος από την πώληση εισιτηρίων, λαμβάνοντας υπόψη όλες τις κατηγορίες εισιτηρίων και παρέχοντας ανάλυση ανά είδος πληρωμής.
SELECT f.year,
    t.payment_method,
    SUM(t.cost) AS total_revenue
FROM Ticket t
    JOIN Event e ON t.event_id = e.event_id
    JOIN Festival f ON e.festival_id = f.festival_id
GROUP BY f.year, t.payment_method
ORDER BY f.year, t.payment_method;

-- b2: Βρείτε όλους τους καλλιτέχνες που ανήκουν σε ένα συγκεκριμένο μουσικό είδος με ένδειξη αν συμμετείχαν σε εκδηλώσεις του φεστιβάλ για το συγκεκριμένο έτος ;
SELECT
    ag.genre,
    a.artist_id,
    a.name
FROM artist_genre ag JOIN artist a

    ON ag.artist_id = a.artist_id

ORDER BY 
    ag.genre, a.name;

-- b3: Βρείτε ποιοι καλλιτέχνες έχουν εμφανιστεί ως warm up περισσότερες από 2 φορές στο ίδιο φεστιβάλ;
SELECT
    Artist.name AS artist_name,
    Festival.year AS festival_year,
    COUNT(Performance.performance_id) AS warm_up_count
FROM Performance

    JOIN Event ON Performance.event_id = Event.event_id

    JOIN Festival ON Event.festival_id = Festival.festival_id

    JOIN Artist ON Performance.artist_id = Artist.artist_id

WHERE 
    Performance.performance_type = 'warm up'

GROUP BY 
    Artist.name, Festival.year

HAVING 
    COUNT(Performance.performance_id) > 2;

-- B4: Για κάποιο καλλιτέχνη, βρείτε το μέσο όρο αξιολογήσεων (Ερμηνεία καλλιτεχνών) και εμφάνιση (Συνολική εντύπωση).
-- PREPI NA DOKIMASTOUN DIAFORA JOIN OPOS LEI O ASTERISKOS.
-- H MSSQL DEN EXEI FORCE INDEX OPOTE DEN TREXEI H PROTH MORFH.
-- SELECT 
   -- a.artist_id,
   -- a.name,
    -- ROUND(AVG((r.interpretation_score + r.sound_lighting_score + 
                -- r.stage_presence_score + r.organization_score )/4
                -- ), 2) AS average_rating,
	-- ROUND(AVG(r.overall_score), 2) AS overall_score
-- FROM 
    -- Rating r FORCE INDEX (idx_rating_performance_id)
    
    
-- JOIN 
    -- Performance p FORCE INDEX (idx_performance_artist_id) ON r.performance_id = p.performance_id
-- JOIN 
    -- Artist a ON p.artist_id = a.artist_id
    
-- WHERE a.artist_id = 3

-- GROUP BY
-- a.artist_id, a.name;
-- -------------------------------------------------------------------
    SELECT 
   a.artist_id,
   a.name,
    ROUND(AVG((r.interpretation_score + r.sound_lighting_score + 
                r.stage_presence_score + r.organization_score) / 4.0), 2) AS average_rating,
	ROUND(AVG(r.overall_score), 2) AS overall_score
FROM 
    Rating r
JOIN 
    Performance p ON r.performance_id = p.performance_id
JOIN 
    Artist a ON p.artist_id = a.artist_id
WHERE 
    a.artist_id = 3
GROUP BY
   a.artist_id, a.name;

-- b6: Για κάποιο επισκέπτη, βρείτε τις παραστάσεις που έχει παρακολουθήσει και το μέσο όρο της αξιολόγησης του, ανά παράσταση.
-- Αντικαταστήστε το 1 με το visitor_id που σας ενδιαφέρει.
SELECT
    p.performance_id,
    AVG(
        (r.interpretation_score + r.sound_lighting_score + r.stage_presence_score + r.organization_score) / 4.0
    ) AS avg_rating_by_visitor
FROM Ticket t
JOIN Performance p ON t.performance_id = p.performance_id
LEFT JOIN Rating r ON r.performance_id = p.performance_id AND r.visitor_id = t.visitor_id
WHERE t.visitor_id = 1
GROUP BY p.performance_id;

-- Commands completed successfully.
-- Total execution time: 00:00:00.214

-- --- Εναλλακτικές στρατηγικές JOIN (SQL Server syntax) ---
-- Nested Loops Join
-- OPTION (LOOP JOIN)
SELECT
    p.performance_id,
    AVG(
        (r.interpretation_score + r.sound_lighting_score + r.stage_presence_score + r.organization_score) / 4.0
    ) AS avg_rating_by_visitor
FROM Ticket t
JOIN Performance p ON t.performance_id = p.performance_id
LEFT JOIN Rating r ON r.performance_id = p.performance_id AND r.visitor_id = t.visitor_id
WHERE t.visitor_id = 1
GROUP BY p.performance_id
OPTION (LOOP JOIN);

-- Commands completed successfully.
-- Total execution time: 00:00:00.172

-- Hash Join
-- OPTION (HASH JOIN)
SELECT
    p.performance_id,
    AVG(
        (r.interpretation_score + r.sound_lighting_score + r.stage_presence_score + r.organization_score) / 4.0
    ) AS avg_rating_by_visitor
FROM Ticket t
JOIN Performance p ON t.performance_id = p.performance_id
LEFT JOIN Rating r ON r.performance_id = p.performance_id AND r.visitor_id = t.visitor_id
WHERE t.visitor_id = 1
GROUP BY p.performance_id
OPTION (HASH JOIN);

-- Commands completed successfully.
-- Total execution time: 00:00:00.178

-- Merge Join
-- OPTION (MERGE JOIN)
SELECT
    p.performance_id,
    AVG(
        (r.interpretation_score + r.sound_lighting_score + r.stage_presence_score + r.organization_score) / 4.0
    ) AS avg_rating_by_visitor
FROM Ticket t
JOIN Performance p ON t.performance_id = p.performance_id
LEFT JOIN Rating r ON r.performance_id = p.performance_id AND r.visitor_id = t.visitor_id
WHERE t.visitor_id = 1
GROUP BY p.performance_id
OPTION (MERGE JOIN);

-- Commands completed successfully.
-- Total execution time: 00:00:00.191

-- --- Traces & Συμπεράσματα ---
-- Εκτελέσαμε τα παραπάνω queries με SET STATISTICS IO ON, SET STATISTICS TIME ON (SQL Server).
-- Συλλέξαμε τα execution plans και τα στατιστικά (elapsed time).
-- Συνήθως:
-- - Το LOOP JOIN είναι καλό για μικρούς πίνακες ή όταν το εξωτερικό αποτέλεσμα είναι μικρό.
-- - Το HASH JOIN είναι αποδοτικό για μεγάλα, μη ταξινομημένα σύνολα.
-- - Το MERGE JOIN απαιτεί ταξινομημένα δεδομένα αλλά είναι πολύ αποδοτικό σε τέτοιες περιπτώσεις.
-- - Η χρήση κατάλληλων index μπορεί να βελτιώσει σημαντικά την απόδοση.
--  Ως αναμενόμενο, το LOOP JOIN είναι πιο αποδοτικό, καθώς οι πίνακες που έχουμε φτιάξει είναι μικροί.
--  Το αρχείο generate_bulk_data.py μπορεί να χρησιμοποιηθεί για να δημιουργήσει δεδομένα για τους πίνακες και να τους κάνει μεγαλύτερους.
--  θα μπορούσε να χρησιμοποιηθεί για την δοκιμή των παραπάνω στρατηγικών join. 

-- b7: Βρείτε ποιο φεστιβάλ είχε τον χαμηλότερο μέσο όρο εμπειρίας τεχνικού προσωπικού;
SELECT TOP 1
    f.festival_id,
    ROUND(AVG(
        CASE s.experience_level
            WHEN 'ειδικευόμενος' THEN 1
            WHEN 'αρχάριος' THEN 2
            WHEN 'μέσος' THEN 3
            WHEN 'έμπειρος' THEN 4
            WHEN 'πολύ έμπειρος' THEN 5
        END
    ), 2) AS avg_experience_score  
FROM Festival f
JOIN Event e ON f.festival_id = e.festival_id
JOIN Event_Staff es ON e.event_id = es.event_id
JOIN Staff s ON es.staff_id = s.staff_id
GROUP BY f.festival_id
ORDER BY avg_experience_score ASC;


-- b11: Βρείτε όλους τους καλλιτέχνες που συμμετείχαν τουλάχιστον 5 λιγότερες φορές από τον καλλιτέχνη με τις
--περισσότερες συμμετοχές σε φεστιβάλ.
WITH ArtistParticipation AS (
    SELECT 
        Artist.artist_id,
        Artist.name AS artist_name,
        COUNT(DISTINCT Festival.festival_id) AS total_participations
    FROM Performance
    JOIN Artist ON Artist.artist_id = Performance.artist_id
    JOIN Event ON Performance.event_id = Event.event_id
    JOIN Festival ON Event.festival_id = Festival.festival_id
    GROUP BY Artist.artist_id, Artist.name
),
MaxParticipation AS (
    SELECT MAX(total_participations) AS max_participations
    FROM ArtistParticipation
)
SELECT 
    ap.artist_id,
    ap.artist_name,
    ap.total_participations
FROM ArtistParticipation ap
JOIN MaxParticipation mp ON 1 = 1
WHERE ap.total_participations <= mp.max_participations - 5;

-- b9:  Βρείτε ποιοι επισκέπτες έχουν παρακολουθήσει τον ίδιο αριθμό παραστάσεων σε διάστημα ενός έτους με περισσότερες από 3 παρακολουθήσεις;
WITH yearly_counts AS (
   SELECT
       t.visitor_id,
       YEAR(e.event_date) AS year,
       COUNT(DISTINCT t.event_id) AS num_events
   FROM Ticket t
   JOIN Event e ON t.event_id = e.event_id
   GROUP BY t.visitor_id, YEAR(e.event_date)
),
filtered_counts AS (
   SELECT *
   FROM yearly_counts
   WHERE num_events >= 3
)
SELECT DISTINCT a.visitor_id, a.year, a.num_events
FROM filtered_counts a
JOIN filtered_counts b
  ON a.year = b.year
 AND a.num_events = b.num_events
 AND a.visitor_id <> b.visitor_id
ORDER BY a.year, a.num_events, a.visitor_id;

-- B12: Βρείτε το προσωπικό που απαιτείται για κάθε ημέρα του φεστιβάλ, παρέχοντας ανάλυση ανά κατηγορία (τεχνικό προσωπικό ασφαλείας, βοηθητικό προσωπικό);
 with total_visits AS(
SELECT 
e.event_id,
COUNT(t.ticket_id) AS total_tickets
FROM Event e
JOIN Ticket t on e.event_id = t.event_id
GROUP BY e.event_id
),
Security_Personnel AS (
	SELECT 
		tv.event_id,
		CEIL(tv.total_tickets * 0.05) AS num_of_security
	FROM total_visits tv
),
auxiliary_Personnel AS (
	SELECT 
		tv.event_id,
		CEIL(tv.total_tickets * 0.02) AS num_of_auxiliary
	FROM total_visits tv
)
SELECT 
    sp.event_id,
    sp.num_of_security,
    sec.num_of_auxiliary,
    20 AS num_of_technical
FROM Security_Personnel sp
JOIN auxiliary_Personnel sec ON sp.event_id = sec.event_id
ORDER BY sp.event_id;

-- b13: Βρείτε τους καλλιτέχνες που έχουν συμμετάσχει σε φεστιβάλ σε τουλάχιστον 3 διαφορετικές ηπείρους.
WITH Different_Continents AS (
   SELECT
       a.artist_id,
       a.name,
       COUNT(DISTINCT l.continent) AS distinct_continents
   FROM festival f
   JOIN location l ON f.location_id = l.location_id
   JOIN event e ON f.festival_id = e.festival_id
   JOIN performance p ON p.event_id = e.event_id
   JOIN artist a ON p.artist_id = a.artist_id
   GROUP BY a.artist_id
),
filtered_counts AS (
   SELECT *
   FROM Different_Continents
   WHERE distinct_continents >= 3
)
SELECT * FROM filtered_counts;

--b8: Βρείτε το προσωπικό υποστήριξης που δεν έχει προγραμματισμένη εργασία σε συγκεκριμένη ημερομηνία;
WITH All_Posibol_combinations AS(
SELECT
	e.event_date,
	es.staff_id

FROM Event e
CROSS JOIN Event_Staff es
where es.staff_category = 'auxiliary'
ORDER BY e.event_date
),
Asigned_dates AS(
SELECT
	e.event_date,
	es.staff_id

FROM Event e
JOIN Event_Staff es ON e.event_id = es.event_id
where es.staff_category = 'auxiliary'
ORDER BY e.event_date
)
SELECT 
apc.event_date,
apc.staff_id,
s.name
FROM Staff s
JOIN All_Posibol_combinations apc ON s.staff_id = apc.staff_id
LEFT JOIN Asigned_dates ad  ON apc.staff_id = ad.staff_id AND apc.event_date = ad.event_date
WHERE ad.staff_id IS NULL
ORDER BY apc.event_date;




