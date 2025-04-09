-- Query 1: Find revenue of the festival, per year from ticket sales, broken down by payment method
SELECT f.year,
       t.payment_method,
       SUM(t.cost) AS total_revenue
FROM Ticket t
JOIN Event e ON t.event_id = e.event_id
JOIN Festival f ON e.festival_id = f.festival_id
GROUP BY f.year, t.payment_method
ORDER BY f.year, t.payment_method;