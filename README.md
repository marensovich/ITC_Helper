# 🎓 ITC Helper Bot

A **Telegram bot** built with Spring Boot to assist students of **KIPFIN (ITC) College**.

## 📋 Description

A helpful companion bot for college students. Provides access to schedules, announcements, and other useful college-related information directly through Telegram.

## 🛠️ Tech Stack

- **Java** — core language
- **Spring Boot** — application framework
- **Telegram Bot API** — messaging platform
- **Maven** — build tool

## ✨ Features

- Student information assistant via Telegram
- Schedule and announcement access
- Service-based architecture (ApplicationService, UserService)
- Command manager pattern for extensibility
- Easy college resource access from mobile

## 🚀 Running

```bash
# Clone the repository
git clone https://github.com/marensovich/ITC_Helper.git
cd ITC_Helper

# Configure your bot token in application.properties
# BOT_TOKEN=your_telegram_bot_token

# Build and run
mvn spring-boot:run
```

> Requires: Java 17+, Maven, Telegram Bot Token
