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
