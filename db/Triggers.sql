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

IF OBJECT_ID('StaffTrigger', 'TR') IS NOT NULL
    DROP TRIGGER StaffTrigger;
GO

IF OBJECT_ID('[dbo].[check_consecutive_years]', 'TR') IS NOT NULL
    DROP TRIGGER [dbo].[check_consecutive_years];
GO

IF OBJECT_ID('[dbo].[check_consecutive_years_performance]', 'TR') IS NOT NULL
    DROP TRIGGER [dbo].[check_consecutive_years_performance];
GO

IF OBJECT_ID('[dbo].[trg_Ticket_GenerateEAN]', 'TR') IS NOT NULL
    DROP TRIGGER [dbo].[trg_Ticket_GenerateEAN];
GO

IF OBJECT_ID('[dbo].[trg_one_scene_per_event]', 'TR') IS NOT NULL
    DROP TRIGGER [dbo].[trg_one_scene_per_event];
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
    SET NOCOUNT ON;

    -- Check for security staff violations
    -- This checks if any event/scene combination affected by the DML operation
    -- now has insufficient security staff.
    IF EXISTS (
        SELECT 1
        FROM (SELECT DISTINCT event_id, scene_id FROM inserted) i  -- Process each affected event/scene combination once
        JOIN Scene sc ON i.scene_id = sc.scene_id
        WHERE (
            -- Count total security staff for this specific event_id and scene_id AFTER the DML
            SELECT COUNT(*)
            FROM Event_Staff es
            WHERE es.event_id = i.event_id
              AND es.scene_id = i.scene_id
              AND es.staff_category = 'security'
        ) < (sc.max_capacity * 0.05) -- Compare with 5% of that scene's capacity
    )
    BEGIN
        ROLLBACK TRANSACTION;
        RAISERROR ('Security staff must be at least 5%% of the stage capacity for one or more affected events/scenes.', 16, 1);
        RETURN;
    END

    -- Check for auxiliary staff violations
    -- This checks if any event/scene combination affected by the DML operation
    -- now has insufficient auxiliary staff.
    IF EXISTS (
        SELECT 1
        FROM (SELECT DISTINCT event_id, scene_id FROM inserted) i -- Process each affected event/scene combination once
        JOIN Scene sc ON i.scene_id = sc.scene_id
        WHERE (
            -- Count total auxiliary staff for this specific event_id and scene_id AFTER the DML
            SELECT COUNT(*)
            FROM Event_Staff es
            WHERE es.event_id = i.event_id
              AND es.scene_id = i.scene_id
              AND es.staff_category = 'auxiliary'
        ) < (sc.max_capacity * 0.02) -- Compare with 2% of that scene's capacity
    )
    BEGIN
        DECLARE @DebugEventID INT, @DebugSceneID INT, @DebugAuxCount INT, @DebugSceneCapacity INT, @DebugRequired DECIMAL(5,2);
        SELECT TOP 1 @DebugEventID = i.event_id, @DebugSceneID = i.scene_id, @DebugSceneCapacity = sc.max_capacity
        FROM (SELECT DISTINCT event_id, scene_id FROM inserted) i
        JOIN Scene sc ON i.scene_id = sc.scene_id
        WHERE (SELECT COUNT(*) FROM Event_Staff es WHERE es.event_id = i.event_id AND es.scene_id = i.scene_id AND es.staff_category = 'auxiliary') < (sc.max_capacity * 0.02);

        IF @DebugEventID IS NOT NULL
        BEGIN
            SELECT @DebugAuxCount = COUNT(*) FROM Event_Staff es WHERE es.event_id = @DebugEventID AND es.scene_id = @DebugSceneID AND es.staff_category = 'auxiliary';
            SET @DebugRequired = @DebugSceneCapacity * 0.02;
            PRINT 'TRIGGER DEBUG: Failing for EventID=' + CAST(@DebugEventID AS VARCHAR) + 
                  ', SceneID=' + CAST(@DebugSceneID AS VARCHAR) + 
                  ', AuxCount=' + CAST(@DebugAuxCount AS VARCHAR) + 
                  ', SceneCapacity=' + CAST(@DebugSceneCapacity AS VARCHAR) +
                  ', RequiredCount (calc)=' + CAST(@DebugRequired AS VARCHAR);
        END
        RAISERROR ('Auxiliary staff must be at least 2%% of the stage capacity for one or more affected events/scenes.', 16, 1);
        RETURN;
    END
END;
--------------------------------------------------------------
GO------------------------------------------------------------
--------------------------------------------------------------
-- Artist Performance Trigger: Prevent >3 consecutive years for an artist
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
CREATE TRIGGER trg_Ticket_GenerateEAN
ON Ticket
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Generate EAN-13 for each inserted ticket
    UPDATE Ticket
    SET ean = CAST(base_code AS BIGINT) * 10 + check_digit
    FROM (
        SELECT 
            i.ticket_id,
            RIGHT('000000' + CAST(i.ticket_id AS VARCHAR(6)), 6) +
            RIGHT('000' + CAST(i.event_id AS VARCHAR(3)), 3) +
            RIGHT('000' + CAST(i.visitor_id AS VARCHAR(3)), 3) AS base_code,
            (
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
                    ) +
                    3 * (
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
            ) % 10 AS check_digit
        FROM inserted i
    ) AS gen
    WHERE Ticket.ticket_id = gen.ticket_id;
END
GO
-- ------------------------------------------
-- trigger ena scene ana event
CREATE TRIGGER trg_one_scene_per_event
ON Event
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- This condition checks if an event (defined by festival_id and event_date) 
    -- already exists. If so, it prevents inserting another event for the same 
    -- festival and date, effectively ensuring that such a conceptual event 
    -- is tied to only one scene (the one from the first insertion).
    IF EXISTS (
        SELECT 1
        FROM Inserted i
        JOIN Event e
          ON e.festival_id = i.festival_id
         AND e.event_date = i.event_date
    )
    BEGIN
        THROW 50001, 'An event for this festival on this date already exists, and thus is already assigned a scene. Cannot assign another scene.', 1;
        RETURN;
    END

    -- If no conflicting event exists, proceed with the insertion.
    -- Only insert columns that exist in the Event table.
    -- event_id is an IDENTITY column and should not be in the list.
    INSERT INTO Event (festival_id, event_date, scene_id)
    SELECT festival_id, event_date, scene_id              
    FROM Inserted;
END;
