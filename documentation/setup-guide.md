# Android Studio Setup & Common Issues

## SDK Location Issues

### Error: "SDK location not found"

This happens when `local.properties` doesn't have the SDK path.

### Quick Fix:

**Option 1: Let Android Studio Fix It (Easiest)**
1. Open project in Android Studio
2. File → Project Structure
3. Verify SDK Location shows a path
4. Click "Apply" → "OK"
5. Android Studio will auto-update `local.properties`

**Option 2: Manual Fix**
1. Open `local.properties` in the project root
2. Add this line (adjust path if needed):
   ```properties
   sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk
   ```
   Note the double backslashes (`\\`)!

**Option 3: Find Your SDK Path**

Windows:
```powershell
# Usually at:
C:\Users\YOUR_USERNAME\AppData\Local\Android\Sdk
```

Mac:
```bash
# Usually at:
~/Library/Android/sdk
```

Linux:
```bash
# Usually at:
~/Android/Sdk
```

### Verify SDK Path:

In Android Studio:
1. File → Settings (Windows/Linux) or Preferences (Mac)
2. Appearance & Behavior → System Settings → Android SDK
3. Copy the "Android SDK Location" path
4. Add to `local.properties`:
   ```properties
   sdk.dir=YOUR_PATH_HERE
   ```

---

## Common Build Issues

### 1. "Failed to resolve: ..."

**Solution:**
```bash
# Clean and rebuild
./gradlew clean
./gradlew build --refresh-dependencies
```

In Android Studio:
- Build → Clean Project
- Build → Rebuild Project

### 2. "Gradle sync failed"

**Solution:**
1. File → Invalidate Caches → Invalidate and Restart
2. After restart, sync again

### 3. "Minimum supported Gradle version..."

**Solution:**
Check `gradle/wrapper/gradle-wrapper.properties`:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
```

Update if needed.

### 4. "Unable to find BuildConfig"

**Solution:**
1. Clean project: `./gradlew clean`
2. Sync Gradle
3. Build → Rebuild Project

---

## Deploying to Device

### 1. Enable USB Debugging (Physical Device)

**Android Phone:**
1. Settings → About Phone
2. Tap "Build Number" 7 times
3. Go back → Developer Options
4. Enable "USB Debugging"
5. Connect via USB
6. Allow debugging on popup

### 2. Use Emulator

**Create AVD:**
1. Tools → Device Manager
2. Click "Create Device"
3. Choose device (e.g., Pixel 5)
4. Choose system image (API 24+)
5. Click "Finish"
6. Click "Run" ▶️

### 3. Run Configuration

If app doesn't launch:
1. Run → Edit Configurations
2. Check "Android App" is selected
3. Module: `app`
4. Click "Apply" → "OK"

---

## Gradle Properties

Your `local.properties` should look like this:

```properties
# Android SDK location
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk

# App credentials
supabase.url=https://yourproject.supabase.co
supabase.key=your_key_here
paystack.key=pk_test_your_key
```

**Important:**
- Use double backslashes (`\\`) in Windows paths
- No quotes around values
- No spaces around `=`

---

## Performance Issues

### Slow Build Times

Add to `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
```

### Out of Memory Errors

Increase heap size:
```properties
org.gradle.jvmargs=-Xmx6144m -Dfile.encoding=UTF-8
```

---

## Android Studio Settings

### Recommended Settings:

**1. Enable Auto-Import:**
- Settings → Editor → General → Auto Import
- Check "Add unambiguous imports on the fly"

**2. Disable Instant Run (if issues):**
- Settings → Build, Execution, Deployment → Instant Run
- Uncheck if causing problems

**3. Increase Memory:**
- Help → Edit Custom VM Options
- Add: `-Xmx4096m`

---

## Clean Project Checklist

When things go wrong:

1. **Clean build folders:**
   ```bash
   ./gradlew clean
   rm -rf .gradle build app/build
   ```

2. **Invalidate caches:**
   - File → Invalidate Caches → Invalidate and Restart

3. **Sync Gradle:**
   - File → Sync Project with Gradle Files

4. **Rebuild:**
   - Build → Rebuild Project

5. **Restart Android Studio**

---

## Verification Checklist

Before running the app:

- [ ] `local.properties` has `sdk.dir` path
- [ ] SDK path actually exists on your system
- [ ] `local.properties` has all credentials filled
- [ ] Gradle sync completed without errors
- [ ] Project builds successfully
- [ ] Device/emulator is connected and visible

---

## Quick Fixes

### Reset Everything:

```bash
# Delete build artifacts
./gradlew clean

# Delete Gradle cache
rm -rf .gradle

# Delete build folders
rm -rf build app/build

# In Android Studio:
# File → Invalidate Caches → Invalidate and Restart

# Then:
./gradlew build
```

---

## Still Having Issues?

1. Check `documentation/TROUBLESHOOTING.md`
2. Verify SDK is installed in Android Studio
3. Check Android SDK Manager has required packages
4. Ensure Java 11 is installed
5. Restart computer (yes, really!)

---

## SDK Manager

Required SDK Components:
- ✅ Android SDK Platform 24+ (minimum)
- ✅ Android SDK Platform 34 or 36 (target)
- ✅ Android SDK Build-Tools
- ✅ Android SDK Platform-Tools
- ✅ Android Emulator (if using emulator)

Check: Tools → SDK Manager

---

**Need More Help?**
- Check official docs: https://developer.android.com/studio
- Review `TROUBLESHOOTING.md`
