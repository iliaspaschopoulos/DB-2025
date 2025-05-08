    SELECT 
   a.artist_id,
   a.name,
    ROUND(AVG((r.interpretation_score + r.sound_lighting_score + 
                r.stage_presence_score + r.organization_score) / 4.0), 2) AS average_rating,
	ROUND(AVG(r.overall_score), 2) AS overall_score
FROM 
    Rating r
JOIN 
    Performance p ON r.performance_id = p.performance_id
JOIN 
    Artist a ON p.artist_id = a.artist_id
WHERE 
    a.artist_id = 3
GROUP BY
   a.artist_id, a.name;
