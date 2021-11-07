package com.idootech.aquarium

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


lateinit var webView: WebView
lateinit var mWebContainer: ConstraintLayout
lateinit var loadLayout: ConstraintLayout
lateinit var play_layout: LinearLayout
private var disableAds: Boolean = false

private lateinit var mp: MediaPlayer
var isPaused: Boolean = false
var isFirstLoad = true
private var mInterstitialAd: InterstitialAd? = null
private var TAG = "HtmlConnect"
private var webViewIsRestarting: Boolean = true

class HtmlConnect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_html_connect)

        mp = MediaPlayer.create(this, R.raw.waterdrop)


        webView = findViewById<WebView>(R.id.webview)

        mWebContainer = findViewById<ConstraintLayout>(R.id.webview_contanner)
        loadLayout = findViewById<ConstraintLayout>(R.id.load_layout)
        loadLayout.visibility = View.VISIBLE
        play_layout = findViewById<LinearLayout>(R.id.paly_layout)
        //play_layout.visibility = View.GONE

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true


        MobileAds.initialize(this) {}

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

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                //firstRun()

                if (FirstRun) {
                    loadLayout.visibility = View.GONE
                    firstRun()
                } else {
                    showAds()

                    val background2: Thread = object : Thread() {
                        override fun run() {
                            try {
                                // Thread will sleep for 14 seconds
                                sleep((2000).toLong())
                                loadLayout.visibility = View.GONE


                            } catch (e: Exception) {
                            }
                        }
                    }
                    // start thread
                    background2.start()
                }

            }


        }
        webView.loadUrl("file:///android_asset/game.html")

        animateFishAndCrab()

        webView.onResume()
        webView.resumeTimers()
/*
        webView.setOnTouchListener(View.OnTouchListener { v, event -> //show dialog here
            webView.onResume()
            webView.resumeTimers()
            if (isPaused&& isFirstLoad) {

                isFirstLoad = false
            }
            false
        })
*/
        val pauseBtn = findViewById<ImageView>(R.id.pause_btn)
        pauseBtn.setOnClickListener {
            mp.start()
            webView.onPause()
            webView.pauseTimers()
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Game Paused!")
            alert.setIcon(R.mipmap.ic_launcher)
            alert.setMessage("Game Paused. Do you want to resume game?")
            alert.setPositiveButton(
                "YES"
            ) { dialog, whichButton ->
                webView.onResume()
                webView.resumeTimers()


            }

            alert.show()
            alert.setCancelable(false)


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

    fun restartActivity(view: View?) {
        mp.start()

        webView.onPause()
        webView.pauseTimers()
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Warning!")
        alert.setIcon(R.mipmap.ic_launcher)
        alert.setMessage("Are you sure you want to restart, all progress would be lost!")
        alert.setPositiveButton(
            "YES"
        ) { dialog, whichButton ->
            webViewIsRestarting = true
            webView.onResume()
            webView.resumeTimers()
            webView.reload()

            loadLayout.visibility = View.VISIBLE
            animateFishAndCrab()


        }
        alert.setNegativeButton(
            "NO"
        ) { dialog, which ->
            webView.onResume()
            webView.resumeTimers()
        }
        alert.show()

        alert.setCancelable(false)

    }

    fun firstRun() {
        webView.onPause()
        webView.pauseTimers()
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Hello")
        alert.setIcon(R.mipmap.ic_launcher)
        alert.setMessage("Welcome to Aquarium, Move the fish using the arrow keys/buttons, eat as many worms as possible before you run out of time. Always avoid the crabs to prevent the fish from getting injured. Collect as many stars as possible to defend yourself from the crab. But be careful the crabs multiplies and moves faster as you progress from one level to another.")
        alert.setPositiveButton(
            "SKIP"
        ) { dialog, whichButton ->
            webView.onResume()
            webView.resumeTimers()

        }

        alert.show()

    }


    fun resume(view: View?) {
        mp.start()

        play_layout.visibility = View.GONE
        webView.onResume()
        webView.resumeTimers()

    }

    fun help(view: View?) {
        mp.start()
        play_layout.visibility = View.VISIBLE
        webView.onPause()
        webView.pauseTimers()
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }
    /*
    fun destroyWebView() {

        // Make sure you remove the WebView from its parent view before doing anything.
       mWebContainer.removeAllViews()
        webView.clearHistory()

        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        //webView.clearCache(true)

        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        //webView.loadUrl("about:blank")
        webView.onPause()
       webView.removeAllViews()
        webView.destroyDrawingCache()

        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
        webView.pauseTimers()

        // NOTE: This can occasionally cause a segfault below API 17 (4.2)
        webView.destroy()

        // Null out the reference so that you don't end up re-using it.
    }
*/

    fun Exit(view: View?) {

        onBackPressed()
    }


    override fun onBackPressed() {
        mp.start()
        webView.onPause()
        webView.pauseTimers()
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Warning!")
        alert.setIcon(R.mipmap.ic_launcher)
        alert.setMessage("Are you sure you want to quit, all progress would be lost!")
        alert.setPositiveButton(
            "YES"
        ) { dialog, whichButton ->
            webView.onResume()
            webView.resumeTimers()
            webView.destroy()
            webView.reload()
            webViewIsRestarting = true

            val intent = Intent(this, LoadActivity::class.java)
            startActivity(intent)

            finish()
        }
        alert.setNegativeButton(
            "NO"
        ) { dialog, which ->
            webView.onResume()
            webView.resumeTimers()
        }
        alert.show()
        alert.setCancelable(false)


    }


    override fun onPause() {
        super.onPause()
        disableAds = true
        if (webViewIsRestarting) {

        } else {

            webviewpause()


        }
    }

    override fun onStart() {
        super.onStart()
        disableAds = false

    }

    override fun onResume() {
        super.onResume()
        disableAds = false


    }

    override fun onStop() {
        super.onStop()
        disableAds = true

    }

    fun webviewpause() {
        play_layout.visibility = View.VISIBLE
        webView.onPause()
        webView.pauseTimers()


    }

    fun animateFishAndCrab() {

        val slideAnimation4 = AnimationUtils.loadAnimation(this, R.anim.left_slide)
        val dropbottom: ImageView = findViewById(R.id.fish_load)
        dropbottom.startAnimation(slideAnimation4)

        val slideAnimation5 = AnimationUtils.loadAnimation(this, R.anim.right_slide)
        val droptop: ImageView = findViewById(R.id.crab_load)
        droptop.startAnimation(slideAnimation5)
    }

    private fun showAds() {
        if (disableAds) {
        } else {
            var adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                this,
                "ca-app-pub-6887344842994800/8697117280",
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError.message)
                        mInterstitialAd = null
                        webViewIsRestarting = false


                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d(TAG, "Ad was loaded.")
                        webViewIsRestarting = false

                        mInterstitialAd = interstitialAd


                    }
                })
            if (mInterstitialAd != null) {

                mInterstitialAd?.show(this)

            } else {
                webViewIsRestarting = false

                Log.d("TAG", "The interstitial ad wasn't ready yet.")

            }

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

    }


}