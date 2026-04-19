# 🥘 Smart Pantry & Meal Manager

Welcome to the **Smart Pantry & Meal Manager**, an intelligent full-stack application designed to seamlessly bridge the gap between what you actually have in your kitchen and what you're eating this week. Built with modern web technologies, this platform prioritizes minimizing food waste, scaling meal portions effectively, and keeping your grocery trips localized only to what you actually need.

## 🚀 Features

### 1. Intelligent Meal Plan Strategies
The core of the manager is its Strategy-Pattern-based meal generation engine.
- **Pantry First**: Intelligently analyzes your current stock and pieces together a meal plan utilizing the highest concentration of ingredients you already own.
- **Zero Waste**: Audits the expiration dates of your inventory, strongly prioritizing recipes that use ingredients at risk of going bad.

### 2. Dynamic Proportional Servings
Gone are the days of rigid, 4-serving-only recipes. When you request a 5-Meal set from the planner, the engine dynamically partitions and scales down (or up!) recipe ingredients mathematically to meet your exact portion constraints without touching the integrity of your core recipe book.

### 3. Integrated Stock Alert System
During the meal plan generation, if the system realizes your requested strategy cannot be 100% fulfilled by local pantry items, it will trigger an immediate Grocery Alert and badge the meal plan, natively integrating the missing ingredients for review.

### 4. Living Inventory Tracking
As you mark meals as "Cooked" within your active plan, the respective ingredients are automatically deducted from the persistent virtual pantry in real-time.

## 🛠 Tech Stack

**Backend**
*   **Java 22** & **Spring Boot 3.4.4** RESTful API
*   **Spring Data JPA / Hibernate** for ORM mapping
*   **H2 In-Memory Database** (for rapid development and state management)
*   **Strategy Design Pattern** applied extensively to handle algorithmic meal selection

**Frontend**
*   **React** (Functional Components + Hooks)
*   **Vite** as a lightning-fast build tool
*   **Tailwind CSS** for responsive, utility-first styling UI elements

## 📦 How to Run Globally

This project uses an H2 in-memory database which seeds dynamically on boot. Follow these steps to run both environments simultaneously.

### Starting the Backend Server
```bash
# Navigate to the project root
cd smart-pantry

# Build & Run via Gradle
./gradlew bootRun
```
*The Spring Boot server will host the REST API on `http://localhost:8080`. Note: If you receive a Port 8080 in-use error, verify you do not have any zombie Java processes running in the background!*

### Starting the Frontend UI
Open a new, separate terminal window:
```bash
# Navigate to the frontend directory
cd smart-pantry/frontend

# Install dependencies via npm (if first time)
npm install

# Start the dev server
npm run dev
```
*The React UI will automatically load up at `http://localhost:5173`.*

---
*Note for NotebookLM: This README serves as a comprehensive system overview. Key discussion points for the presentation slide deck should focus on the algorithmic **Strategy Pattern** implementation for intelligent matching, the **Defensive Entity Modeling** for proportional recipe scaling, and the **React state management** bridging the pantry-to-recipe data pipeline.*
