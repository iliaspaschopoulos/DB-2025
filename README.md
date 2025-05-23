# Οδηγός Σύνδεσης DB-2025

## TL;DR
### Χρήση Azure SQL
- Από κοινού αποφασίστηκε να χρησιμοποιηθεί ένα interactive database vendor, αντί του XAMPP. Χρησιμοποιήθηκε Azure. Τα βασικά οφέλη χρήσης είναι:
    - Interactive workflow and data.
    - Αποφυγή database crash που παρατηρούνται στις εναλλακτικές λύσεις.
    - Πειραματισμός και εξοικείωση με tools που χρησιμοποιούνται στην αγορά εργασίας.

## 1. Στοιχεία Σύνδεσης Βάσης Δεδομένων

### Όνομα Διακομιστή

`dbntua2025v2.database.windows.net`

### Όνομα Βάσης Δεδομένων
`DbNtua2025v2`

### Στοιχεία Ταυτοποίησης

#### Superuser (ΝΑ ΜΗΝ χρησιμοποιείται από φοιτητές)

* **Όνομα χρήστη**: `db2025ntua`
* **Κωδικός**: `TH7knXjZzL4-AV3`

#### Λογαριασμοί Φοιτητών

* **Liakos**
    * **Όνομα χρήστη**: `deandodger`
    * **Κωδικός**: `Password123!`
* **Jason**
    * **Όνομα χρήστη**: `perantzakas`
    * **Κωδικός**: `Password123!`

## SQL Server Version and Technicals

- **SQL Server Version**: Azure SQL Database (Engine Version: 12.0.2000.8, Product Level: RTM) / Local (Docker image mssql/server:2022-latest)
- **Edition**: SQL Azure (Database: General Purpose)
- **Compatibility Level**: 170 (for DbNtua2025v2)
- **Collation**: SQL_Latin1_General_CP1_CI_AS (Server and Database)
- **Service Objective**: GP_S_Gen5_2
- **Other Technical Details**: InstanceName: NULL, IsIntegratedSecurityOnly: False, IsClustered: False

## Οδηγίες για Windows

### Σύνδεση σε SQL Server με Token (Windows)

1.  **Εκτελέστε στο PowerShell:**

    ```powershell
    $token = az account get-access-token --resource [https://database.windows.net](https://database.windows.net) --query accessToken -o tsv
    ```

2.  **Ανοίξτε το VS Code Terminal και εκτελέστε:**

    ```sh
    sqlcmd -S dbntua2025v2.database.windows.net -d DbNtua2025v2 -G -U deandodger --access-token $token
    ```

3.  **Ρύθμιση Σύνδεσης SQL στο VS Code:**

    * Ανοίξτε το VS Code.
    * Πατήστε `Ctrl + Shift + P` → Αναζήτηση για "MS SQL: Manage Connection Profiles".
    * Κάντε κλικ στο "Add New Connection" → Εισάγετε:
        * Server Name: `dbntua2025v2.database.windows.net`
        * Database Name: `DbNtua2025v2`
        * Authentication Type: SQL Login
        * User: `deandodger`
        * Password: `Password123!`

## Οδηγίες για macOS

### Ρύθμιση Σύνδεσης SQL στο VS Code (macOS)

1.  **Εκτελέστε στο τερματικό:**

    ```sh
    token=$(az account get-access-token --resource [https://database.windows.net](https://database.windows.net) --query accessToken -o tsv)
    ```

2.  **Σύνδεση σε SQL Server με Token:**

    Εκτελέστε:

    ```sh
    sqlcmd -S dbntua2025v2.database.windows.net -d DbNtua2025v2 -G -U perantzakas --access-token "$token"
    ```

3.  **Ανοίξτε το VS Code.**

4.  **Πατήστε `Cmd + Shift + P` → Αναζήτηση για "MS SQL: Manage Connection Profiles".**

