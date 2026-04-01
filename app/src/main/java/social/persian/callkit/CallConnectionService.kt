package social.persian.callkit

import android.content.Intent
import android.os.Build
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log

class CallConnectionService : ConnectionService() {

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        Log.d("CallConnectionService", "onCreateIncomingConnection called")
        
        val callerName = request?.extras?.getString("caller_name") ?: "Unknown"
        val connection = CallConnection(this, callerName)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            connection.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        }
        
        connection.setAddress(request?.address, TelecomManager.PRESENTATION_ALLOWED)
        connection.setCallerDisplayName(callerName, TelecomManager.PRESENTATION_ALLOWED)
        connection.setRinging()
        
        Log.d("CallConnectionService", "Starting CallActivity for $callerName")
        val intent = Intent(this, CallActivity::class.java).apply {
            putExtra("caller_name", callerName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        startActivity(intent)
        
        return connection
    }

    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.e("CallConnectionService", "onCreateIncomingConnectionFailed")
    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        Log.d("CallConnectionService", "onCreateOutgoingConnection")
        val connection = CallConnection(this, "Outgoing")
        connection.setAddress(request?.address, TelecomManager.PRESENTATION_ALLOWED)
        return connection
    }

    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.e("CallConnectionService", "onCreateOutgoingConnectionFailed")
    }
}

class CallConnection(private val context: android.content.Context, private val callerName: String) : Connection() {

    override fun onAnswer() {
        super.onAnswer()
        Log.d("CallConnection", "onAnswer")
        setActive()
        
        val intent = Intent(context, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            putExtra("action", "answer")
            putExtra("caller_name", callerName)
        }
        context.startActivity(intent)
    }

    override fun onReject() {
        super.onReject()
        Log.d("CallConnection", "onReject")
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
    }

    override fun onDisconnect() {
        super.onDisconnect()
        Log.d("CallConnection", "onDisconnect")
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }
}
