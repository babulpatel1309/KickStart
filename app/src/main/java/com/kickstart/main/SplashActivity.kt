package com.kickstart.main

import com.kickstart.R
import com.kickstart.constants.PREF_FIRST_LAUNCH
import com.kickstart.constants.SPLASH_TIMEOUT
import com.kickstart.constants.STORAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// Created by Babul Patel on 5/5/18.

class SplashActivity : BaseActivity() {
    override fun setContentView(): Int {
        return R.layout.activity_splash
    }

    override fun init() {

        askPermission(context, object : PermissionListener {
            override fun onGranted() {
                if (prefs.getBooleanDetailTRUE(PREF_FIRST_LAUNCH)) {
                    prefs.setBooleanDetail(PREF_FIRST_LAUNCH, false)
                }

                GlobalScope.launch (Dispatchers.Main){
                    delay(SPLASH_TIMEOUT)
                    goWithFinish(DashboardActivity::class.java)
                }
            }

            override fun onDenied() {
            }

        }, STORAGE)

    }

    override fun buttonClicks() {
    }
}