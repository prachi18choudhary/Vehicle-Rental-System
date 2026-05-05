# Local Setup Guide (Without Docker & RabbitMQ)

This guide provides a comprehensive, step-by-step walkthrough for setting up and running the Vehicle Rental System on a new machine natively, bypassing Docker and RabbitMQ entirely.

## 📋 Prerequisites

Ensure the new machine has the following installed before proceeding:

1. **Java 17 (JDK)**: Required to compile and run the Spring Boot backend services.
2. **Maven (v3.8+)**: Required to build the Java dependencies and projects.
3. **Node.js (v18+) & npm**: Required to run the React frontend.
4. **MySQL Server (v8.0+)**: Must be installed and actively running locally on the default port (`3306`).

---

## 🛠️ Configuration & Setup

### 1. MySQL Database Preparation
Ensure your local MySQL server is running. You **do not** need to manually create the individual databases (e.g., `auth_db`, `vehicle_db`). The application uses Flyway for schema migrations and the JDBC flag `createDatabaseIfNotExist=true`, meaning all required tables and schemas will be created automatically on the first run.

### 2. Environment Variables (`.env`)
In the root directory of the project, locate the `.env` file. You need to configure your MySQL credentials to match the database on your new laptop.

```env
# Change these to match your new laptop's actual MySQL root credentials
DB_USER=root
DB_PASSWORD=your_actual_mysql_password
```
*Note: If your MySQL server does not have a password, you can leave it blank, but it must match your local MySQL configuration.*

---

## 🚀 Starting the Backend

Because this is a microservices architecture, you must start the services in a specific order so that dependencies (like the Eureka registry) are available when the downstream services boot up. 

Open a new terminal window or tab for **each** service below, and run the commands from the root directory of the project.

### Step 1: Start Eureka Server (Service Registry)
This acts as the phonebook for all other microservices to find each other.
```bash
cd backend/eureka-server
mvn spring-boot:run
```
*Wait until you see "Started EurekaServerApplication" in the logs (runs on port `8761`).*

### Step 2: Start API Gateway
This routes requests from the frontend to the correct microservice.
```bash
cd backend/api-gateway
mvn spring-boot:run
```
*Runs on port `8080`.*

### Step 3: Start Auth Service
Handles authentication and user management.
```bash
cd backend/auth-service
mvn spring-boot:run
```
*Runs on port `8081`.*

### Step 4: Start the Core Services (IMPORTANT)
For the remaining services, you **must** include the `-Dspring-boot.run.profiles=norabbitmq` flag. 
This flag disables all RabbitMQ listeners and activates the synchronous HTTP REST fallbacks that we have implemented.

**Vehicle Service**
```bash
cd backend/vehicle-service
mvn spring-boot:run -Dspring-boot.run.profiles=norabbitmq
```

**Booking Service**
```bash
cd backend/booking-service
mvn spring-boot:run -Dspring-boot.run.profiles=norabbitmq
```

**Payment Service**
```bash
cd backend/payment-service
mvn spring-boot:run -Dspring-boot.run.profiles=norabbitmq
```

**Notification Service**
```bash
cd backend/notification-service
mvn spring-boot:run -Dspring-boot.run.profiles=norabbitmq
```

### Verification
Once all terminals are running, open your browser and go to `http://localhost:8761`. You should see the Eureka dashboard with all 6 services (`API-GATEWAY`, `AUTH-SERVICE`, `VEHICLE-SERVICE`, `BOOKING-SERVICE`, `PAYMENT-SERVICE`, `NOTIFICATION-SERVICE`) registered and showing a status of `UP`.

---

## 💻 Starting the Frontend

Once the backend services are fully booted up and registered in Eureka, you can start the React frontend.

Open a final terminal window:
```bash
cd frontend
npm install
npm run dev
```

The application will launch and be available in your browser at **`http://localhost:5173`**.

> [!TIP]
> **Admin Access**: The system automatically seeds a default admin user into the database upon startup. 
> - **Email**: `admin@vrs.com`
> - **Password**: `Admin@123`
