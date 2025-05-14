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
   GROUP BY a.artist_id, a.name
),
filtered_counts AS (
   SELECT *
   FROM Different_Continents
   WHERE distinct_continents >= 3
)
SELECT * FROM filtered_counts;
