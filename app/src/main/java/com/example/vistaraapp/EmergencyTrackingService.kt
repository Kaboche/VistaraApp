package com.example.vistaraapp

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.vistaraapp.api.RetrofitClient
import com.example.vistaraapp.api_requests_responses.TrackingUpdateRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.google.android.gms.location.LocationServices

class EmergencyTrackingService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // Handle the STOP action
        if (intent?.action == "ACTION_STOP_SOS") {
            stopSelf()
            return START_NOT_STICKY
        }

        val token = intent?.getStringExtra("AUTH_TOKEN") ?: ""
        val bookingId = intent?.getStringExtra("BOOKING_ID") ?: ""

        startForeground(101, createNotification())
        startContinuousTracking(token, bookingId)

        return START_STICKY
    }

    private fun startContinuousTracking(token: String, bId: String) {
        lifecycleScope.launch {
            while (isActive) {
                try {
                    val request = TrackingUpdateRequest(
                        latitude = -1.2921,
                        longitude = 36.8219,
                        bookingId = bId,
                        status = "SOS_ACTIVE"
                    )

                    val response = RetrofitClient.bookingInstance.updateTracking("Bearer $token", request)

                    if (response.isSuccessful) {
                        Log.d("TrackingService", "SOS Location update successful")
                    }
                } catch (e: Exception) {
                    Log.e("TrackingService", "Update failed: ${e.message}")
                }
                delay(10000)
            }
        }
    }

    private fun createNotification(): Notification {
        // Create intent that triggers onStartCommand with the STOP action
        val stopIntent = Intent(this, EmergencyTrackingService::class.java).apply {
            action = "ACTION_STOP_SOS"
        }

        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "SOS_CHANNEL")
            .setContentTitle("Emergency Active")
            .setContentText("Vistara is sharing your location with Park Rangers.")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop Tracking", stopPendingIntent)
            .build()
    }
}