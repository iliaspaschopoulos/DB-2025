WITH GenrePairs AS (
    SELECT 
        ag1.artist_id,
        ag1.genre AS genre1,
        ag2.genre AS genre2
    FROM Artist_Genre ag1
    JOIN Artist_Genre ag2 
        ON ag1.artist_id = ag2.artist_id
       AND ag1.genre < ag2.genre -- to avoid duplicate pairs like (Rock, Jazz) and (Jazz, Rock)
),
GenreFestivalAppearances AS (
    SELECT DISTINCT 
        gp.genre1,
        gp.genre2,
        f.festival_id
    FROM GenrePairs gp
    JOIN Performance p ON gp.artist_id = p.artist_id
    JOIN Event e ON p.event_id = e.event_id
    JOIN Festival f ON e.festival_id = f.festival_id
),
GenrePairFestivalCounts AS (
    SELECT 
        genre1,
        genre2,
        COUNT(DISTINCT festival_id) AS festivals_count
    FROM GenreFestivalAppearances
    GROUP BY genre1, genre2
)
SELECT TOP 3
    genre1,
    genre2,
    festivals_count
FROM GenrePairFestivalCounts
ORDER BY festivals_count DESC;
