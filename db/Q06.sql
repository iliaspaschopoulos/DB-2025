SET SHOWPLAN_ALL ON;
GO
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
GO
SET SHOWPLAN_ALL OFF;
GO
-- Στοιχεία Εκτέλεσης (Default Join Strategy):
--   Total execution time: 00:00:00.190
--   Στοιχεία από Εκτιμώμενο Πλάνο Εκτέλεσης (αρχείο: logs/Q06/basic.json):
--     Εκτιμώμενο Συνολικό Κόστος (Estimated Subtree Cost for root): 0.028103122
--     Κύριοι Τελεστές (Operators) και το σχετικό τους κόστος:
--       - Nested Loops (Left Outer Join, Node 5): Εκτιμώμενο Κόστος CPU: 0.000418, Εκτιμώμενο Κόστος I/O: 0.0
--       - Nested Loops (Inner Join, Node 6): Εκτιμώμενο Κόστος CPU: 0.000418, Εκτιμώμενο Κόστος I/O: 0.0
--       - Stream Aggregate (Node 3): Εκτιμώμενο Κόστος CPU: 6.0963637E-05, Εκτιμώμενο Κόστος I/O: 0.0

SET SHOWPLAN_ALL ON;
GO
-- Nested Loops Join
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
GO
SET SHOWPLAN_ALL OFF;
GO
-- Στοιχεία Εκτέλεσης (Nested Loops Join):
--   Total execution time: 00:00:00.131
--   Στοιχεία από Εκτιμώμενο Πλάνο Εκτέλεσης (αρχείο: logs/Q06/loop_join.csv):
--     Εκτιμώμενο Συνολικό Κόστος (TotalSubtreeCost for root, NodeId 1): 0.028103122
--     Κύριοι Τελεστές (Operators) και το σχετικό τους κόστος:
--       - Nested Loops (Left Outer Join, NodeId 5): Εκτιμώμενο Κόστος CPU: 0.000418, Εκτιμώμενο Κόστος I/O: 0
--       - Nested Loops (Inner Join, NodeId 6): Εκτιμώμενο Κόστος CPU: 0.000418, Εκτιμώμενο Κόστος I/O: 0
--       - Stream Aggregate (NodeId 3): Εκτιμώμενο Κόστος CPU: 6.0963637E-05, Εκτιμώμενο Κόστος I/O: 0

SET SHOWPLAN_ALL ON;
GO
-- Hash Join
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
GO
SET SHOWPLAN_ALL OFF;
GO
-- Στοιχεία Εκτέλεσης (Hash Join):
--   Total execution time: 00:00:00.138
--   Στοιχεία από Εκτιμώμενο Πλάνο Εκτέλεσης (αρχείο: logs/Q06/hash_join.json):
--     Εκτιμώμενο Συνολικό Κόστος (Estimated Subtree Cost for root): 0.060169138
--     Κύριοι Τελεστές (Operators) και το σχετικό τους κόστος:
--       - Hash Match (Right Outer Join, Node 6): Εκτιμώμενο Κόστος CPU: 0.018300008, Εκτιμώμενο Κόστος I/O: 0.0
--       - Hash Match (Inner Join, Node 8): Εκτιμώμενο Κόστος CPU: 0.018323692, Εκτιμώμενο Κόστος I/O: 0.0
--       - Sort (Node 4): Εκτιμώμενο Κόστος CPU: 0.0011140706, Εκτιμώμενο Κόστος I/O: 0.011261261

SET SHOWPLAN_ALL ON;
GO
-- Merge Join
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
GO
SET SHOWPLAN_ALL OFF;
GO
-- Στοιχεία Εκτέλεσης (Merge Join):
--   Total execution time: 00:00:00.141
--   Στοιχεία από Εκτιμώμενο Πλάνο Εκτέλεσης (αρχείο: logs/Q06/merge_join.json):
--     Εκτιμώμενο Συνολικό Κόστος (Estimated Subtree Cost for root): 0.03829765
--     Κύριοι Τελεστές (Operators) και το σχετικό τους κόστος:
--       - Merge Join (Left Outer Join, Node 5): Εκτιμώμενο Κόστος CPU: 0.0056140223, Εκτιμώμενο Κόστος I/O: 0.0
--       - Merge Join (Inner Join, Node 7): Εκτιμώμενο Κόστος CPU: 0.0058285273, Εκτιμώμενο Κόστος I/O: 0.0
--       - Sort (Node 6): Εκτιμώμενο Κόστος CPU: 0.0011364525, Εκτιμώμενο Κόστος I/O: 0.011261261

