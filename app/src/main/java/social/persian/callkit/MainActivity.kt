package social.persian.callkit

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import social.persian.callkit.ui.theme.CallKitTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallKitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        onSimulateCall = { simulateIncomingCall() },
                        onRegisterPhoneAccount = { registerPhoneAccount() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        askNotificationPermission()
        checkOverlayPermission()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("MainActivity", "FCM Token: $token")
        }
    }

    private fun registerPhoneAccount() {
        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val componentName = ComponentName(this, CallConnectionService::class.java)
        val phoneAccountHandle = PhoneAccountHandle(componentName, "PersianCallKit")

        val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "CallKit")
            .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
            .build()

        telecomManager.registerPhoneAccount(phoneAccount)

        // On some devices, user needs to manually enable it in phone settings
        val intent = Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS)
        startActivity(intent)
    }

    private fun simulateIncomingCall() {
        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val componentName = ComponentName(this, CallConnectionService::class.java)
        val phoneAccountHandle = PhoneAccountHandle(componentName, "PersianCallKit")
        
        val extras = Bundle().apply {
            putString("caller_name", "Test Simulator")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
            }
        }

        try {
            telecomManager.addNewIncomingCall(phoneAccountHandle, extras)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error adding incoming call", e)
            // Fallback to direct activity start if telecom fails
            val intent = Intent(this, CallActivity::class.java).apply {
                putExtra("caller_name", "Test Simulator (Fallback)")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }

    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun MainScreen(
    onSimulateCall: () -> Unit,
    onRegisterPhoneAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "CallKit Dashboard")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRegisterPhoneAccount) {
            Text(text = "1. Register Phone Account")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onSimulateCall) {
            Text(text = "2. Simulate Incoming Call")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CallKitTheme {
        MainScreen(onSimulateCall = {}, onRegisterPhoneAccount = {})
    }
}