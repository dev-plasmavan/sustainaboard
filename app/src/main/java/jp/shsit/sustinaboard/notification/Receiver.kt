package jp.shsit.sustinaboard.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        if(context != null) {
            val str = p1!!.getStringExtra("mes").toString()
            val id = p1.getIntExtra("id",1)

            val myNotification = Notification(str,id)
            myNotification.sendNotification(context)
        }
    }
}