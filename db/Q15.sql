SELECT
    v.first_name,
    a.name,
    r.overall_score
    FROM Rating r
    JOIN Visitor v ON r.visitor_id = v.visitor_id
    JOIN Performance p ON r.performance_id = p.performance_id
    JOIN Artist a ON p.artist_id = a.artist_id
    ORDER BY r.overall_score DESC, v.first_name
    OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY;
