package com.application.nodawallet.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.utils.Constants
import com.application.nodawallet.utils.UtilsDefault
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

open class FirebaseMessagingService : FirebaseMessagingService(){

    var deviceToken: String = ""
    var title: String = ""
    var message:String = ""
    var notificationManager: NotificationManager? = null
    var typeID: String? = null
    var intent: Intent? = null
    var TAG = "FirebaseMessaging"
    var imageUri = ""
    var type = ""
    var orderid: String? = ""
    var productid = ""
    var bitmap: Bitmap? = null


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            deviceToken = instanceIdResult.token
            Log.i("fcmtoken", deviceToken)
            UtilsDefault.updateSharedPreferenceFCM(Constants.FCM_KEY, deviceToken)
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage!!.data !=null){
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        if (remoteMessage.notification !=null){
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
        }


        if (remoteMessage != null) {

            message = remoteMessage.notification!!.body!!
            title = remoteMessage.notification!!.title!!


        }

        try {

            if (UtilsDefault.getSharedPreferenceString(Constants.PUSHNOTIFICATION) == "enable"){
                sendNotification(message,title)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun sendNotification(message: String, title: String) {

        val channelId = "NODAWALLET_CHANNEL_ID"
        val channelName = "NODAWALLET"


        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("secondNotification", 1)
        intent.setAction("dummy_unique_action_identifyer" + "dummy")
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.noda_circle))
            .setSmallIcon(R.mipmap.noda_circle)
            .setContentTitle(UtilsDefault.checkNull(title))
            .setContentText(UtilsDefault.checkNull(message))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            // .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            //.setColor(ContextCompat.getColor(this,R.color.colorAccent))
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName,
                NotificationManager.IMPORTANCE_HIGH)
            channel.description = message
            notificationManager.createNotificationChannel(channel)
            notificationBuilder.setChannelId(channelId)
        }
        val notificationID = (Date().time / 1000L % Integer.MAX_VALUE).toInt()
        notificationManager.notify(notificationID, notificationBuilder.build())

    }

    fun getBitmapfromUrl(imageUrl: String): Bitmap? {
        try {
            val url = URL("" + imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)

        } catch (e: Exception) {
            e.printStackTrace()
            return null

        }

    }

}