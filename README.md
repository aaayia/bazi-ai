# Bazi Mini - Smart Numerology Assistant

This project consists of a Spring Boot backend and a Uni-app frontend.

## Prerequisites

- Java 17 or higher
- Maven 3.x
- Node.js 16+
- WeChat Developer Tools (for frontend)

## Backend Setup

1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Configure your DeepSeek API Key in `src/main/resources/application.properties`:
   ```properties
   deepseek.api.key=YOUR_API_KEY
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   The backend will start on `http://localhost:8080`.

## Frontend Setup

1. Navigate to the `frontend` directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run in development mode for WeChat Mini Program:
   ```bash
   npm run dev:mp-weixin
   ```
4. Open WeChat Developer Tools and import the `frontend/dist/dev/mp-weixin` directory.

## Architecture

- **Backend**: Spring Boot, Hutool, Lunar-Java, OkHttp.
- **Frontend**: Uni-app (Vue 3), Tailwind CSS, Pinia.
- **AI**: DeepSeek API (JSON Mode).
