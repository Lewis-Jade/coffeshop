# 🚀 Deployment & Configuration Guide

## Prerequisites Checklist

Before running the application, ensure you have:

- ✅ Android Studio (Arctic Fox or later)
- ✅ JDK 11 or higher
- ✅ Android SDK (API 24+)
- ✅ Active internet connection
- ✅ Supabase account (free tier is sufficient)
- ✅ Paystack account (test mode available)

---

## Step-by-Step Configuration

### 1. Clone and Open Project

```bash
git clone <repository-url>
cd MMUST-Coffee-Shop
```

Open the project in Android Studio and let it sync Gradle dependencies.

---

### 2. Set Up Supabase Backend

#### 2.1 Create Supabase Project
1. Go to https://supabase.com
2. Click "New Project"
3. Fill in project details:
   - **Name**: MMUST Coffee Shop
   - **Database Password**: (save this securely)
   - **Region**: Choose closest to Kenya
4. Wait for project to be created (~2 minutes)

#### 2.2 Get API Credentials
1. Go to Project Settings → API
2. Copy the following:
   - **Project URL**: `https://xxxxx.supabase.co`
   - **Anon/Public Key**: `eyJhbGc...` (long string)

#### 2.3 Create Database Tables
1. Go to SQL Editor in Supabase Dashboard
2. Copy the entire SQL script from `SUPABASE_SETUP.md`
3. Run the script
4. Verify tables are created in Table Editor

#### 2.4 Configure Row Level Security
The SQL script automatically sets up RLS policies. Verify in:
- Table Editor → Select any table → Click "Policies" tab

---

### 3. Set Up Paystack

#### 3.1 Create Paystack Account
1. Go to https://paystack.com
2. Sign up (or use test account)
3. Verify your email

#### 3.2 Get API Keys
1. Go to Settings → API Keys & Webhooks
2. Copy **Public Key (Test)**: `pk_test_xxxxx`
3. (For production, use Live keys after verification)

#### 3.3 Test Mode Setup
- Paystack test mode allows testing without real money
- Use test card: `5531 8866 5214 2950` (any future expiry, any CVV)

---

### 4. Configure Application

#### 4.1 Create Configuration File

**Copy the template:**

```bash
# Windows
Copy-Item local.properties.template local.properties

# Mac/Linux
cp local.properties.template local.properties
```

#### 4.2 Update Credentials in local.properties

Open: `local.properties` (in project root)

Replace the placeholders:

```properties
# Supabase Configuration
supabase.url=https://yourproject.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# Paystack Configuration
paystack.key=pk_test_your_actual_key_here
```

