# CallKit for Android 📞

Hey there! This is a simple Android project designed to help you integrate native-like calling features into your app. It uses the modern Android Telecom framework to make your VoIP calls feel just like regular phone calls.

## What's inside?

- **Native Call Integration**: Uses `TelecomManager` so your calls show up in the system's phone app and lock screen.
- **Firebase Ready**: Pre-configured with FCM for handling incoming call notifications.
- **Compose UI**: A clean, modern dashboard built with Jetpack Compose.
- **Reusable Library**: Most of the heavy lifting is tucked away in the `callkit-lib` module, making it easier to drop into your own projects.

## Getting Started

1. **Add your config**: Since this project uses Firebase, you'll need to drop your own `google-services.json` into the `app/` folder. (Don't worry, it's ignored by Git!)
2. **Permissions**: The app will ask for Notification and "Display over other apps" permissions to make sure calls can pop up even when you're busy with something else.
3. **Registration**: Hit the "Register Phone Account" button first—this tells Android that your app is allowed to handle calls.
4. **Test it out**: Use the "Simulate Incoming Call" button to see it in action!

## How to use it in your code

The library is designed to be super simple. Here's a quick example of how to initialize it in your `MainActivity` or `Application` class:

```kotlin
// 1. Initialize CallKit to listen for call events
CallKit.init(object : CallKit.CallKitListener {
    override fun onTokenRefreshed(token: String) {
        // Send this token to your server to target this device for calls
        println("FCM Token: $token")
    }

    override fun onCallAccepted(callerName: String) {
        // User picked up! Navigate to your active call screen
        println("Call accepted from: $callerName")
    }

    override fun onCallDeclined(callerName: String) {
        // User hung up or declined
        println("Call declined")
    }

    override fun onCallMissed(callerName: String) {
        // Handle missed call UI
        println("Missed call from: $callerName")
    }
})
```

That's it! The library handles the system-level `ConnectionService` and Firebase messaging in the background.

Happy coding! 🚀
