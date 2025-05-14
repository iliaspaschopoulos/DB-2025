-- Find young artists (<30 years old) with the most festival participations

WITH
    YoungArtists
    AS
    (
        SELECT
            a.artist_id,
            a.name,
            DATEDIFF(YEAR, a.date_of_birth, GETDATE()) AS age
        FROM Artist a
        WHERE DATEDIFF(YEAR, a.date_of_birth, GETDATE()) < 30
    ),
    ArtistFestivalCounts
    AS
    (
        SELECT
            ya.artist_id,
            COUNT(DISTINCT f.festival_id) AS festival_count
        FROM YoungArtists ya
            JOIN Performance p ON p.artist_id = ya.artist_id
            JOIN Event e ON p.event_id = e.event_id
            JOIN Festival f ON e.festival_id = f.festival_id
        GROUP BY ya.artist_id
    )
SELECT TOP 5
    a.artist_id,
    a.name,
    afc.festival_count
FROM ArtistFestivalCounts afc
    JOIN Artist a ON a.artist_id = afc.artist_id
ORDER BY afc.festival_count DESC, a.artist_id;

