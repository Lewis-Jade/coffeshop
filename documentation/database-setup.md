# Supabase Database Setup Guide

## Step 1: Create Supabase Project
1. Go to https://supabase.com and create a new project
2. Note down your Project URL and Anon Key

## Step 2: Update Constants.java
Update the following file with your Supabase credentials:
- File: `app/src/main/java/com/example/coffeecafe/utils/Constants.java`
- Replace `YOUR_SUPABASE_URL` with your actual Supabase URL
- Replace `YOUR_SUPABASE_ANON_KEY` with your actual Anon Key
- Replace `YOUR_PAYSTACK_PUBLIC_KEY` with your Paystack public key

## Step 3: Create Database Tables

Run the following SQL commands in your Supabase SQL Editor:

```sql
-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Products Table
CREATE TABLE products (
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
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    total_amount DECIMAL(10, 2) NOT NULL,
    status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'paid', 'completed', 'cancelled')),
    payment_reference TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Order Items Table
CREATE TABLE order_items (
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
CREATE TABLE payments (
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

-- Create indexes for better performance
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_reference ON payments(payment_reference);

-- Create updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add triggers
CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

## Step 4: Set up Row Level Security (RLS)

```sql
-- Enable RLS on all tables
ALTER TABLE products ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE order_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE payments ENABLE ROW LEVEL SECURITY;

-- Products Policies (public read, admin write)
-- Drop existing policies first to avoid conflicts
DROP POLICY IF EXISTS "Products are viewable by everyone" ON products;
DROP POLICY IF EXISTS "Products are insertable by authenticated users" ON products;
DROP POLICY IF EXISTS "Products are updatable by admin" ON products;
DROP POLICY IF EXISTS "Products are deletable by admin" ON products;

-- Create new policies
CREATE POLICY "Products are viewable by everyone"
    ON products FOR SELECT
    USING (true);

CREATE POLICY "Products are insertable by authenticated users"
    ON products FOR INSERT
    TO authenticated
    WITH CHECK (
        COALESCE(
            (auth.jwt()->>'user_metadata')::jsonb->>'is_admin',
            'false'
        )::boolean = true
    );

CREATE POLICY "Products are updatable by admin"
    ON products FOR UPDATE
    TO authenticated
    USING (
        COALESCE(
            (auth.jwt()->>'user_metadata')::jsonb->>'is_admin',
            'false'
        )::boolean = true
    );

CREATE POLICY "Products are deletable by admin"
    ON products FOR DELETE
    TO authenticated
    USING (
        COALESCE(
            (auth.jwt()->>'user_metadata')::jsonb->>'is_admin',
            'false'
        )::boolean = true
    );

-- Orders Policies (users can only see their own orders, admins see all)
-- Drop existing policies first
DROP POLICY IF EXISTS "Users can view their own orders" ON orders;
DROP POLICY IF EXISTS "Users can insert their own orders" ON orders;
DROP POLICY IF EXISTS "Users can update their own orders" ON orders;

-- Create new policies
CREATE POLICY "Users can view their own orders"
    ON orders FOR SELECT
    TO authenticated
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own orders"
    ON orders FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own orders"
    ON orders FOR UPDATE
    TO authenticated
    USING (auth.uid() = user_id);

-- Order Items Policies
-- Drop existing policies first
DROP POLICY IF EXISTS "Users can view their own order items" ON order_items;
DROP POLICY IF EXISTS "Users can insert their own order items" ON order_items;

CREATE POLICY "Users can view their own order items"
    ON order_items FOR SELECT
    TO authenticated
    USING (EXISTS (
        SELECT 1 FROM orders 
        WHERE orders.id = order_items.order_id 
        AND orders.user_id = auth.uid()
    ));

CREATE POLICY "Users can insert their own order items"
    ON order_items FOR INSERT
    TO authenticated
    WITH CHECK (EXISTS (
        SELECT 1 FROM orders 
        WHERE orders.id = order_items.order_id 
        AND orders.user_id = auth.uid()
    ));

-- Payments Policies
-- Drop existing policies first
DROP POLICY IF EXISTS "Users can view their own payments" ON payments;
DROP POLICY IF EXISTS "System can insert payments" ON payments;
DROP POLICY IF EXISTS "System can update payments" ON payments;

CREATE POLICY "Users can view their own payments"
    ON payments FOR SELECT
    TO authenticated
    USING (EXISTS (
        SELECT 1 FROM orders 
        WHERE orders.id = payments.order_id 
        AND orders.user_id = auth.uid()
    ));

CREATE POLICY "System can insert payments"
    ON payments FOR INSERT
    TO authenticated
    WITH CHECK (true);

CREATE POLICY "System can update payments"
    ON payments FOR UPDATE
    TO authenticated
    USING (true);
```

## Step 5: Insert Sample Products

```sql
-- Insert sample coffee products
INSERT INTO products (name, description, price, image_url, available) VALUES
('Latte', 'Smooth milk coffee with a rich aroma', 10, null, true),
('Cappuccino', 'Strong and creamy Italian coffee', 20, null, true),
('Espresso', 'Bold and pure coffee shot', 30, null, true),
('Americano', 'Classic black coffee', 15, null, true),
('Mocha', 'Chocolate flavored coffee', 25, null, true);
```

## Step 6: Configure Paystack
1. Go to https://paystack.com and create an account
2. Get your Public Key from the dashboard
3. Update the `PAYSTACK_PUBLIC_KEY` in `Constants.java`

## Step 7: Test the Application
1. Sync Gradle
2. Run the application
3. Register a new account
4. Browse products and add to cart
5. Proceed to checkout and test payment

## Optional: Supabase Edge Functions (for webhooks)

Create a webhook handler for Paystack payment verification:

```typescript
// supabase/functions/paystack-webhook/index.ts
import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'

serve(async (req) => {
  try {
    const signature = req.headers.get('x-paystack-signature')
    const body = await req.text()
    
    // Verify webhook signature here
    
    const event = JSON.parse(body)
    
    if (event.event === 'charge.success') {
      const reference = event.data.reference
      const amount = event.data.amount / 100 // Convert from kobo
      
      // Create Supabase client
      const supabaseClient = createClient(
        Deno.env.get('SUPABASE_URL') ?? '',
        Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
      )
      
      // Update payment and order status
      await supabaseClient
        .from('payments')
        .update({ status: 'success' })
        .eq('payment_reference', reference)
      
      const { data: payment } = await supabaseClient
        .from('payments')
        .select('order_id')
        .eq('payment_reference', reference)
        .single()
      
      if (payment) {
        await supabaseClient
          .from('orders')
          .update({ status: 'paid' })
          .eq('id', payment.order_id)
      }
    }
    
    return new Response(JSON.stringify({ received: true }), {
      headers: { 'Content-Type': 'application/json' },
      status: 200,
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      headers: { 'Content-Type': 'application/json' },
      status: 400,
    })
  }
})
```

Deploy with:
```bash
supabase functions deploy paystack-webhook
```
