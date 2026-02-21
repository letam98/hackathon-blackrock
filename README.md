
Retirement Savings & Micro‑Investment API
A production-grade Spring Boot (Java 21) micro‑investment engine that transforms daily expenses into long-term retirement savings.\ This system processes transactions, applies temporal rules (Q, P & K), performs compound interest projections, inflation adjustment, and calculates tax benefits based on Indian tax slabs.


📑 Table of Contents

\#overview
\#features
\#architecture
\#installation--setup
\#configuration
\#core-engine-logic
\#api-endpoints
\#building--dockerizing
\#publishing-to-docker-hub
\#financial-formulas
\#error-handling
\#validation-rules
\#project-structure


🧾 Overview
This API automates savings by rounding up daily spending to the nearest hundred, applying special rules for specific date ranges, and projecting future returns in NPS & Nifty 50.\ It is built for a coding challenge requiring strict processing order, fixed output formatting, and a mandatory server port.


🛠 Tech Stack

Java 21
Spring Boot 3+
Gradle
Lombok
Docker
Jackson (custom date format)


✨ Features
✔ Round-up based micro-investment engine\ ✔ Q-rule override (dominant temporal rule)\ ✔ P-rule additive enrichment\ ✔ K-period grouped remnant aggregation\ ✔ Compound interest projections\ ✔ Inflation-adjusted real returns\ ✔ Tax benefit calculations (Indian slabs)\ ✔ Duplicate & negative transaction validation\ ✔ Global exception handling\ ✔ Fully containerized & publicly deployable


🧱 Architecture
controller/    service/    model/    validator/    exception/


⚙ Installation & Setup
1. Clone Repository
git clone <your-repo-url>
cd <project-folder>

2. Build Project
./gradlew clean build

3. Run Application
./gradlew bootRun

API is available at:\ http://localhost:5477


🔧 Configuration
Mandatory Port
src/main/resources/application.properties:
server.port=5477
Global JSON Date Format
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss    spring.jackson.time-zone=UTC


🧠 Core Engine Logic (5-Step Pipeline)
Step 1 — Enrichment
For every expense:
rounded = ceil(amount / 100) * 100    remnant = rounded - amount


Step 2 — Q Rule Override
When date ∈ Q-range:

Replace remnant with q.fixedAmount
If multiple Q-ranges overlap:Rule with latest startDate wins


Step 3 — P Rule Addition
When date ∈ P-range:
remnant += p.extra
This stacks with Q rules.


Step 4 — K Period Grouping
Group final remnants into their corresponding K periods:
K(total-remnant) = sum(remnants where date ∈ K period)


Step 5 — Return Projections
NPS @ 7.11%
Nifty 50 @ 14.49%
Duration = 60 – currentAge
Compound interest:
A = P (1 + r)^t


Inflation Adjustment (5.5%)
A_real = A / (1 + 0.055)^t


Tax Benefit Logic
Tax benefit =\ Tax(income) – Tax(income – NPS deduction)\ Using Indian tax slabs.


📡 API Endpoints


POST /transactions/process
Process expenses → apply rules → return projections.
✔ Example Request
{
  "currentAge": 30,
  "expenses": [
    {"date": "2023-05-10 13:22:00", 
     "amount": 340}
  ],
  "qRules": [],
  "pRules": [],
  "kRules": []
}

✔ Example Response
(Structure may vary depending on implementation)


POST /transactions/validate
Validates expenses for:

Negative amount
Duplicate entries (same date + amount)


🐳 Building & Dockerizing
1. Build JAR
./gradlew clean build

Output: build/libs/*.jar


2. Dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 5477
ENTRYPOINT ["java", "-jar", "app.jar"]



3. Build Docker Image
docker build -t ketan3010/financial-service-api .



4. Run Container
docker run -p 5477:5477 <dockerhub-username>/financial-service-api

Test API:\ http://localhost:5477


🌍 Publishing to Docker Hub
1. Login
docker login

2. Push Image
docker push ketan3010/micro-invest-api

3. Make Repo Public
Docker Hub → Repository → Settings → Public
4. Submit Link
Submit your Docker Hub image URL as required.


📘 Financial Formulas
1️⃣ Compound Interest
A = P (1 + r)^t
2️⃣ Inflation Adjustment
A_real = A / (1 + i)^t
3️⃣ Tax Benefit
benefit = tax(income) - tax(income - npsDeduction)


🛡 Error Handling
Global exception handler catches:

Date parsing errors
Mathematical errors
Invalid rule ranges
Missing fields
Response: 400 Bad Request


🧪 Validation Rules
Rule	Condition
Negative Transaction	amount < 0
Duplicate	Same date + same amount in same request
Invalid Date Format	Not yyyy-MM-dd HH:mm:ss




If you'd like, I can also generate:\ ✅ UML diagram\ ✅ Sequence diagrams\ ✅ Sample test cases\ ✅ Postman collection JSON
Just tell me!

