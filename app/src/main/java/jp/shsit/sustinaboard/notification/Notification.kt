package jp.shsit.sustinaboard.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.MainActivity

class Notification(_str: String, _id: Int) {
    @SuppressLint("MissingPermission")
    var str:String="mes"
    var id:Int=1
    init{
        str=_str
        id=_id
    }
    fun sendNotification(context: Context) {
        val channelId = "jp.shsit.sustinaboard"
        val channelName = "sustinaboard"
        val channelDescription = "This is a sustinaboard's notification"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE)

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_launcher)
            setContentText(str + "の賞味期限、または消費期限が近づいています。")
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

        //val id = 0
        //NotificationManagerCompat.from(context).notify(id, builder.build())
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(id, builder.build())
        }
    }
}