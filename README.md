# CryptoBudget

CryptoBudget is a microservices-based application for managing personal budgets and tracking cryptocurrency portfolios.  
It allows users to record income and expenses, analyze financial data, and monitor their cryptocurrency portfolio’s real-time value.

---

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)

---

## Overview

CryptoBudget provides a complete solution for financial management and cryptocurrency tracking.  
The system is composed of multiple independent microservices, each responsible for a specific business function — including user authentication, bookkeeping, and portfolio management.

This modular design ensures scalability, maintainability, and reliability when processing large volumes of data and frequent requests to external APIs.

---

## Architecture

### Microservices Overview

- **UserService**  
  Handles user registration, authentication, and profile management.  
  Uses **Spring Security** and **JWT** for secure authentication and authorization.

- **AccountingService**  
  Provides a digital bookkeeping system where users can:  
  - Record income and expenses  
  - Categorize transactions  
  - Generate reports and perform financial analysis

- **PortfolioService**  
  Manages the user's cryptocurrency portfolio:  
  - Retrieves and caches cryptocurrency prices using **Redis**  
  - Calculates total portfolio value  
  - Publishes transaction events (buy/sell) to the **AccountingService** via **Kafka**

- **API Gateway**  
  Serves as a unified entry point for client requests, handling routing and authentication.

---

### Asynchronous Communication

- **Kafka** is used for asynchronous communication between services.  
  Example: When a user performs a buy or sell transaction in the portfolio, a corresponding record is sent to the AccountingService.

---

### Caching and Performance

- **Redis** is used to cache cryptocurrency prices and reduce dependency on third-party APIs, improving overall performance.

---

### Scalability and Orchestration

- The system supports containerization and orchestration using **Kubernetes**.  
- **Spring Cloud** is used for service discovery, configuration management, and fault tolerance.  
- **Nginx** can be used for load balancing and reverse proxying.

---

## Tech Stack

### Backend
- **Language:** Java 21  
- **Build Tool:** Maven  
- **Framework:** Spring Boot (Web, Security, Cloud)  
- **Authentication:** JWT  
- **Asynchronous Messaging:** Kafka  
- **Database:** PostgreSQL  
- **ORM:** Hibernate  
- **Caching:** Redis  
- **Architecture:** Microservices, Clean Architecture  
- **Utilities:** Lombok  

### Frontend
- **Language:** TypeScript  
- **Framework:** React  
- **Build Tool:** Vite  

### Infrastructure
- **API Gateway:** Spring Cloud Gateway  
- **Kafka:** Asynchronous inter-service communication  
- **Redis:** Caching of cryptocurrency data  
- **Kubernetes:** Container orchestration and scaling  
- **Nginx (optional):** Load balancing  
