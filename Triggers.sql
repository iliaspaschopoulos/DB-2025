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
