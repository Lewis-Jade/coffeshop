# 🚀 Quick Setup Guide for Team Collaboration

## For Anyone Forking/Cloning This Project

This project uses a secure credential management system that allows **anyone to use their own credentials** without seeing others' credentials or committing them to Git.

---

## 📋 Setup Steps (2 minutes)

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/sibby-killer/coffeshop.git
cd coffeshop
```

### 2️⃣ Create Your Local Configuration
```bash
# Copy the template
cp local.properties.template local.properties
```

### 3️⃣ Get Your Credentials

#### **Supabase** (Free - https://supabase.com)
1. Create account and new project
2. Go to Settings → API
3. Copy:
   - **Project URL**: `https://xxxxx.supabase.co`
   - **Anon Key**: `eyJhbGc...`

#### **Paystack** (Free test mode - https://paystack.com)
1. Create account
2. Go to Settings → API Keys
3. Copy **Test Public Key**: `pk_test_...`

### 4️⃣ Update local.properties

Open `local.properties` and add your credentials:

```properties
# Replace with YOUR credentials
supabase.url=https://yourproject.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
paystack.key=pk_test_your_test_key_here
```

### 5️⃣ Set Up Database

1. Go to your Supabase project → SQL Editor
2. Copy SQL from `SUPABASE_SETUP.md`
3. Run the SQL script
4. Verify tables are created

### 6️⃣ Build & Run
```bash
# Sync Gradle
./gradlew build

# Run in Android Studio
# Or: ./gradlew installDebug
```

---

## ✅ Why This Works for Teams

| Feature | Benefit |
|---------|---------|
| **local.properties** | Never committed (in .gitignore) |
| **Template provided** | Easy setup for new team members |
| **BuildConfig** | Credentials injected at build time |
| **No hardcoding** | Zero credentials in source code |
| **Fork-friendly** | Everyone uses their own credentials |

---

## 🔒 Security Features

✅ **Your credentials stay private** - Never leave your machine
✅ **No shared credentials** - Each developer uses their own
✅ **No accidental commits** - local.properties is in .gitignore
✅ **Clean collaboration** - No conflicts with team credentials

---

## 🎯 What Gets Committed vs What Doesn't

### ✅ Committed to Git (Safe)
- `local.properties.template` - Template file
- All source code
- Documentation
- `.gitignore` with security rules

### ❌ NOT Committed (Private)
- `local.properties` - YOUR credentials
- `build/` folders
- IDE files

---

## 🆘 Troubleshooting

### "BuildConfig not found"
```bash
# Solution: Sync Gradle
./gradlew build --refresh-dependencies
```

### "Empty credentials"
Check that:
1. `local.properties` exists in root folder
2. All three properties are filled
3. No typos in property names
4. Values don't have quotes around them

### "Supabase connection failed"
1. Verify URL starts with `https://`
2. Check anon key is complete (starts with `eyJ`)
3. Ensure Supabase project is active

---

## 📁 Project Structure

```
coffeshop/
├── local.properties.template  ✅ Template (in Git)
├── local.properties          ❌ Your credentials (NOT in Git)
├── app/
│   ├── build.gradle.kts      # Loads from local.properties
│   └── src/
│       └── main/java/.../
│           ├── utils/Constants.java  # Uses BuildConfig
│           └── config/SupabaseClient.java
└── SETUP_GUIDE.md  👈 You are here
```

---

## 🎓 How It Works

```
local.properties (your credentials)
        ↓
app/build.gradle.kts (reads properties)
        ↓
BuildConfig.java (generated at build time)
        ↓
Constants.java (provides to app)
        ↓
App uses credentials at runtime
```

---

## 👥 Team Workflow

### New Team Member Joins:
1. Clone repo
2. Copy template: `cp local.properties.template local.properties`
3. Add their own credentials
4. Build and run

### Updating Code:
```bash
git pull origin main
# local.properties unchanged (not in repo)
./gradlew build
```

### Pushing Changes:
```bash
git add .
git commit -m "Your changes"
git push
# local.properties automatically excluded
```

---

## 🌟 Best Practices

### ✅ DO:
- Keep `local.properties` private
- Use test credentials during development
- Document where to get credentials
- Help team members with setup

### ❌ DON'T:
- Commit `local.properties`
- Share credentials via chat/email
- Use production credentials in development
- Hardcode any credentials

---

## 🚀 Production Deployment

For production builds, use environment variables or CI/CD secrets:

```bash
# Set environment variables
export SUPABASE_URL="prod_url"
export SUPABASE_KEY="prod_key"
export PAYSTACK_KEY="pk_live_..."

# Build release
./gradlew assembleRelease
```

---

## 📚 Additional Documentation

- **README.md** - Project overview
- **SUPABASE_SETUP.md** - Database setup
- **DEPLOYMENT_GUIDE.md** - Full deployment guide
- **CREDENTIAL_SETUP.md** - Detailed credential info

---

## ✅ Setup Checklist

- [ ] Repository cloned
- [ ] `local.properties` created from template
- [ ] Supabase account created
- [ ] Supabase credentials added to local.properties
- [ ] Paystack account created
- [ ] Paystack key added to local.properties
- [ ] Database tables created (SQL from SUPABASE_SETUP.md)
- [ ] Gradle synced successfully
- [ ] App builds without errors
- [ ] App runs and connects to your Supabase

---

**🎉 You're ready to develop! Your credentials are secure and private.**

**Questions?** Check `CREDENTIAL_SETUP.md` or other documentation files.
