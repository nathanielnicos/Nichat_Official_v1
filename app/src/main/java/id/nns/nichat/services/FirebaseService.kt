package id.nns.nichat.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import id.nns.nichat.R
import id.nns.nichat.ui.home.HomeActivity
import id.nns.nichat.utils.Constants.CHANNEL_ID
import id.nns.nichat.utils.Constants.CHANNEL_NAME
import id.nns.nichat.utils.Constants.FIREBASE_STORAGE_URL
import id.nns.nichat.utils.firebase_utils.FirestoreUtil
import kotlin.random.Random

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        FirestoreUtil.updateToken(p0)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val intent = Intent(this, HomeActivity::class.java)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent
            .getActivity(
                this,
                0,
                intent,
                FLAG_ONE_SHOT
            )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(p0.data["sender"])
            .setContentText(
                if (p0.data["text"]?.contains(FIREBASE_STORAGE_URL) == true) {
                    "Image"
                } else {
                    p0.data["text"]
                }
            )
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }

}
