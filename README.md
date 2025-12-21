# Library Application

A comprehensive Library Management System featuring a Node.js backend and a native Android (Jetpack Compose) frontend.

## Features
- **User Authentication**: Register/Login with JWT.
- **Book Management**: Browse, Add, Delete books.
- **Favorites System**: Save books to favorites.
- **Reviews**: detailed rating and review system.
- **Admin Panel**: Dedicated dashboard for administrators to manage content.
- **Offline Support**: Local database synchronization.

## 🚀 Getting Started

### Prerequisites
- [Node.js](https://nodejs.org/) (installed and added to PATH)
- [Android Studio](https://developer.android.com/studio) (Koala or newer recommended)
- JDK 17 or higher

---

### 1. Backend Setup (Node.js)

The backend is a local Express.js server using SQLite.

1.  Open a terminal in the `backend/` directory:
    ```bash
    cd backend
    ```

2.  Install dependencies:
    ```bash
    npm install
    ```

3.  Start the server:
    ```bash
    node server.js
    ```
    *   *The server runs on `http://localhost:3000`.*
    *   *It automatically seeds default data and an Admin user on first run.*

---

### 2. Android App Setup

1.  Open the project in **Android Studio**.
2.  Allow Gradle Sync to complete.
3.  **Important**: Check the API Base URL.
    *   Open `app/src/main/java/com/kaan/libraryapplication/util/Constants.kt`.
    *   If using **Android Emulator**: Ensure it is `http://10.0.2.2:3000/`.
    *   If using a **Real Device**: Change it to your computer's local IP (e.g., `http://192.168.1.35:3000/`).
4.  Run the app on an Emulator (Pixel 6 API 30+ recommended).

---

### 🔑 Admin Credentials

To access the Admin Panel (Add/Delete Books, Delete Reviews):

*   **Email:** `admin@library.com`
*   **Password:** `admin123`

---

### 🛠 Troubleshooting

*   **Network Error / "Connection Refused"**:
    *   Ensure `node server.js` is running.
    *   Verify `Constants.BASE_URL` matches your network setup.
    *   If on emulator, `adb reverse tcp:3000 tcp:3000` might help if using localhost, but `10.0.2.2` is preferred.
    
*   **Compilation Errors**:
    *   Run `Build > Clean Project` and then `Rebuild Project`.