5.  **Κάντε κλικ στο "Add New Connection" → Εισάγετε:**

    * Server Name: `dbntua2025v2.database.windows.net`
    * Database Name: `DbNtua2025v2`
    * Authentication Type: SQL Login
    * User: `perantzakas`
    * Password: `Password123!`

6.  **Κάντε Αποθήκευση.**

## Λογική

### Αποφάσεις Υλοποίησης και Συμμόρφωση με τις Απαιτήσεις του Καθηγητή

1.  **Πίνακες Αναζήτησης vs. ENUMs/Check Constraints:**

    * Το σχήμα χρησιμοποιεί check constraints για διακριτά χαρακτηριστικά (όπως `experience_level`, `payment_method`, και `staff_category`) αντί για lookup tables.
    * **Σημείωση Καθηγητή:** Προτιμώνται οι lookup tables για κανονικοποίηση, αλλά τα check constraints είναι αποδεκτά για μικρά, σταθερά σύνολα.
    * **Τρέχουσα Κατάσταση:** Το σχήμα χρησιμοποιεί check constraints για αυτά τα πεδία. Αν απαιτείται αυστηρότερη κανονικοποίηση, μπορούν να μετατραπούν σε lookup tables.

2.  **Πεδία Εικόνας (με Alt Text):**

    * Το σχήμα **δεν** περιλαμβάνει προς το παρόν πεδία `image_path` ή `image_alt` για οντότητες (Festival, Scene, Artist, Band).
    * **Σημείωση Καθηγητή:** Κάθε οντότητα (όπου έχει νόημα) πρέπει να περιλαμβάνει εικόνα με περιγραφικό alt text.
    * **Τρέχουσα Κατάσταση:** Τα πεδία αυτά δεν υπάρχουν. Αν απαιτείται, το σχήμα πρέπει να ενημερωθεί.

3.  **Προσαρμογές Ερωτημάτων:**

    * Τα ερωτήματα χρησιμοποιούν απευθείας τα πεδία τύπου VARCHAR για χαρακτηριστικά όπως `payment_method`.
    * **Σημείωση Καθηγητή:** Αν χρησιμοποιηθούν lookup tables, τα ερωτήματα πρέπει να κάνουν join με αυτές. Διαφορετικά, η απευθείας χρήση VARCHAR/check constraints είναι αποδεκτή.
    * **Τρέχουσα Κατάσταση:** Τα ερωτήματα χρησιμοποιούν απευθείας τα πεδία VARCHAR. Αν προστεθούν lookup tables, τα ερωτήματα πρέπει να ενημερωθούν.

4.  **Σειρά Διαγραφών/Διαγραφής Πινάκων και Ακεραιότητα Δεδομένων:**

    * Η σειρά των εντολών `DELETE` και `DROP TABLE` έχει διαμορφωθεί προσεκτικά ώστε να τηρούνται οι εξαρτήσεις ξένων κλειδιών, διαγράφοντας πρώτα τους child πίνακες και μετά τους parent.
    * **Σημείωση Καθηγητή:** Αυτό αποτρέπει παραβιάσεις constraints κατά τη διαγραφή δεδομένων ή αλλαγές στο σχήμα.
    * **Τρέχουσα Κατάσταση:** Η σειρά σε σχήμα και scripts είναι σωστή και αποτρέπει σφάλματα FK.

5.  **Επιπλέον Επιχειρηματικοί Κανόνες:**

    * Κάποιοι σύνθετοι κανόνες (π.χ. αυτόματη διαχείριση resale queue, επικύρωση ποσοστού προσωπικού, ή περιορισμοί συμμετοχής καλλιτεχνών ανά έτος) μπορεί να απαιτούν triggers ή λογική σε επίπεδο εφαρμογής.
    * **Σημείωση Καθηγητή:** Κάποιοι κανόνες θα υλοποιηθούν με triggers ή σε επίπεδο εφαρμογής.
    * **Τρέχουσα Κατάσταση:** Τα δομικά στοιχεία ακολουθούν τις οδηγίες. Περαιτέρω έλεγχοι μπορούν να προστεθούν με triggers ή application code.

