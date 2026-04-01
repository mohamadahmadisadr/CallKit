package social.persian.callkit_lib

import android.content.Context
import android.util.Log

object CallKit {
    private var listener: CallKitListener? = null

    interface CallKitListener {
        fun onTokenRefreshed(token: String)
        fun onCallAccepted(callerName: String)
        fun onCallDeclined(callerName: String)
        fun onCallMissed(callerName: String)
    }

    fun init(listener: CallKitListener) {
        this.listener = listener
    }

    internal fun notifyTokenRefreshed(token: String) {
        listener?.onTokenRefreshed(token)
    }

    internal fun notifyCallAccepted(callerName: String) {
        listener?.onCallAccepted(callerName)
    }

    internal fun notifyCallDeclined(callerName: String) {
        listener?.onCallDeclined(callerName)
    }

    internal fun notifyCallMissed(callerName: String) {
        listener?.onCallMissed(callerName)
    }
}
