# 🔧 Troubleshooting Guide

## Common Build & Setup Issues

### 1. "Unresolved reference: util" or "Unresolved reference: BuildConfig"

**Cause**: Gradle hasn't synced properly or local.properties is missing.

**Solution**:
```bash
# 1. Create local.properties if missing
cp local.properties.template local.properties

# 2. Clean and rebuild
./gradlew clean
./gradlew build

# 3. In Android Studio
File → Invalidate Caches → Invalidate and Restart
```

---

### 2. "Configuration error: Please set up local.properties"

**Cause**: local.properties file is empty or has incorrect format.

**Solution**:
```properties
# Edit local.properties - Make sure format is EXACTLY like this:
supabase.url=https://yourproject.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
paystack.key=pk_test_your_key_here

# NO QUOTES, NO SPACES around =
```

---

### 3. "Supabase URL not configured" Error

**Cause**: Credentials not loaded from local.properties.

**Solution**:
1. Verify `local.properties` exists in **project root** (not in app/ folder)
2. Check file content:
   ```bash
   cat local.properties  # Mac/Linux
   type local.properties  # Windows
   ```
3. Rebuild:
   ```bash
   ./gradlew clean build
   ```

---

### 4. Build Fails with "Cannot find BuildConfig"

**Cause**: BuildConfig feature not enabled.

**Solution**:
Already fixed in `build.gradle.kts`:
```kotlin
buildFeatures {
    buildConfig = true
}
```

If still failing:
```bash
# Clean
./gradlew clean

# Sync
./gradlew --refresh-dependencies

# Build
./gradlew build
```

---

### 5. "Network Error" at Runtime

**Cause**: Invalid Supabase credentials.

**Solution**:
1. Go to https://supabase.com/dashboard
2. Select your project → Settings → API
3. Copy EXACT values:
   - **URL**: Must start with `https://`
   - **Anon key**: Long string starting with `eyJ`
4. Update local.properties
5. Rebuild app

---

### 6. "Payment Failed" Error

**Cause**: Invalid Paystack key.

**Solution**:
1. Go to https://dashboard.paystack.com
2. Settings → API Keys & Webhooks
3. Copy **Public Key**: starts with `pk_test_` (test mode)
4. Update local.properties:
   ```properties
   paystack.key=pk_test_your_actual_key
   ```
5. Rebuild app

---

### 7. Gradle Sync Keeps Failing

**Full Reset Solution**:
```bash
# 1. Delete build folders
rm -rf build app/build .gradle

# 2. Delete Gradle cache (optional)
rm -rf ~/.gradle/caches

# 3. Invalidate Android Studio caches
# File → Invalidate Caches → Invalidate and Restart

# 4. Sync again
./gradlew clean build
```

---

### 8. "Module not specified" or "No module" Error

**Solution**:
1. File → Project Structure
2. Check SDK location is set
3. Ensure compileSdk = 36 or lower (based on your SDK)
4. Click "Apply" → "OK"
5. Sync Gradle

---

### 9. Dependencies Download Fails

**Cause**: Network issues or repository problems.

**Solution**:
Add to `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then:
```bash
./gradlew build --refresh-dependencies
```

---

### 10. App Crashes on Startup

**Check logcat for**:
- `IllegalStateException: Supabase URL not configured`
- `NullPointerException` in SupabaseClient

**Solution**:
1. Verify local.properties has valid credentials
2. Check BuildConfig is generated:
   - Look in `app/build/generated/source/buildConfig/debug/`
3. Clean and rebuild:
   ```bash
   ./gradlew clean build
   ```

---

## Verification Checklist

Before running the app, verify:

- [ ] `local.properties` file exists in project root
- [ ] File has all three properties filled
- [ ] Properties have NO quotes or extra spaces
- [ ] Supabase URL starts with `https://`
- [ ] Supabase key starts with `eyJ`
- [ ] Paystack key starts with `pk_test_` or `pk_live_`
- [ ] Gradle sync completed successfully
- [ ] Project builds without errors
- [ ] No red underlines in code

---

## Still Having Issues?

### Check BuildConfig Generation:

1. Build the project
2. Navigate to: `app/build/generated/source/buildConfig/debug/com/example/coffeecafe/`
3. Open `BuildConfig.java`
4. Verify these fields exist:
   ```java
   public static final String SUPABASE_URL = "your_url";
   public static final String SUPABASE_KEY = "your_key";
   public static final String PAYSTACK_KEY = "your_key";
   ```

If empty strings, check local.properties again.

---

## Quick Fix Commands

```bash
# Complete reset and rebuild
./gradlew clean
rm -rf .gradle build app/build
./gradlew build

# Force dependency refresh
./gradlew build --refresh-dependencies

# Check Gradle configuration
./gradlew properties

# Verify local.properties
cat local.properties  # Should show your credentials
```

---

## Contact & Support

If issues persist:
1. Check `SETUP_GUIDE.md`
2. Review `DEPLOYMENT_GUIDE.md`
3. Verify database setup in `SUPABASE_SETUP.md`

---

**Most Common Fix**: 
```bash
cp local.properties.template local.properties
# Edit local.properties with your credentials
./gradlew clean build
```
