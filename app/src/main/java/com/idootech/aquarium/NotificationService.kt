package com.idootech.aquarium

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.util.*

class NotificationService : Service() {
    private val ADMIN_CHANNEL_ID = "admin_channel"
    private var factsRef: String? = null

    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var TAG = "Timers"
    var Your_X_SECS = 186400000
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        startTimer()
        factsRef = intent.extras!!["description"].toString()

        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        stoptimertask()
        super.onDestroy()
    }

    //we are going to use a handler to be able to run in our TimerTask
    val handler = Handler()
    fun startTimer() {
        //set a new Timer
        timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask()

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer!!.schedule(timerTask, 86400000, (Your_X_SECS * 100000000).toLong()) //
        //timer.schedule(timerTask, 5000,1000); //
    }

    fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(object : Runnable {
                    override fun run() {

                        //TODO CALL NOTIFICATION FUNC
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        val notificationManager =
                            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        val notificationID = Random().nextInt(3000)

                        /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them. Therefore, confirm if version is Oreo or higher, then setup notification channel
      */if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            setupChannels(notificationManager)
                        }

                        /* intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
*/intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        val requestID = System.currentTimeMillis().toInt()
                        val pendingIntent = PendingIntent.getActivity(
                            applicationContext, requestID, intent,
                            0
                        )
                        val largeIcon = BitmapFactory.decodeResource(
                            resources,
                            R.mipmap.ic_launcher_round
                        )
                        val notificationSoundUri =
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val notificationBuilder = NotificationCompat.Builder(
                            applicationContext, ADMIN_CHANNEL_ID
                        )
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setLargeIcon(largeIcon)
                            .setContentTitle("AQUARIUM: New Tips for the Day")
                            .setContentText(factsRef)
                            .setAutoCancel(true)
                            .setSound(notificationSoundUri)
                            .setContentIntent(pendingIntent)

                        //Set notification color to match your app color template
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            notificationBuilder.color = resources.getColor(R.color.light_blue)
                        }
                        notificationManager.notify(notificationID, notificationBuilder.build())
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    private fun setupChannels(notificationManager: NotificationManager?) {
                        val adminChannelName: CharSequence = "New notification"
                        val adminChannelDescription = "Device to device notification"
                        val adminChannel: NotificationChannel
                        adminChannel = NotificationChannel(
                            ADMIN_CHANNEL_ID,
                            adminChannelName,
                            NotificationManager.IMPORTANCE_HIGH
                        )
                        adminChannel.description = adminChannelDescription
                        adminChannel.enableLights(true)
                        adminChannel.lightColor = Color.RED
                        adminChannel.enableVibration(true)
                        notificationManager?.createNotificationChannel(adminChannel)
                    }
                })
            }
        }
    }
}