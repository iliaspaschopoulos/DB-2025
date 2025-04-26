# **DB-2025 Connection Guide**

## **1. Database Connection Details**
### **Server Name**
`dbntua2025v2.database.windows.net`

### **Database Name**
`DbNtua2025v2`

### **Authentication Details**
#### **Superuser (DO NOT use for students)**
- **Username**: `db2025ntua`
- **Password**: `TH7knXjZzL4-AV3`

#### **Student Accounts**
- **Liakos**
  - **Username**: `deandodger`
  - **Password**: `Password123!`
- **Jason**
  - **Username**: `perantzakas`
  - **Password**: `Password123!`



# Logic
## Implementation Decisions and Compliance with Professor's Requirements

1. **Lookup Tables vs. ENUMs/Check Constraints:**
   - We replaced check constraints for discrete attributes (such as `continent`, `experience_level`, `payment_method`, and `staff_category`) with lookup tables.
   - **Professor's Point:** Use lookup tables for normalization and avoid SQL enums.  
   - **Our Compliance:** Lookup tables have been added (for example, the `Continent`, `Experience_Level`, and `Payment_Method` tables).

2. **Image Fields (with Alt Text):**
   - Added `image_path` and `image_alt` fields to applicable entities (Festival, Scene, Artist, Band).
   - **Professor's Point:** Each entity (where it makes sense) should include an image with a descriptive alt text.
   - **Our Compliance:** Schema was updated accordingly to store image file paths and corresponding alt text descriptions.

3. **Query Adjustments:**
   - Updated the revenue query to join with the Payment_Method lookup instead of directly using a VARCHAR field.
   - **Professor's Point:** Adapt queries to take advantage of normalized lookup tables.
   - **Our Compliance:** The query in `queries.sql` has been updated to join with the Payment_Method table (note that further similar changes may be required for other queries).

4. **Additional Business Rules:**
   - While the schema defines most entities and constraints, some complex rules (e.g., automatic resale queue management, staff percentage validation, or constraints on artist participation across years) will require additional triggers or application logic.
   - **Professor's Note:** Some business rules are to be handled via triggers or application-level solutions.
   - **Our Compliance:** Structural aspects now follow guidelines; further enforcement may be added as triggers or via application code.

## Overall Assessment
We believe that these changes bring the database design closer to full compliance with the assignment requirements and the professor’s clarifications. Most core specifications (normalized design, image attributes, and query modifications) are addressed.
