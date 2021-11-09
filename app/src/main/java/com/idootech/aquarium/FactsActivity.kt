package com.idootech.aquarium

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class FactsActivity : AppCompatActivity() {


    private lateinit var factsTitle: TextView
    private lateinit var factsDescription: TextView
    private lateinit var factsImage: ImageView
    private var mInterstitialAd: InterstitialAd? = null
    private var TAG = "FactsActivity"
    private var disableAds = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_facts)

        var mtitle = intent.extras!!["title"].toString()
        var mimage = intent.extras!!["image"]
        var mdescription = intent.extras!!["description"].toString()



        factsTitle = findViewById(R.id.facts_title)
        factsTitle.text = mtitle
        factsDescription = findViewById(R.id.movie_description)
        factsDescription.text = mdescription
        factsImage = findViewById(R.id.facts_image)
        factsImage.setImageResource(mimage as Int)

        MobileAds.initialize(this) {}

        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
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

    fun Exit(view: View?) {
        showAds()
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        return

    }

    private fun showAds() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
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

    override fun onStart() {
        super.onStart()
        disableAds = false

    }

    override fun onStop() {
        super.onStop()

        disableAds = true

    }

}
