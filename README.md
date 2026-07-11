# 🎓 ITC Helper Bot

A Telegram bot built with **Spring Boot** that serves as a smart assistant for students and staff at **KIPFIN College**. Provides access to college services, schedule info, and notifications right in Telegram.

## ✨ Features

- 👤 Role-based access control — Student, Teacher, Admin, Department roles
- 📋 Application service for submitting and tracking student requests
- 👥 User registration and profile management
- 🔔 Smart notifications and updates
- 🏢 Department system for routing requests
- ⚙️ Configurable settings per role/user

## 🛠️ Tech Stack

- **Java 17** + **Spring Boot 3**
- **Telegram Bots API** (`telegrambots 6.x`)
- **Spring Data JPA** + database persistence
- **Lombok**

## 📁 Project Structure

```
src/main/java/me/marensovich/itsKipfin/
├── bot/            # Telegram bot + command manager
├── services/       # ApplicationService, UserService
├── data/           # Domain: Department, Permission, Role
├── database/       # Repositories & persistence
├── config/         # Spring configuration
├── settings/       # Runtime settings
├── utils/          # Helpers
└── ItcKipifinApplication.java
```

## 🚀 Getting Started

```bash
git clone https://github.com/marensovich/ITC_Helper.git
cd ITC_Helper && git checkout dev
# Configure application.properties with your bot token
./gradlew bootRun
```

## 👤 Author

[@marensovich](https://github.com/marensovich)
