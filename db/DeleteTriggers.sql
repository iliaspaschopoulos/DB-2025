-- Drop all custom triggers related to the project

IF OBJECT_ID('[dbo].[VIP]', 'TR') IS NOT NULL
    DROP TRIGGER [dbo].[VIP];
GO

IF OBJECT_ID('[dbo].[Staff]', 'TR') IS NOT NULL
    DROP TRIGGER [dbo].[Staff];
GO

IF OBJECT_ID('Staff', 'TR') IS NOT NULL
    DROP TRIGGER Staff;
GO

IF OBJECT_ID('[dbo].[StaffTrigger]', 'TR') IS NOT NULL
    DROP TRIGGER [dbo].[StaffTrigger];
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

-- Add more DROP TRIGGER statements here if you add more triggers in the future
