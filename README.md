# 🩸 LifeStream — Blood Donation Management System

A full-stack **Spring Boot + Thymeleaf** web application for managing the national blood donation workflow — from donor registration and mobile blood camps, to lab testing, inventory, hospital requests, and cold-chain delivery.

Built as a university project for **SLIIT (Sri Lanka Institute of Information Technology)** — SE2030 Software Engineering.

---

## ✨ Features

The system supports **five roles**, each with its own portal:

| Role | What they can do |
|---|---|
| 👤 **Donor** | Register, log in, view profile, request/book donations, view donation history |
| 🏥 **Hospital** | Register, log in, raise blood requests, track request status & history |
| 🏢 **Organization** | Register, log in, request mobile blood camps, view camp history |
| 🧪 **Lab Tester** | Log in, view pending blood tests, record test results, view tested blood history |
| 🛡️ **Admin** | Manage donors, hospitals, organizations, lab testers, drivers, cool boxes, blood bags, blood inventory, camp requests, hospital requests, deliveries |

### Core capabilities
- 🩸 Live national blood stock dashboard (by blood group, with critical/low alerts)
- 📅 Mobile blood camp scheduling & approval workflow
- 🧊 Cold-chain delivery tracking with drivers & cool boxes
- 🧪 Blood bag lab testing & screening pipeline
- 📧 Email-based password reset flow
- 🌐 Public marketing site — Home, Services, Events, For Donors, Contact

---

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot, Spring MVC, Spring Data JPA (Hibernate)
- **Frontend:** Thymeleaf, Tailwind CSS (CDN), Material Symbols icons
- **Database:** MySQL
- **Mail:** Spring Mail (SMTP / Gmail)
- **Build tool:** Maven

---

## 📂 Project Structure

```
src/main/java/com/blooddonation/
├── controller/     # MVC controllers (per role: Admin*, Donor*, Hospital*, Organization*, LabTest*)
├── service/        # Business logic
├── repository/     # Spring Data JPA repositories
├── entity/         # JPA entities (Donor, Hospital, Organization, BloodBag, CampRequest, ...)
└── util/           # Validation & form-error helpers

src/main/resources/
├── templates/       # Thymeleaf HTML pages, grouped by role (admin/, donor/, hospital/, ...)
├── templates/fragments/  # Shared header/footer fragment used across public pages
└── application.properties
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven (or use the included `./mvnw`)
- MySQL server running locally

### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/bloodDonation.git
cd bloodDonation
```

### 2. Create the database
```sql
CREATE DATABASE blood_donation_db;
```

### 3. Configure your local settings
Update `src/main/resources/application.properties` with your own MySQL credentials and mail settings:
```properties
spring.datasource.username=your_mysql_user
spring.datasource.password=your_mysql_password

spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```
> ⚠️ Never commit real credentials — use environment variables or a local `.properties` override that's git-ignored (see below).

### 4. Run the application
```bash
./mvnw spring-boot:run
```
The app starts at **http://localhost:8080**

Tables are auto-created/updated on startup (`spring.jpa.hibernate.ddl-auto=update`).

---

## 🔐 Environment / Secrets

This project currently reads DB and mail credentials from `application.properties`. Before pushing to a public repo, make sure `application.properties` (or a `mail-secret.properties` / `.env`) containing real passwords is added to `.gitignore`, and provide an `application.properties.example` with placeholder values instead.

---

## 🗺️ Roadmap / Ideas
- [ ] Wire the Contact page form to send mail via `EmailService`
- [ ] Multi-language support (Sinhala / Tamil toggle already in the header UI)
- [ ] Blood bank locator with map search
- [ ] Push/SMS notifications for critical blood group shortages

---

## 👥 Team / Contributors
Add your team members here:
- Hansala
- Viswa
- Kashmira
- Sahan
- Hirusha
- Seya
