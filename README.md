# Vehicle Rental System

A production-grade microservices Vehicle Rental application built with **Spring Boot 3 + React 18**, demonstrating real-world patterns: service discovery, API gateway, JWT auth with RBAC, async event-driven communication, real-time WebSocket notifications, and integrated payment processing — all using **100% free** technology.

---

## Architecture

```
                    ┌─────────────────────┐
                    │  React SPA (Vite)   │ :5173
                    │  Tailwind + RTK     │
                    └──────────┬──────────┘
                          REST │ WebSocket(STOMP)
                    ┌──────────▼──────────┐
                    │   API Gateway       │ :8080  ← validates JWT, forwards X-User-* headers
                    └──────────┬──────────┘
                               │
        ┌────────┬──────────┬──┴──────────┬─────────────┐
        ▼        ▼          ▼             ▼             ▼
   ┌────────┐ ┌────────┐ ┌────────┐ ┌─────────┐ ┌──────────────┐
   │  Auth  │ │Vehicle │ │Booking │ │ Payment │ │ Notification │
   │  :8081 │ │ :8082  │ │ :8083  │ │  :8084  │ │   :8085      │
   └───┬────┘ └───┬────┘ └───┬────┘ └────┬────┘ └──────┬───────┘
       │ MySQL   │ MySQL    │ MySQL    │ MySQL       │ MySQL
       └─────────┴──────────┴──────────┴─────────────┘
                            ▲
                            │ Eureka registers all
                  ┌─────────┴─────────┐
                  │  Eureka Server    │ :8761
                  └───────────────────┘
                            ▲
                            │ async events
                  ┌─────────┴──────────┐
                  │     RabbitMQ       │ :5672 / UI :15672
                  │  booking.exchange  │
                  │  payment.exchange  │
                  └────────────────────┘
                            ▲
                            │
                  ┌─────────┴──────────┐
                  │  Razorpay (test)   │  Gmail SMTP
                  └────────────────────┘
```

### Microservices

| Service | Port | Responsibility |
|---|---|---|
| **eureka-server** | 8761 | Service registry |
| **api-gateway** | 8080 | Single entry point, JWT validation, CORS, routing |
| **auth-service** | 8081 | Register/login, JWT issuance + refresh, RBAC (USER/ADMIN), seeded admin |
| **vehicle-service** | 8082 | Vehicle CRUD (admin), search/filter, availability check, listens for `booking.cancelled` |
| **booking-service** | 8083 | Create/cancel/list bookings, Feign call to Vehicle, publishes booking events, scheduled stale-payment cleanup |
| **payment-service** | 8084 | Razorpay test integration: create order, verify signature, publish payment events |
| **notification-service** | 8085 | RabbitMQ consumer + Gmail SMTP + WebSocket/STOMP push + history persistence |

### Inter-service communication
- **Sync (REST + Feign)** — Booking → Vehicle (availability check), Payment → Booking (booking lookup)
- **Async (RabbitMQ topic exchanges)** —
  - `booking.exchange`: `booking.created`, `booking.confirmed`, `booking.cancelled`
  - `payment.exchange`: `payment.success`, `payment.failed`
- **Real-time (WebSocket/STOMP)** — Notification Service → React (per-user topic `/topic/user.{userId}`)

---

## Tech Stack (100% Free)

**Backend** — Java 17, Spring Boot 3.2, Spring Cloud 2023 (Eureka, Gateway, OpenFeign, Resilience4j), Spring Security + JJWT, Spring Data JPA, Flyway, Spring AMQP, Spring WebSocket + STOMP, Spring Mail, Razorpay Java SDK, Lombok, Springdoc OpenAPI, Maven

**Frontend** — React 18, Vite, Redux Toolkit + RTK Query, React Router 6, TailwindCSS, axios, @stomp/stompjs + sockjs-client, Razorpay Checkout JS, react-hot-toast, react-datepicker, Lucide icons

