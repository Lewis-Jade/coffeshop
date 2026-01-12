-- ============================================
-- MMUST Coffee Shop - Complete Supabase Setup
-- ============================================
-- Run this entire script in Supabase SQL Editor
-- Safe to run multiple times (includes IF NOT EXISTS checks)

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- STEP 1: Create Tables
-- ============================================

-- Products Table
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url TEXT,
    available BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Orders Table
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    total_amount DECIMAL(10, 2) NOT NULL,
    status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'paid', 'completed', 'cancelled')),
    payment_reference TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Order Items Table
CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID REFERENCES products(id) ON DELETE SET NULL,
    product_name TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    payment_reference TEXT UNIQUE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'success', 'failed')),
    provider TEXT DEFAULT 'paystack',
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ============================================
-- STEP 2: Create Indexes
-- ============================================

CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_reference ON payments(payment_reference);

-- ============================================
-- STEP 3: Create Trigger Function
-- ============================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- ============================================
-- STEP 4: Create Triggers
-- ============================================

DROP TRIGGER IF EXISTS update_products_updated_at ON products;
CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_orders_updated_at ON orders;
CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_payments_updated_at ON payments;
CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- STEP 5: Enable Row Level Security
-- ============================================

ALTER TABLE products ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE order_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE payments ENABLE ROW LEVEL SECURITY;

-- ============================================
-- STEP 6: Products Policies
-- ============================================

-- Drop existing policies
DROP POLICY IF EXISTS "Products are viewable by everyone" ON products;
DROP POLICY IF EXISTS "Products are insertable by authenticated users" ON products;
DROP POLICY IF EXISTS "Products are updatable by admin" ON products;
DROP POLICY IF EXISTS "Products are deletable by admin" ON products;

-- Everyone can view products
CREATE POLICY "Products are viewable by everyone"
    ON products FOR SELECT
    USING (true);

-- Only admins can insert products
CREATE POLICY "Products are insertable by authenticated users"
    ON products FOR INSERT
    TO authenticated
    WITH CHECK (
        COALESCE(
            (auth.jwt()->>'user_metadata')::jsonb->>'is_admin',
            'false'
        )::boolean = true
    );

-- Only admins can update products
CREATE POLICY "Products are updatable by admin"
    ON products FOR UPDATE
    TO authenticated
    USING (
        COALESCE(
            (auth.jwt()->>'user_metadata')::jsonb->>'is_admin',
            'false'
        )::boolean = true
    );

-- Only admins can delete products
CREATE POLICY "Products are deletable by admin"
    ON products FOR DELETE
    TO authenticated
    USING (
        COALESCE(
            (auth.jwt()->>'user_metadata')::jsonb->>'is_admin',
            'false'
        )::boolean = true
    );

-- ============================================
-- STEP 7: Orders Policies
-- ============================================

-- Drop existing policies
DROP POLICY IF EXISTS "Users can view their own orders" ON orders;
DROP POLICY IF EXISTS "Users can insert their own orders" ON orders;
DROP POLICY IF EXISTS "Users can update their own orders" ON orders;

-- Users can view their own orders
CREATE POLICY "Users can view their own orders"
    ON orders FOR SELECT
    TO authenticated
    USING (auth.uid() = user_id);

-- Users can insert their own orders
CREATE POLICY "Users can insert their own orders"
    ON orders FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = user_id);

-- Users can update their own orders
CREATE POLICY "Users can update their own orders"
    ON orders FOR UPDATE
    TO authenticated
    USING (auth.uid() = user_id);

-- ============================================
-- STEP 8: Order Items Policies
-- ============================================

-- Drop existing policies
DROP POLICY IF EXISTS "Users can view their own order items" ON order_items;
DROP POLICY IF EXISTS "Users can insert their own order items" ON order_items;

-- Users can view their own order items
CREATE POLICY "Users can view their own order items"
    ON order_items FOR SELECT
    TO authenticated
    USING (EXISTS (
        SELECT 1 FROM orders 
        WHERE orders.id = order_items.order_id 
        AND orders.user_id = auth.uid()
    ));

-- Users can insert their own order items
CREATE POLICY "Users can insert their own order items"
    ON order_items FOR INSERT
    TO authenticated
    WITH CHECK (EXISTS (
        SELECT 1 FROM orders 
        WHERE orders.id = order_items.order_id 
        AND orders.user_id = auth.uid()
    ));

-- ============================================
-- STEP 9: Payments Policies
-- ============================================

-- Drop existing policies
DROP POLICY IF EXISTS "Users can view their own payments" ON payments;
DROP POLICY IF EXISTS "System can insert payments" ON payments;
DROP POLICY IF EXISTS "System can update payments" ON payments;

-- Users can view their own payments
CREATE POLICY "Users can view their own payments"
    ON payments FOR SELECT
    TO authenticated
    USING (EXISTS (
        SELECT 1 FROM orders 
        WHERE orders.id = payments.order_id 
        AND orders.user_id = auth.uid()
    ));

-- System can insert payments
CREATE POLICY "System can insert payments"
    ON payments FOR INSERT
    TO authenticated
    WITH CHECK (true);

-- System can update payments
CREATE POLICY "System can update payments"
    ON payments FOR UPDATE
    TO authenticated
    USING (true);

-- ============================================
-- STEP 10: Insert Sample Products
-- ============================================

-- Only insert if products table is empty
INSERT INTO products (name, description, price, image_url, available)
SELECT * FROM (VALUES
    ('Latte', 'Smooth milk coffee with a rich aroma', 150.00, null, true),
    ('Cappuccino', 'Strong and creamy Italian coffee', 200.00, null, true),
    ('Espresso', 'Bold and pure coffee shot', 100.00, null, true),
    ('Americano', 'Classic black coffee', 120.00, null, true),
    ('Mocha', 'Chocolate flavored coffee', 180.00, null, true),
    ('Macchiato', 'Espresso with a dash of milk', 140.00, null, true)
) AS new_products (name, description, price, image_url, available)
WHERE NOT EXISTS (SELECT 1 FROM products LIMIT 1);

-- ============================================
-- SUCCESS MESSAGE
-- ============================================

DO $$
BEGIN
    RAISE NOTICE '✅ Database setup complete!';
    RAISE NOTICE 'Tables created: products, orders, order_items, payments';
    RAISE NOTICE 'RLS policies applied successfully';
    RAISE NOTICE 'Sample products inserted';
END $$;
