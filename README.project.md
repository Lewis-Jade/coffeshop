# 🚀 Quick Start Guide

## For New Developers

Welcome to the MMUST Mobile Coffee Shop project! Follow these steps to get started:

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/sibby-killer/coffeshop.git
cd coffeshop
```

### 2️⃣ Set Up Credentials
```bash
# Copy the configuration template
cp app/src/main/res/values/config.xml.template app/src/main/res/values/config.xml
```

Then edit `app/src/main/res/values/config.xml` with your actual credentials.

**Get credentials from:**
- **Supabase**: https://supabase.com/dashboard (Project Settings → API)
- **Paystack**: https://dashboard.paystack.com (Settings → API Keys)

📖 **Detailed instructions**: See `CREDENTIAL_SETUP.md`

### 3️⃣ Open in Android Studio
1. Open Android Studio
2. File → Open → Select `coffeshop` folder
3. Wait for Gradle sync to complete

### 4️⃣ Build and Run
- Connect Android device or start emulator
- Click Run (▶️) or press `Shift + F10`

---

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| `README.md` | Original project requirements |
| `README_IMPLEMENTATION.md` | Technical implementation details |
| `CREDENTIAL_SETUP.md` | **⭐ Start here for setup** |
| `DEPLOYMENT_GUIDE.md` | Complete deployment instructions |
| `SUPABASE_SETUP.md` | Database schema and setup |
| `PROJECT_SUMMARY.md` | Project overview and metrics |

---

## ⚡ Quick Setup Checklist

- [ ] Clone repository
- [ ] Copy `config.xml.template` → `config.xml`
- [ ] Get Supabase credentials
- [ ] Get Paystack credentials
- [ ] Update `config.xml`
- [ ] Open project in Android Studio
- [ ] Sync Gradle
- [ ] Run app

---

## 🆘 Need Help?

1. **Can't build?** → Check `DEPLOYMENT_GUIDE.md`
2. **Credential issues?** → See `CREDENTIAL_SETUP.md`
3. **Database setup?** → Follow `SUPABASE_SETUP.md`

---

## 🔒 Security Note

✅ Credentials are stored in `config.xml` (NOT committed to Git)
✅ Never commit `config.xml` - it's in `.gitignore`
✅ Use template file for reference

---

**Ready to code! 🎉**
