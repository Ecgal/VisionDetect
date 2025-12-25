CREATE TABLE IF NOT EXISTS users (
                       id TEXT PRIMARY KEY,          -- UUID as string
                       username TEXT UNIQUE NOT NULL,
                       hashed_password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS photos (
                        id TEXT PRIMARY KEY,          -- UUID
                        filename TEXT NOT NULL,
                        owner_id TEXT NOT NULL,
                        created_at TEXT NOT NULL

);