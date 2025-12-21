const sqlite3 = require('sqlite3');
const { open } = require('sqlite');

async function initDb() {
  const db = await open({
    filename: './library.db',
    driver: sqlite3.Database
  });

  await db.exec(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      username TEXT UNIQUE,
      email TEXT UNIQUE,
      password TEXT,
      role TEXT,
      fullName TEXT,
      created_at INTEGER
    );

    CREATE TABLE IF NOT EXISTS books (
      id TEXT PRIMARY KEY,
      title TEXT,
      author TEXT,
      isbn TEXT,
      category TEXT,
      description TEXT,
      published_year INTEGER,
      cover_image_url TEXT,
      rating REAL,
      total_reviews INTEGER,
      added_at INTEGER
    );

    CREATE TABLE IF NOT EXISTS favorites (
      user_id TEXT,
      book_id TEXT,
      PRIMARY KEY (user_id, book_id)
    );

    CREATE TABLE IF NOT EXISTS reviews (
      id TEXT PRIMARY KEY,
      user_id TEXT,
      book_id TEXT,
      rating INTEGER,
      comment TEXT,
      timestamp INTEGER
    );
  `);

  return db;
}

module.exports = initDb;
