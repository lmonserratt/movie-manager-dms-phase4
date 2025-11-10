# 🎬 Movie Manager DMS (Phase 4 – MySQL Integration)

**Course:** CEN 3024C – Software Development I  
**Author:** Luis Augusto Monserratt Alvarado  
**Professor:** Dr. Lisa Macon 
**Semester:** Fall 2025  

---

🧩 Project Overview

Movie Manager DMS is a Java 17 application with a Swing GUI and MySQL backend. It lets users perform CRUD on movies, search by title, and run a custom action that calculates the average duration of all movies.

This is Phase 4 of a multi-phase project:

OOP design (encapsulation, inheritance, polymorphism)

GUI with Swing

JDBC + MySQL data persistence

Robust validation and exception handling
---

## ⚙️ Features

✅ Create, read, update, delete movies

✅ Search by title (case-insensitive LIKE)

✅ Custom action: Average Duration

✅ Swing GUI with table + dialogs

✅ MySQL connection via JDBC

✅ Input validation + friendly error messages

✅ Modular layers: Model → DAO → Service → GUI

---

## 🗂️ Project Structure

movie-manager-dms-phase4/
├─ pom.xml
├─ README.md
├─ docs/                         # Exported Javadoc (open docs/index.html)
├─ dist/
│  └─ movie-manager-dms-1.0.0.jar
├─ sql/
│  ├─ schema.sql                 # CREATE TABLE movies (…)
│  └─ sample_data.sql            # 20+ sample rows (optional)
└─ src/
   └─ main/java/dms/
      ├─ app/
      │  └─ GuiMainMysql.java    # Entry point (prompts for DB creds)
      ├─ dao/
      │  └─ MysqlMovieDao.java   # JDBC DAO (CRUD + search)
      ├─ gui/
      │  ├─ MovieFormDialog.java
      │  └─ MovieTableFrameMysql.java
      ├─ model/
      │  └─ Movie.java
      └─ service/
         └─ MovieService.java
💾 Database Setup (MySQL / DataGrip)

1. Create DB:

CREATE DATABASE dms_movies;


2. Create table + sample data:

. Run sql/schema.sql

. (Optional) run sql/sample_data.sql

3. Verify:

USE dms_movies;
SELECT COUNT(*) FROM movies;
SELECT * FROM movies LIMIT 5;


Expected schema (movies):

movie_id         VARCHAR(10) PRIMARY KEY,
title            VARCHAR(100) NOT NULL,
director         VARCHAR(100),
release_year     INT,
duration_minutes INT,
genre            VARCHAR(50),
rating           DOUBLE


---

## 🧠 Class Architecture

| Layer | Class | Responsibility |
|-------|--------|----------------|
| **app** | `GuiMainMysql` | Entry point — initializes the GUI and handles DB credentials |
| **dao** | `MysqlMovieDao` | Database access logic using JDBC |
| **model** | `Movie` | Represents a movie entity (id, title, director, year, duration) |
| **service** | `MovieService` | Business logic between DAO and GUI |
| **gui** | `MovieTableFrameMysql`, `MovieFormDialog` | Swing interface for user interaction |

---


🚀 How to Run the Application

🧱 Option 1: Run from IntelliJ IDEA

Open the project.

Run the class: dms.app.GuiMainMysql

When prompted, enter:

Host: localhost

User: your MySQL user (e.g., root)

Password: your MySQL password

GUI loads and displays data from dms_movies.movies.

💻 Option 2: Run from Terminal

cd dist
java -jar movie-manager-dms-1.0.0.jar


You’ll be prompted for host/user/password (same as IntelliJ). (localhost, username: root, password: root)

Option 3 — System properties / Env vars (optional)

You can preconfigure connection:

System properties: -DJDBC_URL=... -DDB_USER=... -DDB_PASS=...

Env vars: JDBC_URL, DB_USER, DB_PASS

Example JDBC URL (code):

jdbc:mysql://localhost:3306/dms_movies?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8

Example JDBC URL (Javadoc HTML):

jdbc:mysql://localhost:3306/dms_movies?serverTimezone=UTC&amp;useUnicode=true&amp;characterEncoding=utf8



🧮 Custom Action:
From the main window, click “Average Duration” to compute and display the average of duration_minutes for all rows in movies.

Example Custom Action:
When clicking Calculate Average Duration, the program computes the average of all movie durations and shows it in a message dialog box:
🎥 Average Duration: 121.4 minutes

🧰 Technologies Used

Java 17

Swing GUI

MySQL 8+

JDBC

Maven

IntelliJ IDEA

DataGrip

🧪 Validation & Error Handling

Checks for empty fields, invalid inputs, and SQL exceptions.

Prompts user before deleting records.

Ensures safe DB connection handling (connect, close, and isConnected methods).

🛠️ Build, Docs & Packaging

Build + tests + shaded JAR:
mvn -q clean package

Generate Javadoc:
mvn -q javadoc:javadoc

Export Javadoc into repo (so it’s present in IntelliJ and GitHub):
mkdir -p docs
rsync -a --delete target/reports/apidocs/ docs/
open docs/index.html

📦 Submission (Module 11)

GitHub link: provide your repository URL (branch main, up to date)

Javadoc export: upload a ZIP of /docs to Canvas

zip -r DMS_Javadoc_Luis_Monserratt.zip docs


📸 Demonstration Video

🎥 [Watch the Final Phase 4 Video Presentation on YouTube](https://youtu.be/G01ehAZjiaY)


📚 Acknowledgments

Special thanks to Professor Ashley Evans and Valencia College for guidance through all DMS project phases:

Phase 1: CLI File-Based

Phase 2: Unit Testing

Phase 3: GUI Integration

Phase 4: MySQL Database Integration

🧑‍💻 Author

Luis Augusto Monserratt Alvarado
📍 Orlando, FL
📧 lmonserrattalvara@mail.valenciacollege.edu

🌐 GitHub Profile
