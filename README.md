# ✈ Airline Management System

A desktop application built with **Java** and **JavaFX** for managing airline operations. It supports three user roles — **Admin**, **Staff**, and **Passenger** — each with a dedicated dashboard and set of features. The application connects to a **MySQL** database via JDBC.

---

## Features

### Passenger (User)
- Register and log in securely
- Search available flights by source, destination, and date
- Book flights with seat selection (Economy, Business, First Class)
- Apply discount coupon codes at checkout
- Multiple payment methods: Card, Bank Transfer, Bkash, Nagad
- View and manage personal bookings
- Download/view ticket after booking

### Staff
- View all scheduled flights
- Book flights on behalf of passengers
- View passenger lists per flight

### Admin
- Full dashboard with system-wide statistics (total flights, users, bookings, revenue)
- **Flight Management** — add, update, and cancel flights
- **User Management** — view and manage registered users
- **Employee Management** — manage staff accounts (pilots, crew)
- **Coupon Management** — create and manage discount coupons
- **Reports** — view booking and revenue reports
- Book flights with an exclusive admin discount
- View recent bookings across all users

---

## Tech Stack

| Layer        | Technology                        |
|--------------|-----------------------------------|
| Language     | Java 11+                          |
| UI Framework | JavaFX (FXML)                     |
| Database     | MySQL / MariaDB (via XAMPP)       |
| DB Driver    | MySQL Connector/J (JDBC)          |
| Build Tool   | None (manual classpath / IDE)     |

---

## Requirements

### 1. Java Development Kit (JDK) 11 or higher
Download from: https://www.oracle.com/java/technologies/downloads/  
Or use OpenJDK: https://adoptium.net/

Verify installation:
```bash
java -version
```

### 2. JavaFX SDK 17+ (if using JDK 11–20)
> **Note:** JDK 21+ does not bundle JavaFX. You must download it separately.

Download from: https://gluonhq.com/products/javafx/

Extract the SDK to a known location, e.g. `C:\javafx-sdk` or `/usr/local/javafx-sdk`.

### 3. XAMPP (MySQL/MariaDB Server)
Download from: https://www.apachefriends.org/download.html

XAMPP provides MySQL (MariaDB) which the app connects to on `localhost:3306`.

### 4. MySQL Connector/J (JDBC Driver)
Download from: https://dev.mysql.com/downloads/connector/j/

Select **Platform Independent** and download the `.jar` file. You will need to add it to the project's classpath.

### 5. IDE (Recommended)
- **Eclipse IDE** with e(fx)clipse plugin  
  https://www.eclipse.org/downloads/
- **IntelliJ IDEA** (Community or Ultimate)  
  https://www.jetbrains.com/idea/download/

---

## Setup & Installation

### Step 1 — Clone or Download the Project
```bash
git clone https://github.com/your-username/Airline-Management-System-JAVA-CSE-215.git
```
Or download and extract the ZIP file.

### Step 2 — Start XAMPP and Enable MySQL
1. Open **XAMPP Control Panel**.
2. Click **Start** next to **Apache** and **MySQL**.
3. Confirm MySQL is running on port **3306**.

### Step 3 — Import the Database
1. Open your browser and go to: `http://localhost/phpmyadmin`
2. Click **New** in the left sidebar and create a database named `airline_management`.
3. Select the `airline_management` database.
4. Click the **Import** tab.
5. Click **Choose File** and select `Database/airline_management.sql` from the project folder.
6. Click **Import** to execute the SQL and create all tables with sample data.

### Step 4 — Configure the Project in Your IDE

#### Eclipse
1. Open Eclipse and go to **File → Import → Existing Projects into Workspace**.
2. Select the project root folder and click **Finish**.
3. Right-click the project → **Build Path → Configure Build Path**.
4. Under the **Libraries** tab, click **Add External JARs** and add:
   - `mysql-connector-j-x.x.x.jar` (MySQL JDBC driver)
5. Also add the JavaFX library:
   - Click **Add Library → User Library → New** and name it `JavaFX`.
   - Add all `.jar` files from the `lib/` folder of your JavaFX SDK.
6. Under **Run Configurations**, add the following VM arguments:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```
   Replace `/path/to/javafx-sdk` with the actual path to your JavaFX SDK.

#### IntelliJ IDEA
1. Open IntelliJ and select **File → Open**, then choose the project folder.
2. Go to **File → Project Structure → Libraries** and add:
   - The `mysql-connector-j-x.x.x.jar` file.
   - All `.jar` files from the `lib/` folder of your JavaFX SDK.
3. Go to **Run → Edit Configurations**, select your `Main` run configuration, and add to **VM options**:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```

### Step 5 — Verify Database Credentials
Open `src/application/DBConnection.java` and confirm the credentials match your MySQL setup:
```java
private static final String URL  = "jdbc:mysql://localhost:3306/airline_management";
private static final String USER = "root";
private static final String PASSWORD = ""; // Default XAMPP has no password
```
Change `USER` or `PASSWORD` if your MySQL configuration differs.

### Step 6 — Run the Application
Run `src/application/Main.java` as a Java application from your IDE.

The login screen will open. Use the following default credentials:

| Role  | Username | Password |
|-------|----------|----------|
| Admin | admin    | admin    |
| Staff | staff    | staff    |
| User  | Register a new account via the Register button |

---

## Project Structure

```
Airline-Management-System/
├── Database/
│   └── airline_management.sql      # SQL dump with schema and sample data
├── src/
│   └── application/
│       ├── Main.java               # Application entry point
│       ├── DBConnection.java       # JDBC database connection
│       ├── Session.java            # Stores logged-in user session data
│       ├── LoginController.java    # Login & role-based navigation
│       ├── RegisterController.java
│       ├── AdminDashboardController.java
│       ├── UserDashboardController.java
│       ├── StaffDashboardController.java
│       ├── FlightManagementController.java
│       ├── SearchFlightController.java
│       ├── BookFlightController.java
│       ├── PaymentController.java
│       ├── CouponManagementController.java
│       ├── EmployeeManagementController.java
│       ├── ReportsController.java
│       ├── TicketController.java
│       └── *.fxml                  # UI layout files
└── bin/                            # Compiled class files
```

---

## Database Schema

| Table       | Description                                      |
|-------------|--------------------------------------------------|
| `users`     | Stores all users with roles (admin, staff, user) |
| `flights`   | Flight schedules with pricing and seat count     |
| `bookings`  | All booking records with payment and status      |
| `seats`     | Seat availability per flight                     |
| `coupons`   | Discount coupon codes with expiry dates          |
| `employees` | Links staff/admin users to employee records      |
