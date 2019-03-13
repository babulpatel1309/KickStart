package com.kickstart.main.Fragments

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kickstart.ApplicationClass
import com.kickstart.R
import com.kickstart.main.BaseActivity
import com.kickstart.utils.AnimatedColor
import com.kickstart.utils.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BaseFragment : Fragment() {

    /*
    * Lateinit vars*/
    lateinit var mContext: Context
    lateinit var currentView: View
    lateinit var currentFragment: Fragment
    lateinit var prefs: Prefs
    lateinit var baseActivity: BaseActivity

    /*
    * Initialized vars*/


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentView = inflater.inflate(setContentView(), container, false)!!
        baseActivity = activity as BaseActivity

        prefs = ApplicationClass.mInstance.getPrefs()!!

        init()
        buttonClicks()
        setFragment()

        return currentView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mContext = context!!
    }

    fun setFragment() {

        Handler().postDelayed(object : Runnable {
            override fun run() {
                val fragments = (context as BaseActivity).getLocalFragmentManager().fragments
                for (i in 0 until fragments.size) {
                    if (fragments[i].isVisible) {
                        currentFragment = fragments[i]
                    }
                }
            }
        }, 300)
    }

    fun removeFragment() {
        GlobalScope.launch(Dispatchers.Main) {
            changeColor()
            try {
                hideKeyboard()
                baseActivity.removeFragment(currentFragment)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun hideKeyboard() {
        baseActivity.hideKeyboard()
    }

    fun go(cls: Class<*>) {
        baseActivity.go(cls)
    }

    fun goWithFinish(cls: Class<*>) {
        baseActivity.goWithFinish(cls)
    }

    fun goWithFinish(cls: Class<*>, bundle: Bundle) {
        baseActivity.goWithFinish(cls, bundle)
    }

    fun go(cls: Class<*>, bundle: Bundle?) {
        baseActivity.go(cls, bundle)
    }

    fun goWithSerializable(cls: Class<*>, list: ArrayList<*>) {
        baseActivity.goWithSerializable(cls, list)
    }

    fun goWithSerializableForResult(cls: Class<*>, list: ArrayList<*>) {
        baseActivity.goWithSerializableForResult(cls, list)
    }

    fun goWithResult(cls: Class<*>, picker: Int) {
        baseActivity.goWithResult(cls, picker)
    }

    fun askPermission(context: Context = this.context!!, permissionListener: BaseActivity.PermissionListener, type: Int) {
        baseActivity.askPermission(context, permissionListener, type)
    }

    fun addFragment(newFragment: Fragment) {
        baseActivity.addFragment(newFragment)
    }

    fun replaceFragment(newFragment: Fragment) {
        baseActivity.replaceFragment(newFragment)
    }

    fun setToolbar(title: String, isBack: Boolean = false, toolbar: Toolbar) {
        baseActivity.setToolbar(title, isBack, toolbar)
    }

    fun showProgressDialog() {
        baseActivity.showProgressDialog()
    }

    fun hideProgressDialog() {
        baseActivity.hideProgressDialog()
    }

    fun getCurrentTimeStamp(): Long {
        return System.currentTimeMillis() / 1000
    }

    fun openCalendar(selection: BaseActivity.DateSelection, fromDate: Long) {
        baseActivity.openCalendar(selection, fromDate)
    }

    fun showSnackBar(view: View, msg: String) {
        baseActivity.showSnackBar(view, msg)
    }

    fun animateView(oldColor: AnimatedColor, viewsArray: Array<View>, changeStatusBar: Boolean = true) {
        baseActivity.animateView(oldColor, viewsArray, changeStatusBar)
    }

    fun changeColor(startcolor: Int? = null, selectedColor: Int = resources.getColor(R.color.colorPrimaryDark), arrayOfView: Array<View>? = null) {
        baseActivity.changeColor(startcolor, selectedColor, arrayOfView)
    }

    fun getDrawableInt(drawableName: Int): Drawable? {
        return baseActivity.getDrawableInt(drawableName)
    }

    //TODO abstract methods
    abstract fun setContentView(): Int

    abstract fun init()

    abstract fun buttonClicks()

}