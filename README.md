[![](https://jitpack.io/v/mohamadahmadisadr/CallKit.svg)](https://jitpack.io/#mohamadahmadisadr/CallKit)
# CallKit for Android 📞

Hey there! This is a simple Android project designed to help you integrate native-like calling features into your app. It uses the modern Android Telecom framework to make your VoIP calls feel just like regular phone calls.

## What's inside?

- **Native Call Integration**: Uses `TelecomManager` so your calls show up in the system's phone app and lock screen.
- **Firebase Ready**: Pre-configured with FCM for handling incoming call notifications.
- **Compose UI**: A clean, modern dashboard built with Jetpack Compose.
- **Reusable Library**: Most of the heavy lifting is tucked away in the `callkit-lib` module.

## GitHub Repository
Find the source code and contribute at: [https://github.com/mohamadahmadisadr/CallKit](https://github.com/mohamadahmadisadr/CallKit)

## Setup Instructions (Before Implementing)

### 1. Firebase Configuration
*   **google-services.json**: Create a project in the [Firebase Console](https://console.firebase.google.com/), add an Android app, and download the `google-services.json` file. Place it in your `app/` directory.
*   **Plugin**: Ensure the Google Services plugin is added to your root `build.gradle.kts` and applied in your app-level `build.gradle.kts`.

### 2. Required Permissions
Add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.MANAGE_OWN_CALLS" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

### 3. Service Declaration
Ensure your `AndroidManifest.xml` includes the `ConnectionService`:

```xml
<service
    android:name="social.persian.callkit_lib.CallConnectionService"
    android:permission="android.permission.BIND_TELECOM_CONNECTION_SERVICE"
    android:exported="true">
    <intent-filter>
        <action android:name="android.telecom.ConnectionService" />
    </intent-filter>
</service>
```

## How to use it in your code

### 1. Dependency
Add JitPack to your root `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then add the library to your app's `build.gradle.kts`:
```kotlin
dependencies {
    implementation("com.github.mohamadahmadisadr:CallKit:v1.0.0")
}
```

### 2. Initialization
Initialize `CallKit` in your `MainActivity` or `Application` class:

```kotlin
CallKit.init(object : CallKit.CallKitListener {
    override fun onTokenRefreshed(token: String) {
        // Send this token to your server
    }

    override fun onCallAccepted(callerName: String) {
        // Handle call acceptance
    }

    override fun onCallDeclined(callerName: String) {
        // Handle call decline
    }

    override fun onCallMissed(callerName: String) {
        // Handle missed call
    }
})
```

Happy coding! 🚀
