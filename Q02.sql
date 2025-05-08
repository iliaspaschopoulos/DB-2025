SELECT
    ag.genre,
    a.artist_id,
    a.name
FROM Artist_Genre ag JOIN Artist a

    ON ag.artist_id = a.artist_id

ORDER BY 
    ag.genre, a.name;
