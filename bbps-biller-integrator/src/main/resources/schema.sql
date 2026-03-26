
CREATE TABLE if not exists billerdb.bbps_master (
    biller_id SERIAL PRIMARY KEY,
    biller_name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,  -- electricity, water, gas, etc.
    bbps_biller_id VARCHAR(50) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    bbps_endpoint_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE if not exists billerdb.customer_params_master (
    param_id SERIAL PRIMARY KEY,
    param_name VARCHAR(100) NOT NULL,
    param_type VARCHAR(50) NULL,
    param_value VARCHAR(100) NOT NULL,
    validation_regex VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE if not exists billerdb.bill_details (
    bill_id SERIAL PRIMARY KEY,
    customer_param_name VARCHAR(100) NOT NULL,
    customer_param_type VARCHAR(50) NULL,
    customer_param_value VARCHAR(100) NOT NULL,
    bill_amount NUMERIC(12,2) NOT NULL,
    bill_date DATE,
    due_date DATE,
    bill_number varchar(1000) null,
    bill_period VARCHAR(50),
    bill_status VARCHAR(20) DEFAULT 'UNPAID',
    additional_info JSONB,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE if not exists billerdb.payment_transactions (
    txn_id SERIAL PRIMARY KEY,
    bill_id INT REFERENCES billerdb.bill_details(bill_id),
    bbps_txn_ref VARCHAR(100) UNIQUE NOT NULL,
    amount_paid NUMERIC(12,2) NOT NULL,
    payment_mode VARCHAR(50),
    payment_channel varchar(50) NULL,
    payment_status VARCHAR(20) DEFAULT 'SUCCESS',
    payer_info JSONB,
    paid_at TIMESTAMP DEFAULT NOW()
);