**Infra** — MySQL 8, RabbitMQ 3 (with management UI), Docker + Docker Compose, Nginx (frontend container)

**External (free)** — Razorpay Test Mode, Gmail SMTP (App Password)

---

## Project Structure

```
vehicle-rental-system/
├── backend/
│   ├── pom.xml                    (parent — Spring Cloud BOM, common versions)
│   ├── common-lib/                (JWT util, event DTOs, exceptions, headers)
│   ├── eureka-server/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── vehicle-service/
│   ├── booking-service/
│   ├── payment-service/
│   ├── notification-service/
│   └── Dockerfile                 (generic — built once per service via SERVICE arg)
├── frontend/
│   ├── src/
│   │   ├── api/api.js             (RTK Query — every endpoint)
│   │   ├── features/authSlice.js
│   │   ├── ws/stompClient.js      (real-time STOMP connection)
│   │   ├── components/            (Navbar, NotificationBell, VehicleCard)
│   │   └── pages/                 (Home, Login, Register, Vehicles, VehicleDetail,
│   │                              BookVehicle, Payment, MyBookings, Notifications,
│   │                              AdminDashboard, AdminVehicles, AdminBookings)
│   ├── tailwind.config.js
│   ├── nginx.conf                 (production reverse proxy)
│   └── Dockerfile
├── docker-compose.yml             (one-command full stack)
├── .env.example
└── README.md
```

---

## Prerequisites

| Tool | Required Version | Why |
|---|---|---|
| **Java JDK** | 17 | Spring Boot 3.x |
| **Maven** | 3.8+ | Backend build (or use IDE) |
| **Node.js** | 20 LTS | Frontend dev server |
| **Docker Desktop** | latest | MySQL + RabbitMQ + (optional) all services |
| **Git** | any | Source control |

> All can be installed via Homebrew on macOS:
> ```bash
> brew install openjdk@17 maven node@20 git
> brew install --cask docker
> ```

---

## Free Credentials You Need (one-time setup)

### 1. Razorpay Test Mode
1. Sign up at <https://dashboard.razorpay.com/signup>
2. Switch to **Test Mode** (toggle top-left of dashboard)
3. **Settings → API Keys → Generate Test Key**
4. Copy `Key Id` and `Key Secret` into `.env`
5. Test card for checkout: `4111 1111 1111 1111`, any future expiry, any CVV

### 2. Gmail App Password (for email notifications)
1. Enable **2-Step Verification** at <https://myaccount.google.com/security>
2. Generate at <https://myaccount.google.com/apppasswords>
3. Copy the 16-char password into `MAIL_PASSWORD` in `.env`
4. Set `MAIL_ENABLED=true`

> **Don't want to set up email yet?** Set `MAIL_ENABLED=false` — notifications still appear in-app via WebSocket and the notification history page.

---

## Quick Start

### Option A — Full Stack via Docker Compose (recommended)

```bash
# 1. Copy env template and fill in your values (Razorpay, optional Gmail)
cp .env.example .env
# 2. Build & launch everything (first run takes 5-10 min for downloads)
docker compose up --build
# 3. Wait until all services are healthy, then open:
#    Frontend:        http://localhost:5173
#    Eureka:          http://localhost:8761
#    RabbitMQ UI:     http://localhost:15672  (guest/guest)
#    Swagger (auth):  http://localhost:8081/swagger-ui.html
```

To stop & remove everything (including DB volume):
```bash
docker compose down -v
```

### Option B — Run Locally (better for development)

#### 1. Start infra
```bash
docker compose up -d mysql rabbitmq
```

#### 2. Start each Spring Boot service (in separate terminals)
```bash
cd backend
mvn -pl eureka-server spring-boot:run         # terminal 1 — wait until 'Started EurekaServerApplication'
mvn -pl auth-service spring-boot:run          # terminal 2
mvn -pl vehicle-service spring-boot:run       # terminal 3
mvn -pl booking-service spring-boot:run       # terminal 4
mvn -pl payment-service spring-boot:run \
  -Dspring-boot.run.arguments="--razorpay.key-id=YOUR_KEY --razorpay.key-secret=YOUR_SECRET"  # terminal 5
mvn -pl notification-service spring-boot:run  # terminal 6
mvn -pl api-gateway spring-boot:run           # terminal 7 (start last)
```

