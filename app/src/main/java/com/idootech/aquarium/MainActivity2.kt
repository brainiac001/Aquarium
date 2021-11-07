package com.idootech.aquarium

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.idootech.aquarium.Model.Products
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


lateinit var buttonanimation: Animation
lateinit var backgroundmusic: MediaPlayer
private lateinit var mp: MediaPlayer
var length: Int = 0
var mdescription: String = ""
var nextdescription: String = ""
var mtitle: String = ""
var mimage: Int = 0
private lateinit var factsTitle: TextView
private lateinit var factsImage: ImageView
var currentFact = 0
var nextFact = 0
var dayCount: Long = 0L
var FirstRun: Boolean = false
var movieUrl: String = ""
var versionname: String = ""
var currentversion: String = ""
private var mInterstitialAd: InterstitialAd? = null
private var TAG = "MainActivity2"
private var disableAds = false

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main2)
        MobileAds.initialize(this) {}

        var adRequest = AdRequest.Builder().build()
        FirebaseMessaging.getInstance().subscribeToTopic("aquarium")

        InterstitialAd.load(
            this,
            "ca-app-pub-6887344842994800/8697117280",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })
        val facts_prepare = FactsList()

        factsImage = findViewById(R.id.facts_image2)
        factsTitle = findViewById(R.id.facts_title2)


        val preferences = getSharedPreferences("progress", MODE_PRIVATE)
        dayCount = preferences.getLong("dayCount", 0)
        var counts = preferences.getInt("counts", 0)
        val editor = preferences.edit()

        stopService(Intent(this, NotificationService::class.java))


        var currentDayCount = Date().time


        if (dayCount == 0L) {
            FirstRun = true
            val facts_list = ArrayList<Int?>()
            for (i in 1..58) facts_list.add(i)
            Collections.shuffle(facts_list)


            val str = StringBuilder()
            for (i in 0..57) {
                str.append(facts_list[i]).append(",")
            }

            editor.putString("string", str.toString())




            dayCount = currentDayCount

            editor.putLong("dayCount", dayCount)
            editor.apply()
            counts = 0
            var currentFact = facts_list[counts]
            var nextFact = facts_list[counts + 1]

            if (currentFact != null) {
                facts_prepare.chooseList(currentFact, nextFact)
            }
        } else {
            FirstRun = false
            //86400000
            val savedString = preferences.getString("string", "")
            val st = StringTokenizer(savedString, ",")
            val facts_list = IntArray(58)
            for (i in 0..57) {
                facts_list[i] = st.nextToken().toInt()

            }

            if ((currentDayCount - dayCount) > 86400000) {
                //Toast.makeText(baseContext, "New Day", Toast.LENGTH_SHORT).show()
                dayCount = currentDayCount
                if (counts > 56) {
                    counts = 0
                    currentFact = facts_list[counts]
                    nextFact = facts_list[counts + 1]
                    facts_prepare.chooseList(currentFact, nextFact)

                } else {
                    counts++
                    currentFact = facts_list[counts]
                    nextFact = facts_list[counts + 1]
                    facts_prepare.chooseList(currentFact, nextFact)

                }
                editor.putInt("counts", counts)
                editor.putLong("dayCount", dayCount)
                editor.apply()


            } else {
                var currentFact = facts_list[counts]
                if (counts == 57) {
                    nextFact = facts_list[counts + 1]
                    facts_prepare.chooseList(currentFact, nextFact)


                } else {
                    nextFact = facts_list[0]
                    facts_prepare.chooseList(currentFact, nextFact)
                }
            }
        }

        val fish = findViewById<ImageView>(R.id.fish_view)
        var animationDrawable = fish.drawable as AnimationDrawable
        animationDrawable.start()
        val mAnimation: Animation
        mAnimation = TranslateAnimation(
            TranslateAnimation.ABSOLUTE, 0f,
            TranslateAnimation.ABSOLUTE, 0f,
            TranslateAnimation.RELATIVE_TO_PARENT, 0f,
            TranslateAnimation.RELATIVE_TO_PARENT, .10f
        )
        mAnimation.duration = 1000
        mAnimation.repeatCount = -1
        mAnimation.repeatMode = Animation.REVERSE
        mAnimation.interpolator = LinearInterpolator()
        fish.animation = mAnimation




        backgroundmusic = MediaPlayer.create(this, R.raw.cave)
        backgroundmusic.isLooping = true


        val scheduler = Executors.newSingleThreadScheduledExecutor()
        scheduler.scheduleAtFixedRate(
            {
                runOnUiThread {

                }
            }, 0, 7420, TimeUnit.MILLISECONDS
        )


        buttonanimation = AnimationUtils.loadAnimation(this, R.anim.button_click)
        mp = MediaPlayer.create(this, R.raw.waterdrop)


        val PlayBtn = findViewById<LinearLayout>(R.id.play_btn)
        PlayBtn.setOnClickListener {
            PlayBtn.startAnimation(buttonanimation)
            mp.start()
            showAds()
            val intent = Intent(this@MainActivity2, HtmlConnect::class.java)
            startActivity(intent)

        }
        val HelpBtn = findViewById<LinearLayout>(R.id.help_btn)
        HelpBtn.setOnClickListener {
            HelpBtn.startAnimation(buttonanimation)
            mp.start()
            showAds()

            val intent = Intent(this@MainActivity2, HelpActivity::class.java)
            startActivity(intent)
        }

        val RateUsBtn = findViewById<LinearLayout>(R.id.rate_us_btn)
        RateUsBtn.setOnClickListener {

            RateUsBtn.startAnimation(buttonanimation)
            mp.start()
            showAds()

            val intent2 = Intent(Intent.ACTION_VIEW)
            intent2.data = Uri.parse("http://www.idootech.com.ng/aquarium")
            try {
                startActivity(intent2)
            } catch (e: ActivityNotFoundException) {
                intent2.data = Uri.parse("http://www.idootech.com.ng/aquarium")
            }
        }

        val MoreAppBtn = findViewById<LinearLayout>(R.id.more_apps_btn)
        MoreAppBtn.setOnClickListener {
            MoreAppBtn.startAnimation(buttonanimation)
            mp.start()
            showAds()

            val intent2 = Intent(Intent.ACTION_VIEW)
            intent2.data = Uri.parse("http://www.idootech.com.ng/apps")
            try {
                startActivity(intent2)
            } catch (e: Exception) {
                intent2.data = Uri.parse("http://www.idootech.com.ng/apps")
            }
        }

        val CreditsBtn = findViewById<LinearLayout>(R.id.credits_btn)
        CreditsBtn.setOnClickListener {
            CreditsBtn.startAnimation(buttonanimation)
            mp.start()
            showAds()

            val intent = Intent(this@MainActivity2, CreditsActivity::class.java)
            startActivity(intent)
        }

        versionname = BuildConfig.VERSION_NAME
        FirebaseDatabase.getInstance().reference.child("App Version")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val profile: Products? = dataSnapshot.getValue(Products::class.java)
                    if (profile != null) {
                        movieUrl = profile.pdescription!!
                        currentversion = profile.pname!!
                        if (versionname != currentversion) {
                            NewVersionUpdate()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        val scheduler2 = Executors.newSingleThreadScheduledExecutor()
        scheduler2.scheduleAtFixedRate({
            Log.i("hello", "world")
            runOnUiThread {
                if (disableAds) {
                } else {
                    showAds()
                }

            }
        }, 300, 300, TimeUnit.SECONDS)

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                mInterstitialAd = null
            }
        }

    }


    fun ShareApp(view: View?) {
        try {
            view?.let<View, View?> { findViewById(it.id) }?.startAnimation(buttonanimation)
            mp.start()
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Aquarium")
            var shareMessage =
                "\nI just played this game called Aquarium it's awesome, am sure you would love it too, download using the link below\n\n"
            shareMessage =
                """
                ${shareMessage}http://www.idootech.com.ng/aquarium
                
                
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: java.lang.Exception) {
            //e.toString();
        }
    }

    fun Exit(view: View?) {
        view?.let<View, View?> { findViewById(it.id) }?.startAnimation(buttonanimation)
        mp.start()

        onBackPressed()

    }


    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Alert!")
        alert.setIcon(R.mipmap.ic_launcher)
        alert.setMessage("Are you sure you want to exit the sole survival of your aquarium depends on you")
        alert.setPositiveButton(
            "YES"
        ) { dialog, whichButton ->
            finishAffinity()
        }
        alert.setNegativeButton(
            "NO"
        ) { dialog, which ->
        }
        alert.show()


    }


    override fun onStop() {
        super.onStop()
        backgroundmusic.pause()
        length = backgroundmusic.currentPosition
        disableAds = true
/*
        val intent = Intent(this, NotificationService::class.java)
        intent.putExtra("description", nextdescription)
        startService(intent)

 */
    }


    override fun onStart() {
        super.onStart()
        disableAds = false

    }

    override fun onResume() {
        super.onResume()

        backgroundmusic.seekTo(length)
        backgroundmusic.start()
    }

    fun getFactsContent(
        Image: Int,
        Title: String,
        Description: String,
        NextDescription: String
    ) {

        //factsImage = findViewById(R.id.facts_image2)
        factsImage.setImageResource(Image)
        factsImage.visibility = View.VISIBLE
        // factsTitle = findViewById(R.id.facts_title2)
        factsTitle.text = Title
        factsTitle.visibility = View.VISIBLE



        mtitle = Title
        mdescription = Description
        mimage = Image
        nextdescription = NextDescription

    }

    fun readMore(view: View?) {
        view?.let<View, View?> { findViewById(it.id) }?.startAnimation(buttonanimation)
        mp.start()
        showAds()
        val intent = Intent(this, FactsActivity::class.java)
        intent.putExtra("title", mtitle)
        intent.putExtra("description", mdescription)
        intent.putExtra("image", mimage)
        startActivity(intent)
    }

    private fun NewVersionUpdate() {
        val alert = androidx.appcompat.app.AlertDialog.Builder(this)
        alert.setTitle("New App Update!")
        alert.setIcon(R.mipmap.ic_launcher)
        alert.setMessage("New version of " + getString(R.string.app_name) + " App is available, click yes to download version " + currentversion + " you are currently using version " + versionname)
        alert.setPositiveButton(
            "YES"
        ) { dialog, whichButton ->
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(movieUrl)))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(movieUrl)))
            }
        }
        alert.setNegativeButton(
            "NO"
        ) { dialog, which -> }
        alert.show()
    }

    private fun showAds() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-6887344842994800/8697117280",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

}