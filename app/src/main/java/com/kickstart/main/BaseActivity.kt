package com.kickstart.main

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.MaterialIcons
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.kickstart.ApplicationClass
import com.kickstart.R
import com.kickstart.WebService.ServiceInterface
import com.kickstart.constants.*
import com.kickstart.data.Database.RoomDBHelper
import com.kickstart.data.ViewModels.CategoryViewModel
import com.kickstart.data.ViewModels.ScoreViewModel
import com.kickstart.data.ViewModels.WordsViewModel
import com.kickstart.utils.AnimatedColor
import com.kickstart.utils.ImageFilePath
import com.kickstart.utils.Prefs
import kotlinx.android.synthetic.main.custom_actionbar.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

public abstract class BaseActivity : AppCompatActivity(),AnkoLogger {

    /*
    * Lateinit vars*/
    lateinit var context: Context
    lateinit var prefs: Prefs
    lateinit var roomDBHelper: RoomDBHelper
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var scoreViewModel: ScoreViewModel
    lateinit var wordsViewModel: WordsViewModel
    lateinit var inflater: LayoutInflater

    /*
    * Initialized vars*/
    var TAG: String = ""
    var currentCalendar = Calendar.getInstance()
    var msg_wrong = "Something went wrong"
    var fieldMap = JsonObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (setContentView() > 0)
            setContentView(setContentView())

        context = this
        TAG = context.javaClass.simpleName

        prefs = ApplicationClass.mInstance.getPrefs()!!

