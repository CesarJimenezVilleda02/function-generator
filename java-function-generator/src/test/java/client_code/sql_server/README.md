# Books and Users Management System

A full-stack application for managing users and books. The backend is built with **Java**, and the frontend is built with **React**. The app provides dynamic search functionality for users and books using the **Function Generator API**.

---

## Project Structure

```
/frontend - React frontend for user interaction  
/backend  - Java backend with SQLite integration
```

The backend is built using **Java**, **SQLite**, and **NanoHTTPD**.

---

## Prerequisites

Before running the application, ensure the following are installed:

- **Java** (JDK 8 or higher)
- **SQLite**
- **Maven**
- **Node.js** (with npm)

---

## Instructions

### 1. Running the Frontend

Navigate to the `frontend` directory:

```bash
cd frontend
```

Install the dependencies:

```bash
npm install
```

Start the development server:

```bash
npm start
```

The frontend will be available at:  
**http://localhost:3000**

---

### 2. Running the Backend

Navigate to the `java-function-generator` directory:
```bash
cd ../../../../../
```

Execute the following Maven command:

```bash
mvn exec:java
```

The backend will process incoming requests and serve data to the frontend.

---

## Example JSON Data

The backend supports importing initial data via a JSON file. Below is an example format:

```json
{
  "users": [
    {
      "name": "Alice",
      "email": "alice@example.com",
      "company": "TechCorp",
      "age": 29
    },
    {
      "name": "Bob",
      "email": "bob@example.com",
      "company": "InnovateLtd",
      "age": 34
    }
  ],
  "books": [
    {
      "title": "1984",
      "author": "George Orwell",
      "genre": "Dystopian",
      "year": 1949
    },
    {
      "title": "Animal Farm",
      "author": "George Orwell",
      "genre": "Political Satire",
      "year": 1945
    }
  ]
}
```

### Loading JSON Data

1. Place the JSON file in the `backend` directory as `data.json`.
2. Start the backend server to load the data.

---

## Features

### 1. Searchable Users and Books
- Dynamically query users or books by specific terms.
- Search by attributes such as:
  - **Users**: Name, email, company, or age.
  - **Books**: Title, author, genre, or publication year.

### 2. Database Integration
- Uses **SQLite** to store users and books.
- Supports importing data from JSON files.

### 3. Frontend Integration
- User-friendly interface built with **React**.
- Dynamic search functionality for users and books.

---

## Tech Stack

### Backend
- **Java** (with NanoHTTPD and SQLite)
- **Maven** (Dependency Management)

### Frontend
- **React** (Components and State Management)
- **Axios** (HTTP Requests)