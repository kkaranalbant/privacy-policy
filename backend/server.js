const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const initDb = require('./db');
// const { v4: uuidv4 } = require('uuid'); // Removed as not used and not installed

const app = express();
const PORT = 3000;
const SECRET_KEY = "my_secret_key_123";

app.use(cors());
app.use(bodyParser.json());

let db;

// Initialize DB
initDb().then(async (_db) => {
    db = _db;
    console.log('Database initialized');

    // Seed Books if empty
    const count = await db.get('SELECT COUNT(*) as count FROM books');
    if (count.count === 0) {
        console.log("Seeding initial books...");
        const now = Date.now();
        await db.run(`INSERT INTO books (id, title, author, isbn, category, description, published_year, cover_image_url, rating, total_reviews, added_at) VALUES 
            ('1', 'Clean Code', 'Robert C. Martin', '9780132350884', 'Technology', 'Even bad code can function. But if code isn\'\'t clean, it can bring a development organization to its knees.', 2008, 'https://m.media-amazon.com/images/I/41xShlnTZTL._SX218_BO1,204,203,200_QL40_FMwebp_.jpg', 5.0, 10, ${now}),
            ('2', 'The Hobbit', 'J.R.R. Tolkien', '9780547928227', 'Fiction', 'In a hole in the ground there lived a hobbit.', 1937, 'https://m.media-amazon.com/images/I/91b0C2YNSrL._AC_UF1000,1000_QL80_.jpg', 4.8, 20, ${now}),
            ('3', 'Design Patterns', 'Erich Gamma', '0201633612', 'Technology', 'Capturing a wealth of experience about the design of object-oriented software.', 1994, 'https://m.media-amazon.com/images/I/51k+0d2l84L._SX377_BO1,204,203,200_.jpg', 4.9, 15, ${now})
        `);
    }

    // Seed Admin User
    const adminExists = await db.get('SELECT * FROM users WHERE role = ?', ['ADMIN']);
    if (!adminExists) {
        console.log("Seeding default ADMIN user...");
        const hashedAdminPw = await bcrypt.hash("admin123", 10);
        await db.run(
            `INSERT INTO users (username, email, password, role, fullName, created_at) VALUES (?, ?, ?, ?, ?, ?)`,
            ['admin', 'admin@library.com', hashedAdminPw, 'ADMIN', 'System Admin', Date.now()]
        );
    }
});

// Middleware for JWT verification
const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];
    if (token == null) return res.sendStatus(401);

    jwt.verify(token, SECRET_KEY, (err, user) => {
        if (err) return res.sendStatus(403);
        req.user = user;
        next();
    });
};

// --- AUTH ROUTES ---

app.post('/auth/register', async (req, res) => {
    const { username, email, password, fullName, role } = req.body;
    try {
        const hashedPassword = await bcrypt.hash(password, 10);
        const createdAt = Date.now();

        const result = await db.run(
            `INSERT INTO users (username, email, password, role, fullName, created_at) VALUES (?, ?, ?, ?, ?, ?)`,
            [username, email, hashedPassword, role, fullName, createdAt]
        );

        const user = {
            userId: result.lastID.toString(),
            username,
            email,
            role,
            fullName,
            createdAt,
            passwordHash: hashedPassword // In real app, don't send this back ideally, but Android app UserEntity has it.
        };

        const token = jwt.sign({ userId: user.userId, role: user.role }, SECRET_KEY);

        res.json({ token, user });
    } catch (e) {
        console.error(e);
        res.status(400).json({ message: "Registration failed. Username or Email may already exist." });
    }
});