### Συνολική Εκτίμηση

Το σχήμα και τα scripts ευθυγραμμίζονται με τις απαιτήσεις της εργασίας όσον αφορά την ακεραιότητα ξένων κλειδιών και τη σωστή σειρά διαγραφών.

## Εισαγωγή δεδομένων

Για την εισαγωγή δεδομένων δημιουργήσαμε 2 αρχεία:
- `generate_bulk_data.py`
    - Δημιουργία του load.sql. Ιδιαίτερη έμφαση στην σειρά εισαγωγής, στα foreign keys matching και στα constraints της βάσης (π.χ. 5% security staff).
    Το αρχείο παράγει τις εντολές SQL `INSERT` για το `load.sql`. Διασφαλίζει τη σωστή σειρά εισαγωγής (π.χ. πρώτα οι πίνακες `Festivals`, μετά τα `Events`) και τη συνοχή των εξωτερικών κλειδιών (FKs) αποθηκεύοντας και επαναχρησιμοποιώντας τα IDs που παράγονται. Επίσης, εφαρμόζει λογική για την τήρηση κανόνων, όπως ο υπολογισμός του προσωπικού ασφαλείας (`Security Staff`) βάσει της χωρητικότητας της σκηνής, για να ικανοποιηθούν τα triggers της βάσης (`Triggers.sql`).
- delete_all_data.sql
    - Διαγράφει όλα τα υπάρχοντα δεδομένα.
    - Κάνει rebase indexes.

## Σύμβολα Σχέσεων ERD/Relational Diagram

**Διαγράμματα Lucidchart:**
- [ER Διάγραμμα (σύνδεσμος επεξεργασίας)](https://lucid.app/lucidchart/57fd9f8f-7159-4475-8242-13dee6cf33a6/edit?viewport_loc=-3257%2C232%2C4818%2C2641%2C0_0&invitationId=inv_e50e2cf1-da7a-4854-8eb9-4a1bab05ae1d)
- [Relational Διάγραμμα (σύνδεσμος επεξεργασίας)](https://lucid.app/lucidchart/9b7b5d05-00e3-4911-99d4-70bafd1a7f99/edit?viewport_loc=-2684%2C490%2C3298%2C1807%2C0_0&invitationId=inv_24cdfe50-dea8-45e7-95c8-f628a27367fb)

| Τύπος Σχέσης              | Σύμβολο στο Διάγραμμα      | Ερμηνεία                                 |
|--------------------------|---------------------------|------------------------------------------|
| Ένα-προς-πολλά (1:N)     | Γονέας —&#124;< Παιδί     | Μία γραμμή στον γονέα, crow’s foot στο παιδί (κάθε γονέας έχει πολλά παιδιά, κάθε παιδί έναν γονέα) |
| Μηδέν ή πολλά (0..N)      | Γονέας ○—< Παιδί          | Κύκλος στον γονέα, crow’s foot στο παιδί (κάθε γονέας έχει μηδέν ή πολλά παιδιά) |
| Ένα-προς-ένα (1:1)        | ΠίνακαςΑ —&#124;— ΠίνακαςΒ | Μία γραμμή και στα δύο άκρα (κάθε γραμμή του ΠίνακαΑ αντιστοιχεί σε το πολύ μία του ΠίνακαΒ και αντίστροφα) |
| Μηδέν ή ένα (0..1)        | ΠίνακαςΑ ○— ΠίνακαςΒ      | Κύκλος στο ένα άκρο, γραμμή στο άλλο (κάθε γραμμή του ΠίνακαΑ αντιστοιχεί σε μηδέν ή μία του ΠίνακαΒ) |

**Υπόμνημα:**
- `—|` : Μία γραμμή (υποχρεωτικό/ένα)
- `○` : Κύκλος (προαιρετικό/μηδέν)
- `<` : Crow’s foot (πολλά)
