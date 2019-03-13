package com.kickstart

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.MaterialModule
import com.kickstart.utils.Prefs
import java.util.*


/**
 * Created by sotsys-055 on 29/12/17.
 */
open class ApplicationClass : Application() {

    lateinit var appContext: Context

    var PREF_NAME = "applicationClass"
    private var prefs: Prefs? = null

    private var mActivityTransitionTimer: Timer? = null
    private var mActivityTransitionTimerTask: TimerTask? = null
    var wasInBackground: Boolean = false
    private val MAX_ACTIVITY_TRANSITION_TIME_MS: Long = 3000

    var userReviewd: Boolean = false
    private var mUserTransitionTimer: Timer? = null
    private var mUserTransitionTimerTask: TimerTask? = null

    override fun onCreate() {
        super.onCreate()
        appContext = this
        mInstance = this
        app = this

        Iconify.with(MaterialModule())
        prefs = Prefs(getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE))
    }

    fun appVersion(): String {
        val manager = packageManager
        var info: PackageInfo? = null
        try {
            info = manager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return info!!.versionName
    }

    fun getPrefs(): Prefs? {
        return prefs
    }

    fun startActivityTransitionTimer() {
        this.mActivityTransitionTimer = Timer()
        this.mActivityTransitionTimerTask = object : TimerTask() {
            override fun run() {
                wasInBackground = true
            }
        }

        this.mActivityTransitionTimer!!.schedule(mActivityTransitionTimerTask,
                MAX_ACTIVITY_TRANSITION_TIME_MS)
    }

    fun stopActivityTransitionTimer() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask!!.cancel()
        }

        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer!!.cancel()
        }

        this.wasInBackground = false
    }


    fun checkReviewStatus() {
        userReviewd = false
        this.mUserTransitionTimer = Timer()
        this.mUserTransitionTimerTask = object : TimerTask() {
            override fun run() {
                userReviewd = true
            }
        }

        this.mUserTransitionTimer!!.schedule(mUserTransitionTimerTask,
                5000)
    }

    var fromBackground = false
    var backgroundCounter = 0

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN || level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            fromBackground = true
        }
    }

    companion object {
        lateinit var mInstance: ApplicationClass
        lateinit var app: ApplicationClass

        @Synchronized
        fun getInstance(): ApplicationClass {
            return mInstance
        }

        fun getApps(): ApplicationClass {
            return app
        }
    }
}