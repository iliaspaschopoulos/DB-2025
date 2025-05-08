SELECT
    p.performance_id,
    AVG(
        (r.interpretation_score + r.sound_lighting_score + r.stage_presence_score + r.organization_score) / 4.0
    ) AS avg_rating_by_visitor
FROM Ticket t
JOIN Performance p ON t.performance_id = p.performance_id
LEFT JOIN Rating r ON r.performance_id = p.performance_id AND r.visitor_id = t.visitor_id
WHERE t.visitor_id = 1
GROUP BY p.performance_id;
OPTION (LOOP JOIN);
SELECT
    p.performance_id,
    AVG(
        (r.interpretation_score + r.sound_lighting_score + r.stage_presence_score + r.organization_score) / 4.0
    ) AS avg_rating_by_visitor
FROM Ticket t
JOIN Performance p ON t.performance_id = p.performance_id
LEFT JOIN Rating r ON r.performance_id = p.performance_id AND r.visitor_id = t.visitor_id
WHERE t.visitor_id = 1
GROUP BY p.performance_id
OPTION (HASH JOIN);
SELECT
    p.performance_id,
    AVG(
        (r.interpretation_score + r.sound_lighting_score + r.stage_presence_score + r.organization_score) / 4.0
    ) AS avg_rating_by_visitor
FROM Ticket t
JOIN Performance p ON t.performance_id = p.performance_id
LEFT JOIN Rating r ON r.performance_id = p.performance_id AND r.visitor_id = t.visitor_id
WHERE t.visitor_id = 1
GROUP BY p.performance_id
OPTION (MERGE JOIN);
