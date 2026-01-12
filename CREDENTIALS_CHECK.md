# 🔐 Credentials Verification Guide

## Error: "Invalid Key" when paying

This means your Paystack public key is not configured or is invalid.

---

## ✅ How to Fix

### Step 1: Check Your local.properties File

Location: `C:\Users\alfre\StudioProjects\coffeshop\local.properties`

It should look like this:

```properties
sdk.dir=C:\\Users\\alfre\\AppData\\Local\\Android\\Sdk

# These MUST be filled with real values:
supabase.url=https://uwzcpezmvbsbjnbyyvmv.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.ACTUAL_KEY_HERE
paystack.key=pk_test_ACTUAL_KEY_HERE
```

### Step 2: Get Your Paystack Key

1. **Go to Paystack Dashboard**: https://dashboard.paystack.com
2. **Login** to your account
3. **Settings** → **API Keys & Webhooks**
4. **Copy the Test Public Key** (starts with `pk_test_`)

Example of a valid key:
```
pk_test_4d2e3f5a6b7c8d9e0f1a2b3c4d5e6f7g
```

### Step 3: Add Key to local.properties

```properties
# Replace YOUR_KEY_HERE with actual key
paystack.key=pk_test_4d2e3f5a6b7c8d9e0f1a2b3c4d5e6f7g
```

**Important:**
- ✅ NO quotes around the value
- ✅ NO spaces around the `=`
- ✅ Use TEST key (`pk_test_`) for development
- ❌ DON'T use LIVE key (`pk_live_`) in development

### Step 4: Rebuild App

```bash
# In Android Studio:
1. File → Sync Project with Gradle Files
2. Build → Clean Project
3. Build → Rebuild Project
4. Run the app again
```

---

## Verify Your Credentials

### Check if local.properties is loaded:

Open your `local.properties` file and verify:

```properties
# ✅ CORRECT FORMAT:
paystack.key=pk_test_abc123def456

# ❌ WRONG FORMATS:
paystack.key=                    # Empty
paystack.key="pk_test_abc123"   # Has quotes
paystack.key = pk_test_abc123   # Spaces around =
paystack.key=YOUR_KEY_HERE      # Placeholder not replaced
```

---

## Get Supabase Credentials

### Supabase URL:
1. Go to https://supabase.com/dashboard
2. Select your project
3. Settings → API
4. Copy **Project URL**

Example: `https://yourproject.supabase.co`

### Supabase Anon Key:
1. Same page (Settings → API)
2. Copy **anon/public key** (starts with `eyJ`)

Example: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

---

## Complete local.properties Example

```properties
## This file must *NOT* be checked into Version Control Systems
# It contains your local configuration and credentials

# Android SDK Location (required for Gradle)
# IMPORTANT: Use double backslashes (\\) for Windows paths
sdk.dir=C:\\Users\\alfre\\AppData\\Local\\Android\\Sdk

# ============================================
# APP CREDENTIALS (REQUIRED)
# ============================================

# Supabase Configuration
# Get from: https://supabase.com/dashboard/project/_/settings/api
supabase.url=https://uwzcpezmvbsbjnbyyvmv.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV3emNwZXptdmJzYmpuYnl5dm12Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzY1MTkwMzIsImV4cCI6MjA1MjA5NTAzMn0.YOUR_ACTUAL_KEY

# Paystack Configuration
# Get from: https://dashboard.paystack.com/#/settings/developer
# USE TEST KEY FOR DEVELOPMENT (pk_test_...)
paystack.key=pk_test_your_actual_test_key_here
```

---

## Test Credentials

After adding credentials:

### 1. Test Supabase Connection:
- Register a new user in the app
- If successful, Supabase is working ✅

### 2. Test Paystack:
- Add item to cart
- Go to checkout
- Click "Pay with Paystack"
- If browser opens with Paystack page, it's working ✅

---

## Still Not Working?

### Check BuildConfig:

The app reads credentials from `BuildConfig`. After changing `local.properties`:

1. **Clean build**:
   ```
   Build → Clean Project
   ```

2. **Rebuild**:
   ```
   Build → Rebuild Project
   ```

3. **Restart app** on device

---

## Security Reminder

### ⚠️ IMPORTANT:

1. **NEVER** commit `local.properties` to Git (it's in `.gitignore`)
2. **USE** test keys for development (`pk_test_`)
3. **ROTATE** any exposed keys immediately
4. **DON'T** share keys via email/chat
5. **USE** live keys (`pk_live_`) only in production

---

## Quick Checklist

Before running the app:

- [ ] `local.properties` file exists
- [ ] File has `sdk.dir` path
- [ ] `supabase.url` is filled (not placeholder)
- [ ] `supabase.key` is filled (not placeholder)
- [ ] `paystack.key` is filled (not placeholder)
- [ ] No quotes around any values
- [ ] No spaces around `=`
- [ ] Using TEST key for Paystack
- [ ] Gradle synced successfully
- [ ] Project rebuilt after changes

---

## Need Keys?

### Create Accounts:

**Supabase** (Free):
- https://supabase.com
- Create project
- Get URL and key from Settings → API

**Paystack** (Free Test Mode):
- https://paystack.com
- Create account
- Get test key from Settings → API Keys

---

**After fixing credentials, rebuild and run the app. Payment should work!** 💳✅
