# hideX - App Lock & Protection

A modern, minimalist Android application that protects your apps with a password overlay. Built with Kotlin, Jetpack Compose, and Material Design 3.

## Features

- 🔒 **Password Protection**: Secure your apps with a master password
- 👆 **Biometric Authentication**: Use fingerprint/face unlock as an alternative to password
- 🎨 **Minimalist Design**: Clean black and white UI with Material Design 3
- 📱 **Accessibility Service**: Monitors app launches in the background
- 🔐 **Encrypted Storage**: Passwords stored securely using EncryptedSharedPreferences
- 🏗️ **MVVM Architecture**: Clean, maintainable code structure
- 🗄️ **Room Database**: Efficient local storage for protected apps

## Screenshots

*(Add screenshots here after building the app)*

## Requirements

- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Kotlin**: 1.9.20
- **Gradle**: 8.2.0

## Project Structure

```
hideX_app/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/hidex/app/
│   │       │   ├── data/
│   │       │   │   ├── database/
│   │       │   │   │   ├── HideXDatabase.kt
│   │       │   │   │   ├── ProtectedAppDao.kt
│   │       │   │   │   └── SecureStorageManager.kt
│   │       │   │   ├── model/
│   │       │   │   │   ├── ProtectedApp.kt
│   │       │   │   │   └── InstalledApp.kt
│   │       │   │   └── repository/
│   │       │   │       └── AppRepositoryImpl.kt
│   │       │   ├── domain/
│   │       │   │   └── repository/
│   │       │   │       └── AppRepository.kt
│   │       │   ├── service/
│   │       │   │   └── AppProtectionAccessibilityService.kt
│   │       │   ├── ui/
│   │       │   │   ├── screens/
│   │       │   │   │   ├── FirstLaunchScreen.kt
│   │       │   │   │   ├── MainScreen.kt
│   │       │   │   │   ├── AppSelectionScreen.kt
│   │       │   │   │   ├── SettingsScreen.kt
│   │       │   │   │   └── PasswordOverlayActivity.kt
│   │       │   │   ├── viewmodels/
│   │       │   │   │   ├── FirstLaunchViewModel.kt
│   │       │   │   │   ├── MainViewModel.kt
│   │       │   │   │   ├── AppSelectionViewModel.kt
│   │       │   │   │   └── SettingsViewModel.kt
│   │       │   │   ├── navigation/
│   │       │   │   │   └── NavGraph.kt
│   │       │   │   └── theme/
│   │       │   │       ├── Color.kt
│   │       │   │       ├── Theme.kt
│   │       │   │       └── Type.kt
│   │       │   ├── MainActivity.kt
│   │       │   └── HideXApplication.kt
│   │       ├── res/
│   │       │   ├── values/
│   │       │   │   ├── strings.xml
│   │       │   │   └── themes.xml
│   │       │   └── xml/
│   │       │       ├── accessibility_service_config.xml
│   │       │       ├── backup_rules.xml
│   │       │       └── data_extraction_rules.xml
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## Setup Instructions

### 1. Clone or Open the Project

Open the project in Android Studio:
```bash
cd /home/ubuntu/hideX_app
```

Then open this folder in Android Studio.

### 2. Sync Gradle

Android Studio should automatically sync Gradle. If not, click on:
```
File → Sync Project with Gradle Files
```

### 3. Build the Project

Build the project to ensure everything compiles:
```
Build → Make Project
```

### 4. Run on Device/Emulator

1. Connect an Android device with USB debugging enabled, or start an Android emulator
2. Click the "Run" button in Android Studio or press `Shift + F10`

### 5. Enable Accessibility Service (REQUIRED)

**This is the most important step for the app to work!**

After installing the app:

1. Open the hideX app
2. Create a password on the first launch
3. You'll see a warning that the Accessibility Service is disabled
4. Tap on the warning or go to Settings
5. Navigate to: **Settings → Accessibility → hideX App Protection**
6. Toggle the service ON
7. Accept the permission prompt

**Why is this needed?**
The Accessibility Service allows hideX to monitor when protected apps are launched so it can show the password overlay. Without this permission, the app cannot protect other apps.

### 6. Grant Overlay Permission (Usually Auto-Granted)

The app also needs permission to display over other apps. This is usually granted automatically, but if not:

1. Go to: **Settings → Apps → hideX → Advanced → Display over other apps**
2. Enable the permission

## Usage Guide

### First Time Setup

1. **Create Password**: On first launch, you'll be asked to create a master password
2. **Enable Accessibility Service**: Follow the instructions above
3. **Add Apps to Protect**: Tap the "+" button on the main screen
4. **Select Apps**: Choose which apps you want to protect from the list
5. **Save**: Tap the checkmark to save your selection

### Daily Usage

1. **Launch Protected App**: When you try to open a protected app, a password overlay will appear
2. **Enter Password**: Type your master password or use biometric authentication (if enabled)
3. **Unlock**: The app will open after successful authentication
4. **Manage Protected Apps**: Add or remove apps from protection at any time

### Settings

- **Change Password**: Update your master password
- **Enable Biometric**: Use fingerprint/face unlock instead of typing password
- **Accessibility Service**: Check status and enable if disabled

## Architecture

### MVVM (Model-View-ViewModel)

- **Model**: Data layer with Room database, repositories, and secure storage
- **View**: Jetpack Compose UI screens
- **ViewModel**: Business logic and state management

### Key Components

#### 1. Accessibility Service
**File**: `AppProtectionAccessibilityService.kt`

Monitors app launches in the background:
- Listens to window state change events
- Identifies which app is being opened
- Checks if the app is protected
- Triggers password overlay if needed

#### 2. Password Overlay
**File**: `PasswordOverlayActivity.kt`

Full-screen activity that blocks access to protected apps:
- Transparent theme for seamless appearance
- Prevents back button dismissal
- Supports password and biometric authentication
- Returns to home screen on cancel

#### 3. Secure Storage
**File**: `SecureStorageManager.kt`

Handles encrypted storage of sensitive data:
- Uses `EncryptedSharedPreferences`
- AES256 encryption
- Master key stored in Android Keystore
- Hashed password storage

#### 4. Room Database
**Files**: `HideXDatabase.kt`, `ProtectedAppDao.kt`, `ProtectedApp.kt`

Local database for protected apps:
- Reactive Flow-based queries
- Efficient CRUD operations
- Offline-first architecture

## Security Considerations

### 1. Password Storage
- Passwords are hashed before storage
- Stored in EncryptedSharedPreferences with AES256 encryption
- Master key protected by Android Keystore
- **Note**: For production use, consider stronger hashing algorithms like BCrypt or Argon2

### 2. Accessibility Service
- Only monitors window state changes
- Does not read app content or user data
- Only checks package names against protected list
- Minimal battery impact

### 3. Backup Exclusions
- Secure preferences are excluded from cloud backups
- Prevents password leakage through backups

### 4. Permissions
- **Accessibility Service**: Required to monitor app launches
- **Display over other apps**: Required to show password overlay
- **Query all packages**: Required to list installed apps

## Troubleshooting

### App Protection Not Working

**Problem**: Protected apps open without password prompt

**Solutions**:
1. Verify Accessibility Service is enabled: **Settings → Accessibility → hideX App Protection**
2. Restart the app after enabling Accessibility Service
3. Check that the app is in the protected list
4. Reboot the device if issues persist

### Password Overlay Not Appearing

**Problem**: Password screen doesn't show

**Solutions**:
1. Grant "Display over other apps" permission
2. Check Accessibility Service is running
3. Verify the app package name is in the database

### Biometric Not Working

**Problem**: Fingerprint option doesn't appear

**Solutions**:
1. Ensure device has biometric hardware
2. Set up fingerprint/face unlock in device settings
3. Enable biometric in hideX settings

### App Crashes on Launch

**Solutions**:
1. Clear app data: **Settings → Apps → hideX → Storage → Clear Data**
2. Reinstall the app
3. Check Android Studio Logcat for error details

## Building for Production

### 1. Update Version

Edit `app/build.gradle.kts`:
```kotlin
versionCode = 2
versionName = "1.1.0"
```

### 2. Create Keystore

```bash
keytool -genkey -v -keystore hidex.keystore -alias hidex -keyalg RSA -keysize 2048 -validity 10000
```

### 3. Configure Signing

Add to `app/build.gradle.kts`:
```kotlin
signingConfigs {
    release {
        storeFile = file("../hidex.keystore")
        storePassword = "your_password"
        keyAlias = "hidex"
        keyPassword = "your_password"
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.release
        isMinifyEnabled = true
        proguardFiles(...)
    }
}
```

### 4. Build Release APK

```bash
./gradlew assembleRelease
```

APK will be at: `app/build/outputs/apk/release/app-release.apk`

## Known Limitations

1. **System Apps**: Cannot protect system apps without root access
2. **Root Detection**: App does not detect or prevent tampering on rooted devices
3. **Task Manager**: Users can force-close protected apps from system task manager
4. **Password Reset**: No password recovery mechanism (by design for security)

## Future Enhancements

- [ ] Pattern lock option
- [ ] Multiple password profiles
- [ ] App usage statistics
- [ ] Fake crash screen option
- [ ] Intruder selfie feature
- [ ] Cloud backup for protected app list
- [ ] Dark/Light theme toggle
- [ ] Custom theme colors

## Technologies Used

- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material Design 3** - Design system
- **Room Database** - Local database
- **Coroutines & Flow** - Asynchronous programming
- **EncryptedSharedPreferences** - Secure storage
- **Biometric API** - Fingerprint/face authentication
- **Accessibility Service** - Background app monitoring
- **Navigation Component** - Screen navigation

## License

This project is created for educational and personal use.

## Support

For issues or questions:
1. Check the Troubleshooting section above
2. Review Android Studio Logcat for error details
3. Ensure all permissions are granted

## Contributing

To contribute:
1. Follow MVVM architecture patterns
2. Use Kotlin coding conventions
3. Add comments for complex logic
4. Test on multiple Android versions
5. Update README with new features

---

**Made with ❤️ using Kotlin and Jetpack Compose**
