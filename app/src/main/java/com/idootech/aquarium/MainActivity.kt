package com.idootech.aquarium

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        onNewIntent(intent)

        val slideAnimation4 = AnimationUtils.loadAnimation(this, R.anim.left_slide)
        val dropbottom: ImageView = findViewById(R.id.fish_load)
        dropbottom.startAnimation(slideAnimation4)

        val slideAnimation5 = AnimationUtils.loadAnimation(this, R.anim.right_slide)
        val droptop: ImageView = findViewById(R.id.crab_load)
        droptop.startAnimation(slideAnimation5)

        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.fade)
        val appname: TextView = findViewById(R.id.app_name)
        appname.startAnimation(slideAnimation)


    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE

                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onStart() {
        super.onStart()
        stopService(Intent(this, NotificationService::class.java))

    }

    override fun onResume() {
        super.onResume()
        stopService(Intent(this, NotificationService::class.java))
    }

    override fun onStop() {
        super.onStop()

        val intent = Intent(this, NotificationService::class.java)
        intent.putExtra("description", nextdescription)
        startService(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("title")) {
                val notificationtitle = extras.getString("title")
                if (notificationtitle!!.contains("Update")) {
                    val background: Thread = object : Thread() {
                        override fun run() {
                            try {
                                val intent2 = Intent(Intent.ACTION_VIEW)
                                intent2.data = Uri.parse("http://www.idootech.com.ng/aquarium")
                                try {
                                    startActivity(intent2)
                                } catch (e: Exception) {
                                    intent2.data = Uri.parse("http://www.idootech.com.ng/aquarium")
                                }
                                // Thread will sleep for 5 seconds
                                sleep((5 * 1000).toLong())

                                // After 5 seconds redirect to another intent


                                //Remove activity
                                finish()
                            } catch (e: Exception) {
                            }
                        }
                    }
                    // start thread
                    background.start()
                    val i = Intent(baseContext, MainActivity2::class.java)
                    startActivity(i)


                } else if (notificationtitle.contains("Play")) {
                    val intent2 = Intent(this@MainActivity, HtmlConnect::class.java)

                    startActivity(intent)
                    finish()
                } else if (notificationtitle.contains("Facts")) {
                    val intent2 = Intent(this@MainActivity, MainActivity2::class.java)

                    startActivity(intent)
                    finish()


                } else {
                    val background: Thread = object : Thread() {
                        override fun run() {
                            try {
                                // Thread will sleep for 5 seconds
                                sleep((2 * 1000).toLong())

                                // After 5 seconds redirect to another intent
                                val i = Intent(baseContext, MainActivity2::class.java)
                                startActivity(i)

                                //Remove activity
                                finish()
                            } catch (e: Exception) {
                            }
                        }
                    }
                    // start thread
                    background.start()
                }
            } else {
                /****** Create Thread that will sleep for 5 seconds */
                val background: Thread = object : Thread() {
                    override fun run() {
                        try {
                            // Thread will sleep for 5 seconds
                            sleep((2 * 1000).toLong())

                            // After 5 seconds redirect to another intent
                            val i = Intent(baseContext, MainActivity2::class.java)
                            startActivity(i)

                            //Remove activity
                            finish()
                        } catch (e: Exception) {
                        }
                    }
                }
                // start thread
                background.start()
            }
        } else {
            /****** Create Thread that will sleep for 5 seconds */
            val background: Thread = object : Thread() {
                override fun run() {
                    try {
                        // Thread will sleep for 5 seconds
                        sleep((2 * 1000).toLong())

                        // After 5 seconds redirect to another intent
                        val i = Intent(baseContext, MainActivity2::class.java)
                        startActivity(i)

                        //Remove activity
                        finish()
                    } catch (e: Exception) {
                    }
                }
            }
            // start thread
            background.start()
        }
    }


}