app.post('/auth/login', async (req, res) => {
    const { email, password } = req.body; // Client now sends 'password'
    // Android App sends: LoginRequest(val email: String, val password: String)
    // The previous code in AuthViewModel sends the raw password string into the 'passwordHash' field. 
    // So 'req.body.passwordHash' actually contains the raw password.

    try {
        const user = await db.get(`SELECT * FROM users WHERE email = ?`, [email]);
        if (!user) {
            return res.status(400).json({ message: "User not found" });
        }

        const validPassword = await bcrypt.compare(password, user.password);
        if (!validPassword) {
            return res.status(400).json({ message: "Invalid password" });
        }

        const token = jwt.sign({ userId: user.id, role: user.role }, SECRET_KEY);

        // Map DB fields to API response expected by Android
        const userResponse = {
            userId: user.id.toString(),
            username: user.username,
            email: user.email,
            passwordHash: user.password,
            role: user.role,
            fullName: user.fullName,
            createdAt: user.created_at
        };

        res.json({ token, user: userResponse });
    } catch (e) {
        console.error(e);
        res.status(500).json({ message: "Login error" });
    }
});

// --- BOOK ROUTES ---

app.get('/books', authenticateToken, async (req, res) => {
    try {
        const books = await db.all(`SELECT * FROM books`);
        // Map snake_case to CamelCase if necessary, but I used compatible names mostly.
        // Android BookEntity: bookId, title, author, isbn, category, description...
        // DB: id, title, author...
        const mappedBooks = books.map(b => ({
            bookId: b.id,
            title: b.title,
            author: b.author,
            isbn: b.isbn,
            category: b.category,
            description: b.description,
            publishedYear: b.published_year,
            coverImageUrl: b.cover_image_url,
            averageRating: b.rating || 0.0,
            totalReviews: b.total_reviews || 0,
            addedAt: b.added_at
        }));
        res.json(mappedBooks);
    } catch (e) {
        res.status(500).send(e.toString());
    }
});

app.get('/books/:id', authenticateToken, async (req, res) => {
    try {
        const b = await db.get(`SELECT * FROM books WHERE id = ?`, [req.params.id]);
        if (!b) return res.status(404).send("Book not found");

        const book = {
            bookId: b.id,
            title: b.title,
            author: b.author,
            isbn: b.isbn,
            category: b.category,
            description: b.description,
            publishedYear: b.published_year,
            coverImageUrl: b.cover_image_url,
            averageRating: b.rating || 0.0,
            totalReviews: b.total_reviews || 0,
            addedAt: b.added_at
        };
        res.json(book);
    } catch (e) {
        res.status(500).send(e.toString());
    }
});

