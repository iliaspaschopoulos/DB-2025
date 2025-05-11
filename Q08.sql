WITH All_Possible_Combinations AS (
    SELECT
        e.event_date,
        es.staff_id
    FROM Event e
    CROSS JOIN Event_Staff es
    WHERE es.staff_category = 'auxiliary'
),
Assigned_Dates AS (
    SELECT
        e.event_date,
        es.staff_id
    FROM Event e
    JOIN Event_Staff es ON e.event_id = es.event_id
    WHERE es.staff_category = 'auxiliary'
)
SELECT 
    apc.event_date,
    apc.staff_id,
    s.name
FROM Staff s
JOIN All_Possible_Combinations apc ON s.staff_id = apc.staff_id
LEFT JOIN Assigned_Dates ad  
    ON apc.staff_id = ad.staff_id AND apc.event_date = ad.event_date
WHERE ad.staff_id IS NULL
ORDER BY apc.event_date, staff_id;
