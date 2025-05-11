SELECT
    COALESCE(Artist.name, Band.band_name) AS performer_name,
    Festival.year AS festival_year,
    COUNT(Performance.performance_id) AS warm_up_count
FROM Performance
    JOIN Event ON Performance.event_id = Event.event_id
    JOIN Festival ON Event.festival_id = Festival.festival_id
    LEFT JOIN Artist ON Performance.artist_id = Artist.artist_id
    LEFT JOIN Band ON Performance.band_id = Band.band_id
WHERE 
    Performance.performance_type = 'warm up'
    -- The check constraint chk_performance_artist_or_band in the Performance table
    -- ensures that either artist_id or band_id is populated, but not both.
    -- COALESCE will pick the non-null name.
GROUP BY 
    COALESCE(Artist.name, Band.band_name),
    Festival.year
HAVING 
    COUNT(Performance.performance_id) > 2;