        roomDBHelper = RoomDBHelper.getInstance(context)

        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)
        scoreViewModel = ViewModelProviders.of(this).get(ScoreViewModel::class.java)
        wordsViewModel = ViewModelProviders.of(this).get(WordsViewModel::class.java)


        inflater = LayoutInflater.from(context)

        initProgressDialog(context)
        init()
        buttonClicks()

        /**
         * Create necessary folders only.
         * Change @docpath variable in Appheart for parent folder name.
         */
        /*createDirectory()
        createDirectory(video)
        createDirectory(recordings)*/

        Handler().postDelayed({
            getScreenResolution()
        }, 300)
        isSharePackageInstalled()

        GlobalScope.launch {
            delay(1000)
            centerPoints[0] = resources.displayMetrics.widthPixels / 2
            centerPoints[1] = resources.displayMetrics.heightPixels / 2
        }

    }

    fun databaseExist(): Boolean {
        val dbFile = File(RoomDBHelper.DB_PATH + DATABASE_NAME)
        return dbFile.exists()
    }


    var progressDialog: ProgressDialog? = null


    fun setToolbar(title: String, isBack: Boolean = false, titleLayView: View) {
        titleLayView.txtTitle.text = title
        titleLayView.txtTitle.setTextColor(getColorInt(R.color.white))

        if (isBack) {
            titleLayView.imgLeftIcon.setImageDrawable(getMaterialDrawable(MaterialIcons.md_arrow_back))
        } else {
            titleLayView.imgLeftIcon.setImageDrawable(getMaterialDrawable(MaterialIcons.md_menu))
        }

    }

    private fun initProgressDialog(context: Context) {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)
    }

    fun showProgressDialog() {
        try {
            if (!(context as Activity).isFinishing) {
                if (progressDialog != null && !progressDialog!!.isShowing) {
                    progressDialog!!.show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hideProgressDialog() {
        try {
            if (!(context as Activity).isFinishing)
                if (progressDialog != null
                        && progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun go(cls: Class<*>) {
        startActivity(Intent(context, cls))
    }

    fun goWithFinish(cls: Class<*>) {
        startActivity(Intent(context, cls))
        finish()
    }

    fun goWithFinish(cls: Class<*>, bundle: Bundle) {
        startActivity(Intent(context, cls), bundle)
        finish()
    }

    fun go(cls: Class<*>, bundle: Bundle?) {
        startActivity(Intent(context, cls), bundle)
    }


    fun goWithSerializable(cls: Class<*>, list: ArrayList<*>) {
        startActivity(Intent(context, cls).putExtra(selection, list))
    }

    fun goWithData(cls: Class<*>, data: Any) {

        when (data) {
            is Int -> startActivity(Intent(context, cls).putExtra(selection, data))
            is String -> startActivity(Intent(context, cls).putExtra(selection, data))
            is Long -> startActivity(Intent(context, cls).putExtra(selection, data))
            is Double -> startActivity(Intent(context, cls).putExtra(selection, data))
            is Float -> startActivity(Intent(context, cls).putExtra(selection, data))
        }

    }

    fun goWithSerializableForResult(cls: Class<*>, list: ArrayList<*>) {
        startActivityForResult(Intent(context, cls).putExtra(selection, list), EVENT_LISTENER)
    }

    fun goWithResult(cls: Class<*>, picker: Int) {
        startActivityForResult(Intent(context, cls), picker)
    }

    fun goWithSerializableWithFinish(cls: Class<*>, list: ArrayList<*>) {
        startActivity(Intent(context, cls).putExtra(selection, list))
        finish()
    }

    fun goWithSharedElement(cls: Class<*>, bundle: Bundle, content: Int? = 0, imagesUri: ArrayList<Uri>) {
        startActivity(Intent(context, cls)
                .putExtra(selectedImage, content)
                .putExtra(pickedImage, imagesUri)
                , bundle)
    }

    fun goWithSharedElement(cls: Class<*>, bundle: Bundle) {
        startActivity(Intent(context, cls)
                , bundle)
    }

    fun goWithSharedElement(cls: Class<*>, bundle: Bundle, activityFrom: String) {
        startActivity(Intent(context, cls).putExtra(activityName, activityFrom)
                , bundle)
    }

    /*Fragment Methods*/

    fun getLocalFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

    var lastClicked: Long = 0
    fun addFragment(newFragment: Fragment) {
        if (SystemClock.elapsedRealtime() - lastClicked < 1000) {
            print("Escaped from double click...!!!")
            return
        }
        lastClicked = SystemClock.elapsedRealtime()

        getLocalFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_fragment_horizontal_right_in, R.animator.slide_fragment_horizontal_left_out, R.animator.slide_fragment_horizontal_left_in, R.animator.slide_fragment_horizontal_right_out)
                .add(R.id.container, newFragment, newFragment.javaClass.simpleName)
                .addToBackStack(null)
                .commit()
    }

    fun replaceFragment(newFragment: Fragment) {
        getLocalFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_fragment_horizontal_right_in, R.animator.slide_fragment_horizontal_left_out, R.animator.slide_fragment_horizontal_left_in, R.animator.slide_fragment_horizontal_right_out)
                .replace(R.id.container, newFragment, newFragment.javaClass.simpleName)
                .commit()
    }

    fun removeFragment(newFragment: Fragment) {
        getLocalFragmentManager()
                .beginTransaction()
                .remove(newFragment)
                .commit()

    }

    /**
     * With custom framelayout*/

    fun addFragment(newFragment: Fragment, id: Int) {
        if (SystemClock.elapsedRealtime() - lastClicked < 1000) {
            print("Escaped from double click...!!!")
            return
        }
        lastClicked = SystemClock.elapsedRealtime()

        getLocalFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_fragment_horizontal_right_in, R.animator.slide_fragment_horizontal_left_out, R.animator.slide_fragment_horizontal_left_in, R.animator.slide_fragment_horizontal_right_out)
                .add(id, newFragment, newFragment.javaClass.simpleName)
                .addToBackStack(null)
                .commit()
    }

    fun replaceFragment(newFragment: Fragment, id: Int) {
        getLocalFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_fragment_horizontal_right_in, R.animator.slide_fragment_horizontal_left_out, R.animator.slide_fragment_horizontal_left_in, R.animator.slide_fragment_horizontal_right_out)
                .replace(id, newFragment, newFragment.javaClass.simpleName)
                .commit()
    }

    fun goWithDelay(cls: Class<*>, bundle: Bundle) {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                startActivity(Intent(context, cls).putExtra(BUNDLE_DATA, bundle))
            }

        }, 300)
    }


    fun finishWithDelay() {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                finish()
            }

        }, 300)
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun openKeyboard(editText: EditText) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0)
        editText.requestFocus()
    }

    interface PermissionListener {
        fun onGranted()

        fun onDenied()
    }

    /**
     * Handle all kind of permissions.
     */

    fun askPermission(context: Context = this.context, permissionListener: PermissionListener, type: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionListener.onGranted()
        }

        when (type) {

            CONTACTS -> {

                Dexter.withActivity(this)
                        .withPermission(Manifest.permission.READ_CONTACTS)
                        .withListener(object : com.karumi.dexter.listener.single.PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                permissionListener.onGranted()
                            }

                            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                                val builder = AlertDialog.Builder(context)
                                builder.setMessage("We need permissions for this app.")
                                builder.setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        token?.continuePermissionRequest()
                                    }
                                })
                                builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        toast("Without CONTACTS permission this app won't work as Expected..!!")
                                    }

                                })
                                builder.show()

                            }

                            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                permissionListener.onDenied()
                                onSettingsShown()
                                toast("Without CONTACTS permission this app won't work as Expected..!!")
                            }
                        })
                        .check()

            }

            STORAGE -> {

                Dexter.withActivity(this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(object : com.karumi.dexter.listener.single.PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                permissionListener.onGranted()
                            }

                            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                                val builder = AlertDialog.Builder(context)
                                builder.setMessage("We need permissions for this app.")
                                builder.setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        token?.continuePermissionRequest()
                                    }
                                })
                                builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        toast("Without STORAGE permission this app won't work as Expected..!!")
                                    }

                                })
                                builder.show()

                            }

                            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                permissionListener.onDenied()
                                onSettingsShown()
                                toast("Without STORAGE permission this app won't work as Expected..!!")
                            }
                        })
                        .check()

            }

            CAMERA -> {

                Dexter.withActivity(this)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(object : com.karumi.dexter.listener.single.PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                permissionListener.onGranted()
                            }

                            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                                val builder = AlertDialog.Builder(context)
                                builder.setMessage("We need permissions for this app.")
                                builder.setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        token?.continuePermissionRequest()
                                    }
                                })
                                builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        toast("Without CAMERA permission this app won't work as Expected..!!")
                                    }

                                })
                                builder.show()

                            }

                            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                permissionListener.onDenied()
                                onSettingsShown()
                                toast("Without CAMERA permission this app won't work as Expected..!!")
                            }
                        })
                        .check()

            }

            AUDIO -> {

                Dexter.withActivity(this)
                        .withPermission(Manifest.permission.RECORD_AUDIO)
                        .withListener(object : com.karumi.dexter.listener.single.PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                permissionListener.onGranted()
                            }

                            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                                val builder = AlertDialog.Builder(context)
                                builder.setMessage("We need permissions for this app.")
                                builder.setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        token?.continuePermissionRequest()
                                    }
                                })
                                builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        toast("Without AUDIO permission this app won't work as Expected..!!")
                                    }

                                })
                                builder.show()

                            }

                            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                permissionListener.onDenied()
                                onSettingsShown()
                                toast("Without AUDIO permission this app won't work as Expected..!!")
                            }
                        })
                        .check()

            }

        }
    }

    fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun onSettingsShown() {
        // open setting screen
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data = Uri.parse("package:" + context.getPackageName())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(intent)
    }

    fun saveBitmapToFile(bitmap: Bitmap, path: String = ""): Uri {

        var savePath = path
        if (path.isEmpty()) savePath = docPath + editImages

        val filename = generateFileName()
        val dest = File(savePath, filename)

        try {
            val out = FileOutputStream(dest)
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Uri.fromFile(dest)
    }

    fun saveFilteredBitmapToFile(bitmap: Bitmap, uri: Uri): Uri {
        val dest = File(uri.path)

        try {
            val out = FileOutputStream(dest)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Uri.fromFile(dest)
    }


    fun generateFileName(): String {
        val timeStamp = SimpleDateFormat(yyyyMMdd_HHmmssSSS).format(Date())
        return "PNG_" + timeStamp + "_.png"
    }

    fun onRedirectToPlayStore(pkgName: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PLAYSTORE_URL + pkgName)))
    }

    var height = 0
    var width = 0

    fun getScreenResolution() {
        if (height <= 0 || width <= 0) {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            height = displayMetrics.heightPixels
            width = displayMetrics.widthPixels
        }
    }

    /**
     * Time management.
     */
    fun getCurrentDate(format: String = ddMMyyyy): String {

        val sdf = SimpleDateFormat(format, Locale.US)
        return sdf.format(currentCalendar.getTime())
    }

    fun getDateFromTimeStamp(milliSeconds: String): String {

        var timeDetail = milliSeconds
        val formatter = SimpleDateFormat(ddMMyyyy)


        if (timeDetail.length < 11) {
            timeDetail = milliSeconds + "000"
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = c2Long(timeDetail)
        return formatter.format(calendar.time)
    }

    fun getDateFromTimeStamp(milliSeconds: String, format: String = ddMMMyyyy): String {

        var timeDetail = milliSeconds
        val formatter = SimpleDateFormat(format)


        if (timeDetail.length < 11) {
            timeDetail = milliSeconds + "000"
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = c2Long(timeDetail)
        return formatter.format(calendar.time)
    }

    fun convertDateToMilliSeconds(date: String, format: String = ddMMMyyyy): Long {

        var sdf = SimpleDateFormat(format)
        try {
            val mDate = sdf.parse(date)
            var timeInMilliseconds = mDate.time

            if (format != ddMMMyyyy) {
                sdf = SimpleDateFormat(ddMMyyyy)
                val newDateString = sdf.format(mDate)

                val newDate = sdf.parse(newDateString)
                timeInMilliseconds = newDate.time

                System.out.println("Date in milli :: $timeInMilliseconds")
            }

            return timeInMilliseconds
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return 0L
    }

    fun getDayOfTheWeek(timeStamp: String): String {
        var timeDetail = timeStamp
        if (timeDetail.length < 11) {
            timeDetail = timeStamp + "000"
        }

        val c = Calendar.getInstance()
        c.timeInMillis = c2Long(timeDetail)

        val dayNum = c.get(Calendar.DAY_OF_WEEK)

        return daysOfWeek(dayNum)

    }

    fun daysOfWeek(DayOfWeek: Int): String {
        when (DayOfWeek) {
            1 -> return "MON"
            2 -> return "TUE"
            3 -> return "WED"
            4 -> return "THU"
            5 -> return "FRI"
            6 -> return "SAT"
            7 -> return "SUN"
            else -> return ""
        }
    }


    fun convertDateToSeconds(date: String, format: String = ddMMMyyyy): Long {

        var sdf = SimpleDateFormat(format)
        try {
            val mDate = sdf.parse(date)
            var timeInMilliseconds = mDate.time

            return timeInMilliseconds / 1000
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return 0L
    }

    /**
     * Image compression.
     */
    fun compressImage(imageUri: Uri): Uri {

        val filename: String
        try {
            val filePath = ImageFilePath.getPath(context, imageUri)
            var scaledBitmap: Bitmap? = null

            val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(filePath, options)

            var actualHeight = options.outHeight
            var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612

            val maxHeight = height.toFloat()
            val maxWidth = width.toFloat()
            var imgRatio = (actualWidth / actualHeight).toFloat()
            val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = (maxHeight / actualHeight)
                    actualWidth = ((imgRatio * actualWidth).toInt())
                    actualHeight = maxHeight.toInt()
                } else if (imgRatio > maxRatio) {
                    imgRatio = (maxWidth / actualWidth)
                    actualHeight = ((imgRatio * actualHeight).toInt())
                    actualWidth = maxWidth.toInt()
                } else {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)

            try {
                //          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }

            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f

            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

            val canvas = Canvas(scaledBitmap)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2,
                    Paint(Paint.FILTER_BITMAP_FLAG))

//      check the rotation of the image and display it properly
            val exif: ExifInterface
            try {
                exif = ExifInterface(filePath)

                val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0)
                Log.d("EXIF", "Exif: " + orientation)
                val matrix = Matrix()
                if (orientation == 6) {
                    matrix.postRotate(90F)
                    Log.d("EXIF", "Exif: " + orientation)
                } else if (orientation == 3) {
                    matrix.postRotate(180F)
                    Log.d("EXIF", "Exif: " + orientation)
                } else if (orientation == 8) {
                    matrix.postRotate(270F)
                    Log.d("EXIF", "Exif: " + orientation)
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap?.getWidth()!!, scaledBitmap.getHeight(), matrix,
                        true)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val out: FileOutputStream
            filename = docPath + compressedImages + filePath.substring(filePath.lastIndexOf("/") + 1)
            try {
                out = FileOutputStream(filename)
                scaledBitmap?.compress(Bitmap.CompressFormat.PNG, 80, out)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return Uri.parse(filename)
        } catch (e: Exception) {
            e.printStackTrace()
            return imageUri
        }
    }

    fun convertToLocal(imagesUri: ArrayList<Uri>): ArrayList<Uri> {
        val localUri = ArrayList<Uri>()
        for (i in 0 until imagesUri.size) {

            if (imagesUri[i].toString().startsWith("content://com.google.android.apps.docs")
                    || imagesUri[i].toString().startsWith("content://com.google.android.apps.photos")
                    || imagesUri[i].toString().startsWith("content://com.dropbox.android.FileCache")) {
                val selectedBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imagesUri[i])
                val uri = saveBitmapToFile(selectedBitmap)
                print("No : $i -> $uri")

                localUri.add(uri)
            } else {
                localUri.add(savefile(imagesUri[i]))
            }
        }

        return localUri
    }

    fun savefile(sourceuri: Uri): Uri {

        val compressedURI = compressImage(sourceuri)

//        val sourceFilename = ImageFilePath.getPath(context, compressedURI)
        val sourceFilename = compressedURI.path
        val destinationFilename = docPath + editImages + sourceFilename.substring(sourceFilename.lastIndexOf("/") + 1)

        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null

        try {
            bis = BufferedInputStream(FileInputStream(sourceFilename))
            bos = BufferedOutputStream(FileOutputStream(destinationFilename, false))
            val buf = ByteArray(1024)
            bis.read(buf)
            do {
                bos.write(buf)
            } while (bis.read(buf) != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (bis != null) bis.close()
                if (bos != null) bos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return Uri.fromFile(File(destinationFilename))
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            while (height / inSampleSize > reqHeight || width / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun sharedTransition(view: View, value: Int): Bundle {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, resources.getString(value))
        return options.toBundle()!!
    }

    fun openGallery() {

        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, IMAGES_PICK)
        } else {
            intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("image/*")
            startActivityForResult(intent, IMAGES_PICK)
        }
    }

    fun convertBitmpaToByteArray(bmp: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return byteArray
    }

    fun convertByteArrayToBitmap(array: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(array, 0, array.size)
    }

    var apiService: ServiceInterface? = null

    fun callWS(): ServiceInterface? {

        val builder = OkHttpClient().newBuilder()

        builder.readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val requestBuilder = originalRequest.newBuilder()
                            .method(originalRequest.method(), originalRequest.body())

                    val request = requestBuilder.build()
                    chain.proceed(request)
                }

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        builder.addInterceptor(interceptor)
        val gson = GsonBuilder()
                .setLenient()
                .create()

        val client = builder.build()
        val retrofit: Retrofit
        retrofit = Retrofit.Builder()
                .baseUrl(APP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()

        apiService = retrofit.create<ServiceInterface>(ServiceInterface::class.java)

        return apiService
    }


    fun refreshGallery(uri: Uri) {
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(uri.path)
            val contentUri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            sendBroadcast(mediaScanIntent)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Handle share intents with attachment.
     */

    private var isFBAppFound = false
    private var isInstaAppFound = false
    private var isGmailAppFound = false


    private fun isSharePackageInstalled() {
        try {
            val pm = packageManager

            isFBAppFound = isPackageInstalled(PKG_FB, pm)
            isInstaAppFound = isPackageInstalled(PKG_INSTAGRAM, pm)
            isGmailAppFound = isPackageInstalled(PKG_GMAIL, pm)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun isPackageInstalled(packagename: String, packageManager: PackageManager): Boolean {
        try {
            packageManager.getPackageInfo(packagename, 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }

    interface DateSelection {
        fun onSelected(date: String)
    }

    var myCalendar = Calendar.getInstance()

    fun openCalendar(selection: DateSelection, format: String = ddMMyyyy) {

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat(format, Locale.US)

            selection.onSelected(sdf.format(myCalendar.time))
        }

        val dialog = DatePickerDialog(context, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH))

        dialog.datePicker.maxDate = Date().time
        dialog.show()
    }

    fun openCalendar(selection: DateSelection, fromDate: Long) {

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub

            view.minDate = fromDate

            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd-MMM-yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)

            selection.onSelected(sdf.format(myCalendar.time))
        }

        val dialog = DatePickerDialog(context, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH))

        dialog.datePicker.minDate = fromDate
        dialog.show()
    }

    fun showSnackBar(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }

    fun c2Long(value: String?): Long {
        var value = value
        if (value == null || value.equals("", ignoreCase = true)) {
            value = "0"
        }
        try {
            return java.lang.Long.parseLong(value)
        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        }
    }

    fun animateView(oldColor: AnimatedColor, viewsArray: Array<View>?, changeStatusBar: Boolean = true) {
        val animator = ObjectAnimator.ofFloat(0f, 1f).setDuration(500)
        animator.addUpdateListener {
            val v = it.animatedValue as Float
            if (changeStatusBar)
                window.statusBarColor = oldColor.with(v)
            viewsArray?.forEach {
                it.setBackgroundColor(oldColor.with(v))
            }

        }

        animator.start()
    }

    fun changeColor(startcolor: Int? = null, selectedColor: Int, arrayOfView: Array<View>? = null) {
        val oldColor = AnimatedColor(
                startcolor ?: resources.getColor(R.color.colorPrimaryDark), selectedColor)
        animateView(oldColor, arrayOfView)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    fun showSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    interface RevealAnimationListener {
        fun onCompleted()

        fun onStart()
    }

    var position = IntArray(2)
    var centerPoints = IntArray(2)

    fun revealViewAnimation(dialogView: View, b: Boolean, startX: Int, startY: Int,
                            animationListener: RevealAnimationListener?) {

        try {

            val w = startX
            val h = startY

            val endRadius = Math.hypot(w.toDouble(), h.toDouble()).toInt()

            if (b) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val revealAnimator = ViewAnimationUtils.createCircularReveal(dialogView, w, h, 0f, endRadius.toFloat())
                    revealAnimator.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            animationListener?.onStart()
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            animationListener?.onCompleted()
                        }
                    })

                    dialogView.visibility = View.VISIBLE
                    revealAnimator.duration = 1000
                    revealAnimator.start()
                } else {
                    dialogView.visibility = View.VISIBLE
                }
            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val anim = ViewAnimationUtils.createCircularReveal(dialogView, w, h, endRadius.toFloat(), 0f)

                    anim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            dialogView.visibility = View.INVISIBLE
                            animationListener?.onCompleted()
                        }

                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            animationListener?.onStart()
                        }
                    })
                    anim.duration = 1000
                    anim.start()
                } else {
                    dialogView.visibility = View.INVISIBLE
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getColorInt(colorName: Int): Int {
        return ContextCompat.getColor(context, colorName)
    }

    fun getDrawableInt(drawableName: Int): Drawable? {
        return ContextCompat.getDrawable(context, drawableName)
    }

    interface InternetDialogButtonListener {
        fun btnPositiveClicked()
        fun btnNegativeClicked()
    }

    fun noInternetConnectionDialog(internetDialogButtonListener: InternetDialogButtonListener? = null) {

        alert {
            title = resources.getString(R.string.error_no_internet)
            message = resources.getString(R.string.error_internet_down)

            positiveButton("Retry", {
                internetDialogButtonListener?.btnPositiveClicked()
            })

            negativeButton("Cancel", {
                internetDialogButtonListener?.btnNegativeClicked()
            })

            isCancelable = false

            show()
        }

    }

    fun customErrorDialog(heading: String, msg: String, internetDialogButtonListener: InternetDialogButtonListener? = null) {

        alert {
            title = heading
            message = msg

            positiveButton("Retry", {
                internetDialogButtonListener?.btnPositiveClicked()
            })

            negativeButton("Cancel", {
                internetDialogButtonListener?.btnNegativeClicked()
            })

            isCancelable = false

            show()
        }

    }

    fun createDirectory(subDir: String = "") {
        val invoiceDirectory = File(docPath + subDir)
        if (!invoiceDirectory.exists())
            invoiceDirectory.mkdirs()
    }

    fun deleteDirectory(docPath: String) {
        val invoiceDirectory = File(docPath + docPath)
        if (invoiceDirectory.exists())
            deleteRecursive(invoiceDirectory)
    }

    fun deleteRecursive(fileOrDirectory: File) {

        if (fileOrDirectory.isDirectory) {
            for (child in fileOrDirectory.listFiles()!!) {
                deleteRecursive(child)
            }
        }

        fileOrDirectory.delete()
    }

    fun highLight(msg: String) {
        Log.e("HighLight", msg)
    }

    fun getGradientDrawable(colors: Int): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.colors = intArrayOf(Color.BLACK, colors)
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.setStroke(20, getColorInt(R.color.white))
        gradientDrawable.cornerRadius = 10f

        return gradientDrawable
    }

    fun getMaterialDrawable(key: MaterialIcons, color: Int = R.color.white): IconDrawable {
        return IconDrawable(context, Iconify.findIconForKey(key.key()))
                .colorRes(color)
    }

    //TODO abstract methods
    abstract fun setContentView(): Int

    abstract fun init()

    abstract fun buttonClicks()
}