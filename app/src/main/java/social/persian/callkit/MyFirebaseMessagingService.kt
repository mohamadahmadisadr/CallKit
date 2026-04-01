package social.persian.callkit

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Data payload: ${remoteMessage.data}")

        if (remoteMessage.data.isNotEmpty()) {
            val callerName = remoteMessage.data["caller_name"] ?: "Unknown Caller"
            val type = remoteMessage.data["type"]
            
            if (type == "incoming_call") {
                // 1. Try Telecom (Professional way)
                sendTelecomCall(callerName)
                
                // 2. Show High-Priority Full-Screen Notification (Required for Killed State)
                showFullScreenNotification(callerName)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    @SuppressLint("FullScreenIntentPolicy")
    private fun showFullScreenNotification(callerName: String) {
        val channelId = "incoming_calls"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create High Importance Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Incoming Calls",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent incoming call notifications"
                enableLights(true)
                enableVibration(true)
                setSound(null, null) // Use system default or custom
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create the intent for the CallActivity
        val intent = Intent(this, CallActivity::class.java).apply {
            putExtra("caller_name", callerName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Incoming Call")
            .setContentText("$callerName is calling")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(pendingIntent, true) // THE MAGIC LINE
            .setAutoCancel(true)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        Log.d(TAG, "Posting full-screen notification for $callerName")
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun sendTelecomCall(callerName: String) {
        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val componentName = ComponentName(this, CallConnectionService::class.java)
        val phoneAccountHandle = PhoneAccountHandle(componentName, "PersianCallKit")
        
        val extras = Bundle().apply {
            putString("caller_name", callerName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
            }
        }

        try {
            telecomManager.addNewIncomingCall(phoneAccountHandle, extras)
        } catch (e: Exception) {
            Log.e(TAG, "Telecom failed: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val NOTIFICATION_ID = 202
    }
}
