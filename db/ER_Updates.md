# ER Schema Updates

The following attributes/entities need to be added or updated in the ER diagram to align with the updated schema:

- **Festival**
  - Add attributes: Name, Poster Image (URL), Description.
  
- **Location**
  - Add attributes: Image (URL), Description.
  
- **Band**
  - Add attributes: Type, Image (URL), Description.
  
- **Ticket**
  - Update to include:
    - Stage (FK)
    - Category with allowed values: General, VIP, Backstage.
    - Rename Price (was Cost).
    - Resale Status (Boolean).
  
- **Event**
  - Update to include:
    - Stage (FK)
    - Event Time
    - Image (URL)
    - Description.
  
- **Stage** (renamed from Scene)
  - Update entity:
    - Rename from "Scene" to "Stage".
    - Rename Equipment Info to Technical Equipment.
    - Add attributes: Location (FK), Image (URL).
  
- **Performance**
  - Update to include:
    - Optional Band (FK) for band performances.
    - Stage (FK)
    - Image (URL)
    - Description.
  
- **Artist**
  - Rename attribute "Stage Name" to "Alias".
  - Add attributes: Image (URL), Description.
  
- **Personnel (Staff)**
  - Add attribute: Image (URL).
  - Note subtypes: Security Personnel (min 5% of audience) and Support Staff (min 2% of audience) as business rules.
  
- **New Entities**
  - **Review**
    - Attributes: Visitor (FK), Performance (FK), Interpretation Rating, Sound & Lighting Rating, Stage Presence Rating, Organization Rating, Overall Impression Rating, Review Date.
  - **Website**
    - Attributes: URL, Festival (FK), Image (URL), Description.
  - **Resale Queue**
    - Attributes: Ticket (FK), Seller (FK Visitor), Buyer (FK Visitor), Listing Date, Resale Status, FIFO Order.
