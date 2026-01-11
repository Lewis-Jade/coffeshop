# 🔐 Secure Credential Configuration Guide

## Overview

This application uses a secure, non-hardcoded credential management system. All sensitive credentials are stored in a separate configuration file that is excluded from version control.

---

## 📁 Configuration Structure

### Files:
- **`config.xml.template`** - Template file (committed to Git)
- **`config.xml`** - Actual credentials (NOT committed to Git, in .gitignore)

### Location:
```
app/src/main/res/values/
├── config.xml.template  ✅ Template (safe to commit)
└── config.xml           ❌ Credentials (ignored by Git)
```

---

## 🚀 Setup Instructions

### Step 1: Create Your Configuration File

Copy the template to create your actual config file:

#### On Windows:
```powershell
Copy-Item app/src/main/res/values/config.xml.template app/src/main/res/values/config.xml
```

#### On Mac/Linux:
```bash
cp app/src/main/res/values/config.xml.template app/src/main/res/values/config.xml
```

#### Or manually:
1. Navigate to `app/src/main/res/values/`
2. Copy `config.xml.template`
3. Rename the copy to `config.xml`

---

### Step 2: Get Your Credentials

#### Supabase Credentials

1. **Go to Supabase Dashboard**: https://supabase.com/dashboard
2. **Select your project**
3. **Go to Settings → API**
4. **Copy the following:**
   - **Project URL**: `https://xxxxx.supabase.co`
   - **Anon/Public Key**: `eyJhbGc...` (long string)

#### Paystack Credentials

1. **Go to Paystack Dashboard**: https://dashboard.paystack.com
2. **Go to Settings → API Keys & Webhooks**
3. **Copy your Public Key:**
   - **Test Mode**: `pk_test_xxxxx`
   - **Live Mode**: `pk_live_xxxxx` (use after verification)

---

### Step 3: Update config.xml

Open `app/src/main/res/values/config.xml` and replace the placeholders:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Supabase Configuration -->
    <string name="supabase_url">https://yourproject.supabase.co</string>
    <string name="supabase_anon_key">eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...</string>
    
    <!-- Paystack Configuration -->
    <string name="paystack_public_key">pk_test_your_actual_key_here</string>
</resources>
```

**⚠️ Important**: Replace ALL three values with your actual credentials!

---

### Step 4: Verify Configuration

1. **Sync Gradle**: File → Sync Project with Gradle Files
2. **Build Project**: Build → Make Project
3. **Run the app**: Should connect successfully

---

## 🔒 Security Features

### What's Protected:

✅ **No hardcoded credentials** in Java code
✅ **config.xml excluded** from Git (in .gitignore)
✅ **Template file committed** (no sensitive data)
✅ **Credentials loaded at runtime** from resources
✅ **Initialized in Application class** (singleton pattern)

### How It Works:

```
App Startup
    ↓
CoffeeShopApplication.onCreate()
    ↓
Constants.initialize(context)
    ↓
Load credentials from config.xml
    ↓
SupabaseClient initialized with credentials
    ↓
Paystack SDK initialized with public key
    ↓
Ready to use!
```

---

## 🛡️ Best Practices

### ✅ DO:
- Keep `config.xml` private (already in .gitignore)
- Use test credentials during development
- Change credentials regularly
- Use environment variables in CI/CD
- Document credential locations for team

### ❌ DON'T:
- Commit `config.xml` to Git
- Share credentials in chat/email
- Hardcode credentials in code
- Use production credentials in dev
- Store credentials in public places

---

## 👥 Team Collaboration

### For New Team Members:

1. **Clone the repository**
   ```bash
   git clone https://github.com/sibby-killer/coffeshop.git
   ```

2. **Copy the template**
   ```bash
   cp app/src/main/res/values/config.xml.template app/src/main/res/values/config.xml
   ```

3. **Get credentials from team lead**
   - Request Supabase credentials securely
   - Request Paystack credentials securely

4. **Update config.xml** with provided credentials

5. **Build and run**

---

## 🔄 Different Environments

### Development Environment

**File**: `config.xml` (local, not committed)

```xml
<string name="supabase_url">https://dev-project.supabase.co</string>
<string name="supabase_anon_key">dev_anon_key...</string>
<string name="paystack_public_key">pk_test_...</string>
```

### Staging Environment

**File**: `config.staging.xml`

```xml
<string name="supabase_url">https://staging-project.supabase.co</string>
<string name="supabase_anon_key">staging_anon_key...</string>
<string name="paystack_public_key">pk_test_...</string>
```

### Production Environment

**File**: `config.production.xml`

```xml
<string name="supabase_url">https://prod-project.supabase.co</string>
<string name="supabase_anon_key">prod_anon_key...</string>
<string name="paystack_public_key">pk_live_...</string>
```

---

## 🚨 Troubleshooting

### Error: "Resource not found"

**Cause**: `config.xml` file doesn't exist

**Solution**:
```bash
cp app/src/main/res/values/config.xml.template app/src/main/res/values/config.xml
```

### Error: "Invalid credentials" / "Network error"

**Cause**: Placeholder values not replaced

**Solution**: 
- Open `config.xml`
- Replace ALL three placeholder values
- Sync Gradle and rebuild

### Error: "Paystack initialization failed"

**Cause**: Invalid Paystack public key

**Solution**:
- Verify key starts with `pk_test_` or `pk_live_`
- Check key is copied correctly (no extra spaces)
- Verify key is active in Paystack dashboard

---

## 📝 Configuration Checklist

Before running the app, ensure:

- [ ] `config.xml` file created (copied from template)
- [ ] Supabase URL updated (should start with `https://`)
- [ ] Supabase Anon Key updated (should start with `eyJ`)
- [ ] Paystack Public Key updated (should start with `pk_test_` or `pk_live_`)
- [ ] No placeholder text remains (`YOUR_*`)
- [ ] Gradle synced successfully
- [ ] Project builds without errors

---

## 🔐 Alternative: Environment Variables (Advanced)

For CI/CD or advanced users, you can use environment variables:

### Build Configuration:

**build.gradle.kts**:
```kotlin
android {
    defaultConfig {
        buildConfigField("String", "SUPABASE_URL", "\"${System.getenv("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${System.getenv("SUPABASE_KEY")}\"")
        buildConfigField("String", "PAYSTACK_KEY", "\"${System.getenv("PAYSTACK_KEY")}\"")
    }
}
```

**Set environment variables**:
```bash
export SUPABASE_URL="https://yourproject.supabase.co"
export SUPABASE_KEY="your_key"
export PAYSTACK_KEY="your_key"
```

---

## 📚 Additional Resources

- **Supabase Docs**: https://supabase.com/docs/guides/api
- **Paystack Docs**: https://paystack.com/docs/api/#authentication
- **Android Security**: https://developer.android.com/topic/security/best-practices

---

## ✅ Quick Setup Commands

```bash
# 1. Copy template
cp app/src/main/res/values/config.xml.template app/src/main/res/values/config.xml

# 2. Edit config.xml with your credentials
# (Use your preferred editor)

# 3. Verify .gitignore is working
git status  # config.xml should NOT appear

# 4. Build and run
./gradlew build
```

---

## 🎯 Summary

✅ **Credentials are NOT hardcoded**
✅ **config.xml is in .gitignore**
✅ **Template file is provided**
✅ **Easy to set up for new developers**
✅ **Secure and team-friendly**

Your credentials are now properly managed! 🎉
