CREATE TABLE user_credentials (
                                   id SERIAL PRIMARY KEY,
                                   user_id INT NOT NULL,
                                   email VARCHAR(150) UNIQUE NOT NULL,
                                   password VARCHAR(255) NOT NULL,
                                   role VARCHAR(50) NOT NULL,
                                   is_active BOOLEAN DEFAULT TRUE,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);