-- --- Ανάλυση Απόδοσης και Συμπεράσματα (Ελληνικά) ---
--
-- Σύγκριση Πραγματικών Χρόνων Εκτέλεσης και Εκτιμώμενων Κοστών:
-- 1. Default Join Strategy:
--    - Πραγματικός Χρόνος: 00:00:00.190
--    - Εκτιμώμενο Κόστος Πλάνου: 0.028103122
-- 2. Nested Loops Join (OPTION (LOOP JOIN)):
--    - Πραγματικός Χρόνος: 00:00:00.131
--    - Εκτιμώμενο Κόστος Πλάνου: 0.028103122
-- 3. Hash Join (OPTION (HASH JOIN)):
--    - Πραγματικός Χρόνος: 00:00:00.138
--    - Εκτιμώμενο Κόστος Πλάνου: 0.060169138
-- 4. Merge Join (OPTION (MERGE JOIN)):
--    - Πραγματικός Χρόνος: 00:00:00.141
--    - Εκτιμώμενο Κόστος Πλάνου: 0.03829765
--
-- Βέλτιστη Στρατηγική και Εξήγηση:
-- Η στρατηγική `Nested Loops Join` (με ρητή υπόδειξη `OPTION (LOOP JOIN)`) επέδειξε τον καλύτερο πραγματικό χρόνο εκτέλεσης (0.131 δευτερόλεπτα).
--
-- Παρατηρήσεις:
-- - Το `Nested Loops Join` ήταν το ταχύτερο στην πράξη (0.131s), και το εκτιμώμενο κόστος του πλάνου του ήταν το χαμηλότερο (0.028103122), ισοβαθμώντας με την εκτίμηση για την προεπιλεγμένη στρατηγική. Αυτό δείχνει μια καλή αντιστοιχία μεταξύ της εκτίμησης του optimizer για την αποδοτικότητα αυτής της στρατηγικής και της πραγματικής της απόδοσης όταν επιβάλλεται ρητά.
-- - Η προεπιλεγμένη στρατηγική (Default), παρόλο που είχε το ίδιο χαμηλό εκτιμώμενο κόστος πλάνου με το `Nested Loops Join`, είχε τον υψηλότερο πραγματικό χρόνο εκτέλεσης (0.190s). Αυτό υποδηλώνει ότι ο Query Optimizer, χωρίς ρητή υπόδειξη, ενδέχεται να κατέληξε σε ένα πλάνο που, αν και θεωρητικά εξίσου αποδοτικό (βάσει εκτίμησης), στην πράξη είχε μεγαλύτερη επιβάρυνση ή δεν εκμεταλλεύτηκε τις συνθήκες των δεδομένων τόσο αποτελεσματικά όσο η ρητή υπόδειξη `LOOP JOIN`.
-- - Το `Hash Join` ήταν το δεύτερο ταχύτερο στην πράξη (0.138s), παρόλο που είχε το υψηλότερο εκτιμώμενο κόστος πλάνου (0.060169138). Αυτή η απόκλιση υπογραμμίζει ότι οι εκτιμήσεις του optimizer δεν είναι πάντα απόλυτα ακριβείς και παράγοντες όπως το πραγματικό κόστος δημιουργίας hash tables ή η αποδοτικότητα των τελεστών `Sort` (που υπήρχε στο πλάνο του Hash Join) μπορούν να οδηγήσουν σε διαφορετική πραγματική απόδοση.
-- - Το `Merge Join` ήταν το τρίτο ταχύτερο (0.141s), με ενδιάμεσο εκτιμώμενο κόστος πλάνου (0.03829765). Η παρουσία τελεστή `Sort` και σε αυτό το πλάνο συνέβαλε στο εκτιμώμενο και, πιθανώς, στο πραγματικό κόστος του.
--
-- Γιατί το `Nested Loops Join` ήταν βέλτιστο σε αυτή την περίπτωση:
-- 1.  **Αποδοτικές Αναζητήσεις (Seeks):** Με κατάλληλα ευρετήρια (όπως υποδεικνύεται από τους τελεστές `Index Seek` και `Index Scan` στα πλάνα των `basic.json` και `loop_join.csv`), το `Nested Loops Join` μπορεί να εκτελέσει γρήγορες αναζητήσεις για κάθε γραμμή του (μικρού) εξωτερικού πίνακα.
-- 2.  **Χαμηλό Overhead:** Για μικρά ή καλά φιλτραρισμένα σύνολα δεδομένων, το overhead των `Nested Loops` είναι συχνά χαμηλότερο από το overhead που απαιτείται για τη δημιουργία hash tables (για `Hash Join`) ή την ταξινόμηση των δεδομένων (για `Merge Join`).
--
-- Συμπερασματικά, για το συγκεκριμένο ερώτημα με το δεδομένο φίλτρο, η ρητή υπόδειξη `OPTION (LOOP JOIN)` οδήγησε στην πιο αποδοτική εκτέλεση.