app.post('/books', authenticateToken, async (req, res) => {
    // Basic Admin check
    if (req.user.role !== 'ADMIN') {
        return res.status(403).send("Access denied. Admins only.");
    }
    const { title, author, isbn, category, description, publishedYear, coverImageUrl } = req.body;
    const id = Date.now().toString(); // Simple ID generation
    const addedAt = Date.now();

    try {
        await db.run(
            `INSERT INTO books (id, title, author, isbn, category, description, published_year, cover_image_url, rating, total_reviews, added_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
            [id, title, author, isbn, category, description, publishedYear, coverImageUrl, 0.0, 0, addedAt]
        );
        res.status(201).json({ status: "Book added" });
    } catch (e) {
        res.status(500).send(e.toString());
    }
});

app.delete('/books/:id', authenticateToken, async (req, res) => {
    if (req.user.role !== 'ADMIN') {
        return res.status(403).send("Access denied. Admins only.");
    }
    try {
        await db.run('DELETE FROM books WHERE id = ?', [req.params.id]);
        // Also delete related favorites and reviews
        await db.run('DELETE FROM favorites WHERE book_id = ?', [req.params.id]);
        await db.run('DELETE FROM reviews WHERE book_id = ?', [req.params.id]);
        res.json({ message: "Book deleted successfully" });
    } catch (err) {
        res.status(500).send(err.message);
    }
});

// --- RECOMMENDATION ROUTE (Dummy) ---
// --- FAVORITE ROUTES ---
app.post('/favorites/:bookId', authenticateToken, async (req, res) => {
    // Add to favorites
    try {
        await db.run(`INSERT OR IGNORE INTO favorites (user_id, book_id) VALUES (?, ?)`, [req.user.userId, req.params.bookId]);
        res.status(200).json({ message: "Added to favorites" });
    } catch (e) {
        res.status(500).send(e.toString());
    }
});

app.delete('/favorites/:bookId', authenticateToken, async (req, res) => {
    // Remove from favorites
    try {
        await db.run(`DELETE FROM favorites WHERE user_id = ? AND book_id = ?`, [req.user.userId, req.params.bookId]);
        res.status(200).json({ message: "Removed from favorites" });
    } catch (e) {
        res.status(500).send(e.toString());
    }
});

app.get('/favorites', authenticateToken, async (req, res) => {
    // Get user favorites
    try {
        const rows = await db.all(`SELECT book_id FROM favorites WHERE user_id = ?`, [req.user.userId]);
        const bookIds = rows.map(r => r.book_id);
        res.json(bookIds);
    } catch (e) {
        res.status(500).send(e.toString());
    }
});


// --- REVIEW ROUTES ---
app.post('/books/:bookId/reviews', authenticateToken, async (req, res) => {
    const { rating, comment } = req.body;
    const reviewId = Date.now().toString();
    const timestamp = Date.now();
    try {
        // Add review
        await db.run(
            `INSERT INTO reviews (id, user_id, book_id, rating, comment, timestamp) VALUES (?, ?, ?, ?, ?, ?)`,
            [reviewId, req.user.userId, req.params.bookId, rating, comment, timestamp]
        );

        // Update average rating (simplified) for the book
        const stats = await db.get(`SELECT AVG(rating) as avgRating, COUNT(*) as count FROM reviews WHERE book_id = ?`, [req.params.bookId]);
        await db.run(`UPDATE books SET rating = ?, total_reviews = ? WHERE id = ?`, [stats.avgRating, stats.count, req.params.bookId]);

        res.status(201).json({ message: "Review added" });
    } catch (e) {
        res.status(500).send(e.toString());
    }
});

app.get('/books/:bookId/reviews', authenticateToken, async (req, res) => {
    try {
        const reviews = await db.all(
            `SELECT r.*, u.username FROM reviews r JOIN users u ON r.user_id = u.id WHERE r.book_id = ? ORDER BY r.timestamp DESC`,
            [req.params.bookId]
        );
        const mappedReviews = reviews.map(r => ({
            id: r.id,
            userId: r.user_id,
            userName: r.username,
            bookId: r.book_id,
            rating: r.rating,
            comment: r.comment,
            timestamp: r.timestamp
        }));
        res.json(mappedReviews);
    } catch (e) {
        res.status(500).send(e.toString());
    }
});

app.delete('/reviews/:id', authenticateToken, async (req, res) => {
    if (req.user.role !== 'ADMIN') {
        return res.status(403).send("Access denied. Admins only.");
    }
    try {
        await db.run('DELETE FROM reviews WHERE id = ?', [req.params.id]);
        res.json({ message: "Review deleted successfully" });
    } catch (err) {
        res.status(500).send(err.message);
    }
});


app.get('/recommendations', authenticateToken, async (req, res) => {
    try {
        const books = await db.all(`SELECT * FROM books`);
        // Return random 3 books
        const shuffled = books.sort(() => 0.5 - Math.random());
        const selected = shuffled.slice(0, 3);
        const mappedBooks = selected.map(b => ({
            bookId: b.id,
            title: b.title,
            author: b.author,
            isbn: b.isbn,
            category: b.category,
            description: b.description,
            publishedYear: b.published_year,
            coverImageUrl: b.cover_image_url,
            averageRating: b.rating || 0.0,
            totalReviews: b.total_reviews || 0,
            addedAt: b.added_at
        }));
        res.json(mappedBooks);
    } catch (e) {
        res.status(500).send(e.toString());
    }
});


app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
