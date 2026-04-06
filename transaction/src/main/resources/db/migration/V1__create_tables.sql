CREATE TABLE transaction (
                             transaction_id UUID PRIMARY KEY,
                             from_account_id UUID,
                             to_account_id UUID,
                             amount DECIMAL(15, 2) NOT NULL,
                             currency VARCHAR(3) NOT NULL,
                             status VARCHAR(20) NOT NULL, -- PENDING, COMPLETED, FAILED
                             type VARCHAR(20) NOT NULL,   -- TRANSFER, DEPOSIT, WITHDRAW
                             description VARCHAR(255),
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_from_account ON transaction (from_account_id);
CREATE INDEX idx_to_account ON transaction (to_account_id);
CREATE INDEX idx_status ON transaction (status);