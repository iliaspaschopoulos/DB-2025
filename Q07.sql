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
