# 📚 Smart Library Application

A comprehensive Library Management System featuring a **Kotlin Android** frontend with Jetpack Compose and a **Node.js Express** backend.

## 🚀 Features
- **User Authentication** - Register/Login with JWT tokens
- **Book Management** - Browse, add, and manage books
- **Favorites System** - Save books to your personal favorites
- **Review System** - Rate and review books with detailed feedback
- **Admin Panel** - Dashboard for administrators to manage content
- **Offline Support** - Local database synchronization
- **Dependency Injection** - Clean architecture with DI patterns

## 🛠️ Tech Stack

### Android Frontend
| Technology | Purpose |
|---|---|
| Kotlin | Primary language |
| Jetpack Compose | Modern declarative UI |
| Hilt/Dagger | Dependency Injection |
| Retrofit | Network requests |
| Room | Local database |

### Backend
| Technology | Purpose |
|---|---|
| Node.js | Runtime environment |
| Express.js | Web framework |
| SQLite | Database |
| JWT | Authentication |

## 📦 Getting Started

### Prerequisites
- [Node.js](https://nodejs.org/) (v18+)
- [Android Studio](https://developer.android.com/studio) (Koala or newer)
- JDK 17+

### Backend Setup
```bash
cd backend
npm install
node server.js
```
> Server runs on `http://localhost:3000` and seeds default data on first run.

### Android Setup
1. Open project in Android Studio
2. Wait for Gradle sync
3. Update API Base URL in `Constants.kt`:
   - Emulator: `http://10.0.2.2:3000/`
   - Real device: `http://YOUR_IP:3000/`
4. Run the app

### 🔑 Admin Credentials
- **Email:** `admin@library.com`
- **Password:** `admin123`

## 📂 Project Structure
```
├── app/                  # Android application
│   ├── src/main/java/    # Kotlin source code
│   └── src/main/res/     # Resources & layouts
├── backend/              # Node.js Express server
│   ├── server.js         # Main server file
│   └── package.json      # Dependencies
├── build.gradle.kts      # Project-level Gradle config
└── settings.gradle.kts   # Settings
```