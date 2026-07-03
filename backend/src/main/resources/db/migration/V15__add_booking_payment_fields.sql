-- ============================================================
-- bookings: razorpay gateway linkage + amount snapshot
-- ============================================================
ALTER TABLE bookings
    ADD COLUMN razorpay_order_id   VARCHAR(64),
    ADD COLUMN razorpay_payment_id VARCHAR(64),
    ADD COLUMN amount              NUMERIC(10,2) NOT NULL DEFAULT 0,
    ADD COLUMN currency            VARCHAR(3)    NOT NULL DEFAULT 'INR';

CREATE UNIQUE INDEX ux_bookings_razorpay_order_id
    ON bookings (razorpay_order_id)
    WHERE razorpay_order_id IS NOT NULL;

CREATE INDEX idx_bookings_status_created
    ON bookings (status, created_at);
