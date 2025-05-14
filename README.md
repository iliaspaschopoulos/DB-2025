# DB-2025 Application

This repository contains both a Spring Boot backend (`spring-app/`) and a React frontend (`frontend/`) for managing a music festival database that includes artists, bands, events, performances, and more. The application uses Azure SQL Server for the main database and H2 in-memory for integration tests.

## Features

- **Artist Management**: Create, edit, and delete artist profiles
- **Band Management**: Manage bands and their information
- **Band Members**: Associate artists with bands
- **Artist Genres**: Track music genres for each artist 
- **Events**: Plan and organize festival events at different venues
- **Performances**: Schedule performances by artists or bands

---

## Backend (spring-app)

### Prerequisites
- Java 17+
- Maven 3.6+
- Internet access to Azure SQL Server

### Configuration
The main datasource is configured in `spring-app/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://dbntua2025v2.database.windows.net:1433;database=DbNtua2025Online;encrypt=true;trustServerCertificate=true;loginTimeout=30;
spring.datasource.username=db2025ntua
spring.datasource.password=TH7knXjZzL4-AV3
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

> **Note:** Access to the Azure SQL Server database is restricted to authorized Azure AD or SQL users. Make sure your IP is allowed and credentials are valid.

### Running the Backend
```bash
cd spring-app
mvn spring-boot:run
```
The API will start on http://localhost:8080.

### Testing
Integration tests use an H2 in-memory database. Configuration is in:
```
spring-app/src/test/resources/application-test.properties
``` 
Run tests with:
```bash
cd spring-app
mvn test
```

---

## Frontend (frontend)

### Prerequisites
- Node.js 14+
- npm 6+

### Configuration
The frontend communicates with the backend at `http://localhost:8080/api`. CORS is enabled on the Spring Boot controllers for the React dev server (`http://localhost:3000`).

### Running the Frontend
```bash
cd frontend
npm install
npm start
```
The React app will open at http://localhost:3000.

---

## Directory Structure
```
/DB-2025
  ├── spring-app        # Spring Boot backend
  ├── frontend          # React frontend
  └── db/               # Database schema & scripts
```

## How It Works
1. **Home Page** (`/`): Lists available tables.
2. **Artists Page** (`/artists`): Full CRUD UI for the `Artist` table.
3. **Bands Page** (`/bands`): Full CRUD UI for the `Band` table.

Feel free to extend additional controllers, integration tests, and frontend pages for other tables following the patterns provided.
