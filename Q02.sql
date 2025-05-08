SELECT
    ag.genre,
    a.artist_id,
    a.name,
    e.event_date
FROM artist_genre ag 
JOIN artist a ON ag.artist_id = a.artist_id
JOIN Performance p ON p.artist_id = a.artist_id
JOIN Event e ON p.event_id = e.event_id
WHERE YEAR(e.event_date) = 2025
ORDER BY 
    ag.genre, a.name;