**⚠️ Important Security Notes:**
- `local.properties` is in .gitignore (won't be committed)
- Never commit real API keys to version control
- Each developer creates their own `local.properties`
- Keep credentials secure and private

📖 **For detailed credential setup, see `SETUP_GUIDE.md`**

---

### 5. Sync and Build

#### 5.1 Gradle Sync
1. In Android Studio: File → Sync Project with Gradle Files
2. Wait for sync to complete
3. Resolve any dependency issues

#### 5.2 Build Project
```bash
./gradlew clean build
```

Or in Android Studio: Build → Make Project

---

### 6. Insert Sample Data

#### 6.1 Add Sample Products

In Supabase SQL Editor, run:

```sql
INSERT INTO products (name, description, price, image_url, available) VALUES
('Latte', 'Smooth milk coffee with a rich aroma', 150.00, null, true),
('Cappuccino', 'Strong and creamy Italian coffee', 200.00, null, true),
('Espresso', 'Bold and pure coffee shot', 100.00, null, true),
('Americano', 'Classic black coffee', 120.00, null, true),
('Mocha', 'Chocolate flavored coffee', 180.00, null, true),
('Macchiato', 'Espresso with a dash of milk', 140.00, null, true);
```

**Note**: The app currently uses local drawable images. Product images from database can be integrated in future updates.

---

### 7. Run the Application

#### 7.1 Connect Device/Emulator
- **Physical Device**: Enable USB debugging
- **Emulator**: Create AVD (API 24+)

#### 7.2 Run
1. Click "Run" in Android Studio (or Shift+F10)
2. Select target device
3. Wait for app to install and launch

---

## Testing the Application

### Test Workflow

#### 1. **User Registration**
- Open app
- Click "Register"
- Fill in:
  - Full Name: Test User
  - Email: test@example.com
  - Phone: +254712345678
  - Gender: Select one
  - Password: testpass123
  - Confirm Password: testpass123
- Click Register → Confirm
- Should navigate to Dashboard

#### 2. **Browse Products**
- Default screen shows product list (Drinks tab)
- Click any product to view details
- Adjust quantity with +/- buttons
- Click "Add to Cart"

#### 3. **Manage Cart**
- Click "Cart" tab in bottom navigation
- View cart items
- Update quantities or remove items
- Check total amount

#### 4. **Checkout & Payment**
- Click "Proceed to Checkout"
- Review order summary
- Click "Pay with Paystack"
- Use Paystack test card:
  - **Card**: 5531 8866 5214 2950
  - **Expiry**: Any future date (e.g., 12/25)
  - **CVV**: 123
  - **PIN**: 1234
  - **OTP**: 123456
- Complete payment flow

#### 5. **View Orders**
- Click "Orders" tab
- See completed order with status
- Check order details

#### 6. **Logout**
- Click "Home" tab
- Click "Logout" button
- Returns to welcome screen

---

## Troubleshooting

### Common Issues

#### 1. "Network Error" / "Connection Failed"

**Cause**: Supabase credentials not set or incorrect

**Solution**:
- Verify `SUPABASE_URL` and `SUPABASE_ANON_KEY` in Constants.java
- Check internet connection
- Verify Supabase project is active

#### 2. "Login Failed: Invalid credentials"

**Cause**: User not registered or wrong password

**Solution**:
- Register a new account first
- Check email confirmation if required (Supabase settings)
- Verify auth is enabled in Supabase

#### 3. "Payment Failed"

**Cause**: Paystack key not set or incorrect

**Solution**:
- Verify `PAYSTACK_PUBLIC_KEY` in Constants.java
- Use Paystack test credentials
- Check Paystack account is active

#### 4. Build Errors

**Cause**: Dependency issues or SDK version mismatch

**Solution**:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

#### 5. "Empty Products List"

**Cause**: No products in database

**Solution**:
- Run the INSERT products SQL from Step 6.1
- Verify products exist in Supabase Table Editor

#### 6. "Orders Not Loading"

**Cause**: RLS policies blocking access

**Solution**:
- Verify RLS policies are set correctly
- Check user is authenticated (token valid)
- Review Supabase logs for errors

---

## Environment-Specific Configuration

### Development
- Use Supabase test project
- Use Paystack test keys
- Enable debug logging

### Production
1. Create production Supabase project
2. Get Paystack live keys (requires business verification)
3. Update Constants.java with production credentials
4. Build signed APK:
   ```bash
   ./gradlew assembleRelease
   ```
5. Test thoroughly before deployment

---

## Security Best Practices

### For Development
- ✅ Use test credentials only
- ✅ Never commit API keys to Git
- ✅ Use `.gitignore` for sensitive files

### For Production
- ✅ Use environment variables or secure storage
- ✅ Enable Supabase email confirmation
- ✅ Set up proper RLS policies
- ✅ Use Paystack live keys only after verification
- ✅ Enable ProGuard/R8 for code obfuscation
- ✅ Implement SSL pinning
- ✅ Regular security audits

---

## Monitoring & Maintenance

### Supabase Dashboard
- Monitor database usage
- Check API logs
- View authentication events
- Analyze query performance

### Paystack Dashboard
- Monitor transactions
- Check settlement status
- View failed payments
- Configure webhooks for real-time updates

---

## Next Steps

### Optional Enhancements
1. **Admin Dashboard**: Implement admin UI for product/order management
2. **Push Notifications**: Integrate Firebase Cloud Messaging
3. **Image Upload**: Allow admins to upload product images
4. **Order History Details**: Show order items breakdown
5. **Reviews & Ratings**: Add product review system
6. **Promo Codes**: Implement discount system
7. **Analytics**: Track user behavior and sales

### Supabase Edge Functions (Advanced)
Set up webhook handler for Paystack:
```bash
npx supabase functions new paystack-webhook
# Copy webhook code from SUPABASE_SETUP.md
npx supabase functions deploy paystack-webhook
```

---

## Support

### Resources
- **Supabase Docs**: https://supabase.com/docs
- **Paystack Docs**: https://paystack.com/docs
- **Android Docs**: https://developer.android.com

### Files to Reference
- `README.md` - Original requirements
- `README_IMPLEMENTATION.md` - Implementation details
- `SUPABASE_SETUP.md` - Database schema
- `DEPLOYMENT_GUIDE.md` - This file

---

## Checklist Before First Run

- [ ] Supabase project created
- [ ] Database tables created (run SQL script)
- [ ] RLS policies configured
- [ ] Sample products inserted
- [ ] Paystack account created
- [ ] Constants.java updated with all credentials
- [ ] Gradle sync completed successfully
- [ ] Project builds without errors
- [ ] Device/emulator connected
- [ ] Internet connection active

---

## Quick Start Commands

```bash
# Sync dependencies
./gradlew build

# Run on connected device
./gradlew installDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

---

**Ready to launch! 🚀**

Your MMUST Mobile Coffee Shop app is now configured and ready for testing.
