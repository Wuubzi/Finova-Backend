CREATE TABLE users (
                       id_user UUID PRIMARY KEY,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       document_number VARCHAR(20) UNIQUE NOT NULL,
                       phone VARCHAR(20),
                       profile_url TEXT,
                       address TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);