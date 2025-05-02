-- Q9: Βρείτε τα έσοδα του φεστιβάλ, ανά έτος από την πώληση εισιτηρίων, λαμβάνοντας υπόψη όλες τις κατηγορίες εισιτηρίων και παρέχοντας ανάλυση ανά είδος πληρωμής.
SELECT f.year,
    t.payment_method,
    SUM(t.cost) AS total_revenue
FROM Ticket t
    JOIN Event e ON t.event_id = e.event_id
    JOIN Festival f ON e.festival_id = f.festival_id
GROUP BY f.year, t.payment_method
ORDER BY f.year, t.payment_method;

-- b2: Βρείτε όλους τους καλλιτέχνες που ανήκουν σε ένα συγκεκριμένο μουσικό είδος με ένδειξη αν συμμετείχαν σε εκδηλώσεις του φεστιβάλ για το συγκεκριμένο έτος ;
SELECT
    ag.genre,
    a.artist_id,
    a.name
FROM artist_genre ag JOIN artist a

    ON ag.artist_id = a.artist_id

ORDER BY 
    ag.genre, a.name;

-- b3: Βρείτε ποιοι καλλιτέχνες έχουν εμφανιστεί ως warm up περισσότερες από 2 φορές στο ίδιο φεστιβάλ;
SELECT
    Artist.name AS artist_name,
    Festival.year AS festival_year,
    COUNT(Performance.performance_id) AS warm_up_count
FROM Performance

    JOIN Event ON Performance.event_id = Event.event_id

    JOIN Festival ON Event.festival_id = Festival.festival_id

    JOIN Artist ON Performance.artist_id = Artist.artist_id

WHERE 
    Performance.performance_type = 'warm up'

GROUP BY 
    Artist.name, Festival.year

HAVING 
    COUNT(Performance.performance_id) > 2;



