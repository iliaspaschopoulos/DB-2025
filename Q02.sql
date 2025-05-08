SELECT
    ag.genre,
    a.artist_id,
    a.name
FROM artist_genre ag JOIN artist a

    ON ag.artist_id = a.artist_id

ORDER BY 
    ag.genre, a.name;
