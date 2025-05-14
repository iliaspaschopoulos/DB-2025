WITH ArtistParticipation AS (
    SELECT 
        Artist.artist_id,
        Artist.name AS artist_name,
        COUNT(DISTINCT Festival.festival_id) AS total_participations
    FROM Performance
    JOIN Artist ON Artist.artist_id = Performance.artist_id
    JOIN Event ON Performance.event_id = Event.event_id
    JOIN Festival ON Event.festival_id = Festival.festival_id
    GROUP BY Artist.artist_id, Artist.name
),
MaxParticipation AS (
    SELECT MAX(total_participations) AS max_participations
    FROM ArtistParticipation
)
SELECT 
    ap.artist_id,
    ap.artist_name,
    ap.total_participations
FROM ArtistParticipation ap
JOIN MaxParticipation mp ON 1 = 1
WHERE ap.total_participations <= mp.max_participations - 5;
