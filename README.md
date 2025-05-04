# DB-2025 Connection Guide

## 1. Database Connection Details

### Server Name

`dbntua2025v2.database.windows.net`

### Database Name

`DbNtua2025v2`

### Authentication Details

#### Superuser (DO NOT use for students)

* **Username**: `db2025ntua`
* **Password**: `TH7knXjZzL4-AV3`

#### Student Accounts

* **Liakos**
    * **Username**: `deandodger`
    * **Password**: `Password123!`
* **Jason**
    * **Username**: `perantzakas`
    * **Password**: `Password123!`

## Windows Guidelines

### Connect to SQL Server with Token (Windows)

1.  **Run in PowerShell:**

    ```powershell
    $token = az account get-access-token --resource [https://database.windows.net](https://database.windows.net) --query accessToken -o tsv
    ```

2.  **Open VS Code Terminal and run:**

    ```sh
    sqlcmd -S dbntua2025v2.database.windows.net -d DbNtua2025v2 -G -U deandodger --access-token $token
    ```

3.  **Configure SQL Connection in VS Code:**

    * Open VS Code.
    * Press `Ctrl + Shift + P` → Search for "MS SQL: Manage Connection Profiles".
    * Click "Add New Connection" → Enter:
        * Server Name: `dbntua2025v2.database.windows.net`
        * Database Name: `DbNtua2025v2`
        * Authentication Type: SQL Login
        * User: `deandodger`
        * Password: `Password123!`

## macOS Guidelines

### Configure SQL Connection in VS Code (macOS)

1.  **Run in terminal:**

    ```sh
    token=$(az account get-access-token --resource [https://database.windows.net](https://database.windows.net) --query accessToken -o tsv)
    ```

2.  **Connect to SQL Server with Token:**

    Run:

    ```sh
    sqlcmd -S dbntua2025v2.database.windows.net -d DbNtua2025v2 -G -U perantzakas --access-token "$token"
    ```

3.  **Open VS Code.**

4.  **Press `Cmd + Shift + P` → Search for "MS SQL: Manage Connection Profiles".**

5.  **Click "Add New Connection" → Enter:**

    * Server Name: `dbntua2025v2.database.windows.net`
    * Database Name: `DbNtua2025v2`
    * Authentication Type: SQL Login
    * User: `perantzakas`
    * Password: `Password123!`

6.  **Click Save.**

## Logic

### Implementation Decisions and Compliance with Professor's Requirements

1.  **Lookup Tables vs. ENUMs/Check Constraints:**

    * We replaced check constraints for discrete attributes (such as `continent`, `experience_level`, `payment_method`, and `staff_category`) with lookup tables.
    * **Professor's Point:** Use lookup tables for normalization and avoid SQL enums.
    * **Our Compliance:** Lookup tables have been added (for example, the `Continent`, `Experience_Level`, and `Payment_Method` tables).

2.  **Image Fields (with Alt Text):**

    * Added `image_path` and `image_alt` fields to applicable entities (Festival, Scene, Artist, Band).
    * **Professor's Point:** Each entity (where it makes sense) should include an image with a descriptive alt text.
    * **Our Compliance:** Schema was updated accordingly to store image file paths and corresponding alt text descriptions.

3.  **Query Adjustments:**

    * Updated the revenue query to join with the Payment_Method lookup instead of directly using a VARCHAR field.
    * **Professor's Point:** Adapt queries to take advantage of normalized lookup tables.
    * **Our Compliance:** The query in `queries.sql` has been updated to join with the Payment_Method table (note that further similar changes may be required for other queries).

4.  **Additional Business Rules:**

    * While the schema defines most entities and constraints, some complex rules (e.g., automatic resale queue management, staff percentage validation, or constraints on artist participation across years) will require additional triggers or application logic.
    * **Professor's Note:** Some business rules are to be handled via triggers or application-level solutions.
    * **Our Compliance:** Structural aspects now follow guidelines; further enforcement may be added as triggers or via application code.

### Overall Assessment

We believe that these changes bring the database design closer to full compliance with the assignment requirements and the professor’s clarifications. Most core specifications (normalized design, image attributes, and query modifications) are addressed.

## ERD/Relational Diagram Relationship Symbols

**Lucidchart Diagrams:**
- [ER Diagram (edit link)](https://lucid.app/lucidchart/57fd9f8f-7159-4475-8242-13dee6cf33a6/edit?viewport_loc=-3257%2C232%2C4818%2C2641%2C0_0&invitationId=inv_e50e2cf1-da7a-4854-8eb9-4a1bab05ae1d)
- [Relational Diagram (edit link)](https://lucid.app/lucidchart/9b7b5d05-00e3-4911-99d4-70bafd1a7f99/edit?viewport_loc=-2684%2C490%2C3298%2C1807%2C0_0&invitationId=inv_24cdfe50-dea8-45e7-95c8-f628a27367fb)

| Relationship Type         | Symbol in Diagram      | Meaning                                 |
|--------------------------|------------------------|-----------------------------------------|
| One-to-many (1:N)        | Parent —&#124;< Child  | Single line at parent, crow’s foot at child (each parent can have many children, each child has one parent) |
| Zero or many (0..N)      | Parent ○—< Child       | Circle at parent, crow’s foot at child (each parent can have zero or many children) |
| One-to-one (1:1)         | TableA —&#124;— TableB | Single line at both ends (each row in TableA matches at most one in TableB and vice versa) |
| Zero or one (0..1)       | TableA ○— TableB       | Circle at one end, single line at the other (each row in TableA matches zero or one in TableB) |

**Legend:**
- `—|` : Single line (mandatory/one)
- `○` : Circle (optional/zero)
- `<` : Crow’s foot (many)
