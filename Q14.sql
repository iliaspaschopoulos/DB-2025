WITH GenreYearCount AS (
    SELECT
        ag.genre,
        f.year,
        COUNT(p.performance_id) AS appearances
    FROM Performance p
    JOIN Artist a ON p.artist_id = a.artist_id
    JOIN Artist_Genre ag ON a.artist_id = ag.artist_id
    JOIN Event e ON p.event_id = e.event_id
    JOIN Festival f ON e.festival_id = f.festival_id
    GROUP BY ag.genre, f.year
    HAVING COUNT(p.performance_id) >= 3
),
MatchingGenres AS (
    SELECT
        g1.genre,
        g1.year AS year1,
        g2.year AS year2,
        g1.appearances
    FROM GenreYearCount g1
    JOIN GenreYearCount g2 
        ON g1.genre = g2.genre 
        AND g2.year = g1.year + 1
        AND g1.appearances = g2.appearances
)
SELECT *
FROM MatchingGenres
ORDER BY genre, year1;
