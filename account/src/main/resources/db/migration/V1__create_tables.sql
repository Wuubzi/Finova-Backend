CREATE TABLE account (
    account_id UUID PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    user_id UUID NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    available_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    alias VARCHAR(100),
    overdraft_limit DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_cache (
    user_id UUID PRIMARY KEY
);

CREATE INDEX idx_user_id ON account (user_id);
CREATE INDEX idx_account_number ON account (account_number);
CREATE INDEX idx_status ON account (status);
CREATE INDEX idx_user_status ON account (user_id, status);
