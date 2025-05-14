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