> Tip: use IntelliJ "Run Dashboard" or VS Code multi-run to launch all services with one click.

#### 3. Start the React frontend
```bash
cd frontend
npm install
npm run dev
# → http://localhost:5173
```

---

## Default Credentials

| Account | Email | Password |
|---|---|---|
| **Admin** (auto-seeded) | `admin@vrs.com` | `Admin@123` |
| **Regular user** | register a new account at `/register` | — |

---

## Sample Test Flow (end-to-end)

1. Open <http://localhost:5173> → **Browse Vehicles**
2. Click any car → **Login to Book** → register a new account
3. Pick dates, enter pickup/drop-off → **Continue to Payment**
4. **Pay with Razorpay** → use test card `4111 1111 1111 1111`, any CVV, any future expiry
5. Upon success → notification toast appears (WebSocket), email sent if `MAIL_ENABLED=true`, booking goes from `PENDING_PAYMENT` → `CONFIRMED`
6. Visit **My Bookings** to see status, **Notifications** to see the bell history
7. Login as admin → **Admin → Manage Vehicles** to add/edit/delete fleet, **Admin → All Bookings** to see every booking

---

## API Documentation (Swagger UI)

Each service exposes Swagger when running:

- Auth — <http://localhost:8081/swagger-ui.html>
- Vehicle — <http://localhost:8082/swagger-ui.html>
- Booking — <http://localhost:8083/swagger-ui.html>
- Payment — <http://localhost:8084/swagger-ui.html>
- Notification — <http://localhost:8085/swagger-ui.html>

Or hit them through the gateway: `http://localhost:8080/<service-prefix>/swagger-ui.html`.

---

## Database

5 separate schemas auto-created (DB-per-service pattern):
- `auth_db` — users, roles, user_roles
- `vehicle_db` — vehicles (8 sample vehicles seeded)
- `booking_db` — bookings
- `payment_db` — payments
- `notification_db` — notifications

Schemas are managed by Flyway (`src/main/resources/db/migration/V1__init.sql` in each service).

---

## RBAC

- `ROLE_USER` — browse vehicles, book, pay, view own bookings, view own notifications
- `ROLE_ADMIN` — everything above + vehicle CRUD + view all bookings + admin dashboard

JWT is signed in `auth-service` (HS256). API Gateway validates the JWT and forwards `X-User-Id`, `X-User-Email`, `X-User-Roles` headers downstream so other services don't have to re-parse.

---

## Useful URLs

| What | URL |
|---|---|
| Frontend | <http://localhost:5173> |
| API Gateway | <http://localhost:8080> |
| Eureka Dashboard | <http://localhost:8761> |
| RabbitMQ Management | <http://localhost:15672> (guest / guest) |
| MySQL (TCP) | `localhost:3306` (root / root) |

---

## Troubleshooting

**Services won't start in Docker — "MySQL not ready"**
The healthcheck waits up to 100s. If MySQL is slow on first boot, just `docker compose up` again — services will reconnect.

**Razorpay error "Authentication failed"**
You're using the placeholder keys. Sign up (free) and put your test keys in `.env`.

**No emails delivered**
Set `MAIL_ENABLED=true` and use a Gmail App Password (NOT your account password). Or keep it disabled — WebSocket toasts + notification history still work.

**Port already in use**
Change the host port in `docker-compose.yml` (e.g. `"8081:8081"` → `"19081:8081"`).

**Frontend can't reach backend**
Make sure API Gateway is up at <http://localhost:8080/actuator/health>. The Vite dev server proxies `/api/*` and `/ws/*` to it.

---

## License

MIT — feel free to use this as a portfolio reference or starting point for your own project.
