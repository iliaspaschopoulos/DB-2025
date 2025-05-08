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
