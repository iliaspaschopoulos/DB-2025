SET SHOWPLAN_ALL ON;
GO
-- Default join
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
GO
SET SHOWPLAN_ALL OFF;
GO


SET SHOWPLAN_ALL ON;
GO
-- LOOP join
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
   a.artist_id, a.name
OPTION (LOOP JOIN);
GO
SET SHOWPLAN_ALL OFF;
GO


SET SHOWPLAN_ALL ON;
GO
-- HASH join
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
   a.artist_id, a.name
OPTION (HASH JOIN);
GO
SET SHOWPLAN_ALL OFF;
GO


SET SHOWPLAN_ALL ON;
GO
-- MERGE join
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
   a.artist_id, a.name
OPTION (MERGE JOIN);
GO
SET SHOWPLAN_ALL OFF;
GO