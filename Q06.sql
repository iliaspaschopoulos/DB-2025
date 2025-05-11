SELECT
    p.performance_id,
    AVG(
        (r.interpretation_score + r.sound_lighting_score + r.stage_presence_score + r.organization_score) / 4.0
    ) AS avg_rating_by_visitor
FROM Ticket t
JOIN Event e ON t.event_id = e.event_id
JOIN Performance p ON p.event_id = e.event_id
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
JOIN Event e ON t.event_id = e.event_id
JOIN Performance p ON p.event_id = e.event_id
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
JOIN Event e ON t.event_id = e.event_id
JOIN Performance p ON p.event_id = e.event_id
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
JOIN Event e ON t.event_id = e.event_id
JOIN Performance p ON p.event_id = e.event_id
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
