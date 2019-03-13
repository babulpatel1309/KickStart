package com.kickstart.constants

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.kickstart.R
import java.io.Serializable


const val ddMMyyyy = "dd-MM-yyyy"
const val ddMMMyyyy = "dd MMM yyyy"
const val ddMMyyyyHHmma = "dd-MM-yyyy hh:mm a"
const val HHmma = "hh:mm a"
const val EEEE = "EEEE"
const val dd = "dd"

const val yyyyMMdd_HHmmssSSS = "yyyyMMdd_HHmmssSSS"


const val selection: String = ""

const val APP_BASE_URL = "http://172.16.16.43/damsharas/"
const val PLAYSTORE_URL = "https://play.google.com/store/apps/details?id="

val HTTP_TIMEOUT: Long = 80
val success = 200

val SPLASH_TIMEOUT = 1500L

const val BUNDLE_DATA = "DATA"
const val docPath = "/sdcard/KickStart/"
const val editImages = ".edit/"
const val compressedImages = ".compressed/"
const val video = ".video/"
const val recordings = "Recordings/"
const val CAMERA_FACING = "CAMERA_FACING"
const val stFlashMode = "FlashMode"
const val selectedImage = "selectedImage"
const val pickedImage = "pickedImage"
const val activityName = "activityName"
const val DATABASE_NAME = "headsupgamedb.db"
const val PKG_FB = "com.facebook.katana"
const val PKG_INSTAGRAM = "com.instagram.android"
const val PKG_GMAIL = "com.google.android.gm"

const val CONTACT_PICK = 10001
const val IMAGES_PICK = 10002
const val TAKE_PICTURE = 10003
const val DRIVE_PICK = 10004
const val EVENT_LISTENER = 10005
const val RC_REQUEST = 10006
const val EMAIL_SENT = 10007
const val CAMERA_PICK = 10008
const val RC_SIGN_IN = 10009

const val CONTACTS = 101
const val STORAGE = 102
const val CAMERA = 103
const val AUDIO = 104


const val WS_PARAM_CATID = "cat_id"

data class ScoreTracker(
        var question: String,
        var answer: Boolean = false
) : Serializable

fun getColorInt(context: Context, colorName: Int): Int {
    return ContextCompat.getColor(context, colorName)
}

fun getDrawableInt(context: Context, drawableName: Int): Drawable? {
    return ContextCompat.getDrawable(context, drawableName)
}

data class SideMenuBean(val menuTxt: String, val menuImg: Int)

const val menuProfile = "Profile"
const val menuLogout = "Logout"

fun getMenuItems(): List<SideMenuBean> {
    return listOf(SideMenuBean(menuProfile, R.drawable.ic_person_black_24dp),
            SideMenuBean(menuLogout, R.drawable.ic_exit_to_app_black_24dp)
    )
}


/*Toolbar Theme*/

const val STYLE_BLUE = "BLUE"
const val STYLE_WHITE = "WHITE"

const val PREF_SOUND_ON = "SOUND"
const val PREF_ROUND_TIME = "ROUND_TIME"
const val PREF_VIBRATE_ON = "VIBRATE"
const val PREF_RECORDING_ON = "RECORDING"
const val PREF_FIRST_LAUNCH = "FIRST_LAUNCH"