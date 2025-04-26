CREATE TRIGGER VIP
ON Ticket
AFTER INSERT
AS
BEGIN
    IF (
        (SELECT COUNT(*)
    FROM Ticket
    WHERE event_id = (SELECT event_id
        FROM inserted) AND ticket_category = 'VIP')
        >
        ((SELECT Scene.max_capacity
    FROM Event
        JOIN Scene ON Event.scene_id = Scene.scene_id
    WHERE Event.event_id = (SELECT event_id
    FROM inserted)) * 0.1)
    )
    BEGIN
        ROLLBACK TRANSACTION;
        RAISERROR ('VIP tickets cannot exceed 10%% of the stage capacity.', 16, 1);
    END
END;
--------------------------------------------------------------
GO------------------------------------------------------------
--------------------------------------------------------------
CREATE TRIGGER Staff
ON Event_Staff
AFTER INSERT, UPDATE
AS
BEGIN
    IF (
        (SELECT COUNT(*)
    FROM Event_Staff
    WHERE event_id = (SELECT event_id
        FROM inserted)
        AND scene_id = (SELECT scene_id
        FROM inserted)
        AND staff_category = 'security')
        <
        ((SELECT Scene.max_capacity
    FROM Scene
    WHERE Scene.scene_id = (SELECT scene_id
    FROM inserted)) * 0.05)
    )
    BEGIN
        ROLLBACK TRANSACTION;
        RAISERROR ('Security staff must be at least 5%% of the stage capacity.', 16, 1);
    END

    -- Check if auxiliary staff is less than 2% of the stage capacity
    IF (
        (SELECT COUNT(*)
    FROM Event_Staff
    WHERE event_id = (SELECT event_id
        FROM inserted)
        AND scene_id = (SELECT scene_id
        FROM inserted)
        AND staff_category = 'auxiliary')
        <
        ((SELECT Scene.max_capacity
    FROM Scene
    WHERE Scene.scene_id = (SELECT scene_id
    FROM inserted)) * 0.02)
    )
    BEGIN
        ROLLBACK TRANSACTION;
        RAISERROR ('Auxiliary staff must be at least 2%% of the stage capacity.', 16, 1);
    END
END;
--------------------------------------------------------------
GO------------------------------------------------------------
--------------------------------------------------------------
CREATE TRIGGER check_consecutive_years
ON Artist
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1
    FROM INSERTED
    WHERE consecutive_years_appearing > 2
    )
    BEGIN
    THROW 50001, N'Ο καλλιτέχνης δεν μπορεί να συμμετέχει για πάνω από 3 συνεχόμενα έτη.', 1;
    RETURN;
END

    -- Αν όλα καλά, τότε κάνουμε insert αλλά με αλλαγμένο consecutive_years_appearing
    INSERT INTO Artist
    (consecutive_years_appearing)
SELECT
    consecutive_years_appearing + 1, 
-- Αυξάνουμε
FROM INSERTED;
END;