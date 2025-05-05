-- Drop triggers if they exist before creating them
IF OBJECT_ID('[dbo].[VIP]', 'TR') IS NOT NULL
    DROP TRIGGER [dbo].[VIP];
GO

-- Drop Staff trigger using both schema-qualified and non-schema-qualified names to ensure removal
IF OBJECT_ID('[dbo].[Staff]', 'TR') IS NOT NULL
    DROP TRIGGER [dbo].[Staff];
GO

IF OBJECT_ID('Staff', 'TR') IS NOT NULL
    DROP TRIGGER Staff;
GO

IF OBJECT_ID('[dbo].[check_consecutive_years]', 'TR') IS NOT NULL
    DROP TRIGGER [dbo].[check_consecutive_years];
GO

-- VIP Trigger: Enforce VIP ticket limit per event/scene -- Q6
CREATE TRIGGER VIP
ON Ticket
AFTER INSERT
AS
BEGIN
    -- TODO: Improve for multi-row inserts (currently only works for single row)
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
-- Staff Trigger: Enforce staff ratios per event/scene -- Q7
CREATE TRIGGER StaffTrigger
ON Event_Staff
AFTER INSERT, UPDATE
AS
BEGIN
    -- TODO: Improve for multi-row inserts (currently only works for single row)
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
-- Artist Performance Trigger: Prevent >3 consecutive years for an artist
IF OBJECT_ID('[dbo].[check_consecutive_years_performance]', 'TR') IS NOT NULL
    DROP TRIGGER [dbo].[check_consecutive_years_performance];
GO

CREATE TRIGGER check_consecutive_years_performance
ON Performance
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @artist_id INT;
    DECLARE @prev_year INT;
    DECLARE @curr_year INT;
    DECLARE @streak INT;

    -- Cursor for each artist in the inserted rows
    DECLARE artist_cursor CURSOR FOR
        SELECT DISTINCT i.artist_id
        FROM inserted i;

    OPEN artist_cursor;
    FETCH NEXT FROM artist_cursor INTO @artist_id;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        -- Table variable to hold all years for this artist (including the new insert)
        DECLARE @years TABLE (year INT PRIMARY KEY);

        INSERT INTO @years(year)
        SELECT DISTINCT f.year
        FROM Performance p
        JOIN Event e ON p.event_id = e.event_id
        JOIN Festival f ON e.festival_id = f.festival_id
        WHERE p.artist_id = @artist_id
        UNION
        SELECT DISTINCT f2.year
        FROM inserted i2
        JOIN Event e2 ON i2.event_id = e2.event_id
        JOIN Festival f2 ON e2.festival_id = f2.festival_id
        WHERE i2.artist_id = @artist_id;

        -- Check for streaks
        SET @streak = 1;
        SET @prev_year = NULL;

        DECLARE year_cursor CURSOR FOR
            SELECT year FROM @years ORDER BY year;

        OPEN year_cursor;
        FETCH NEXT FROM year_cursor INTO @curr_year;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            IF @prev_year IS NOT NULL
            BEGIN
                IF @curr_year = @prev_year + 1
                    SET @streak = @streak + 1;
                ELSE
                    SET @streak = 1;
            END

            IF @streak > 3
            BEGIN
                CLOSE year_cursor;
                DEALLOCATE year_cursor;
                CLOSE artist_cursor;
                DEALLOCATE artist_cursor;
                ROLLBACK TRANSACTION;
                RAISERROR ('No artist can perform for more than 3 consecutive years.', 16, 1);
                RETURN;
            END

            SET @prev_year = @curr_year;
            FETCH NEXT FROM year_cursor INTO @curr_year;
        END

        CLOSE year_cursor;
        DEALLOCATE year_cursor;

        FETCH NEXT FROM artist_cursor INTO @artist_id;
    END

    CLOSE artist_cursor;
    DEALLOCATE artist_cursor;
END;
GO


-- Trigger for EAN-13

-- Trigger to auto-generate EAN-13 code for Ticket
GO
CREATE TRIGGER trg_Ticket_GenerateEAN
ON Ticket
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Only generate EAN if it was not provided (NULL or 0)
    UPDATE t
    SET ean = 
        CAST(
            LEFT(
                RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3)
            , 12)
        AS BIGINT
        ) * 10
        + (
            10 - (
                (
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 1, 1) AS INT) +
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 3, 1) AS INT) +
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 5, 1) AS INT) +
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 7, 1) AS INT) +
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 9, 1) AS INT) +
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 11, 1) AS INT)
                )
                + 3 * (
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 2, 1) AS INT) +
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 4, 1) AS INT) +
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 6, 1) AS INT) +
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 8, 1) AS INT) +
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 10, 1) AS INT) +
                    CAST(SUBSTRING(
                        RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
                        RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
                        RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3), 12, 1) AS INT)
                )
            ) % 10
        ) % 10
    FROM Ticket t
    JOIN inserted i ON t.ticket_id = i.ticket_id
    WHERE (i.ean IS NULL OR i.ean = 0);
END
GO