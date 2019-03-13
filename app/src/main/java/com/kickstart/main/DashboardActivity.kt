package com.kickstart.main

import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.kickstart.R
import com.kickstart.adapter.SideMenuAdapter
import com.kickstart.constants.getMenuItems
import com.kickstart.data.Bean.WSQuestionsBean
import com.kickstart.utils.RecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.custom_actionbar.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.async


// Created by Babul Patel on 25/5/18.

class DashboardActivity : BaseActivity() {

    var toggle = false
    lateinit var mDrawerToggle: ActionBarDrawerToggle
    lateinit var sideMenuAdapter: SideMenuAdapter

    override fun setContentView(): Int {
        return R.layout.activity_dashboard
    }

    override fun init() {

        mDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, titleLay.toolbar,
                0, 0) {
        }

        drawer_layout.addDrawerListener(mDrawerToggle)
        setToolbar("Dashboard", false, titleLay)

        sideMenuAdapter = SideMenuAdapter(context)
        navDrawerRecycler.layoutManager = LinearLayoutManager(context)
        navDrawerRecycler.adapter = sideMenuAdapter
        sideMenuAdapter.submitList(getMenuItems())

        GlobalScope.launch(Dispatchers.Main) {
            val result = suspendedFunctionTest()
            toast(result.toString())
        }

        textView.setOnClickListener {

            warn { "Warning" }
            info { "Information" }
            error { "Error" }
            wtf("No idea")
            verbose { "Verbose" }
            debug { "Debug message" }

        }

    }

    override fun buttonClicks() {

        titleLay.btnMenuIcon.setOnClickListener {
            drawerToggle()
        }

        navDrawerRecycler.addOnItemTouchListener(RecyclerItemClickListener(context, navDrawerRecycler, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                drawerToggle()

                GlobalScope.launch {
                    delay(200)

                    when (position) {
                        0 -> {// Profile
                        }
                        1 -> {// Logout
                        }
                        2 -> {
                        }
                        3 -> { //Add user
                        }
                    }
                }

            }

            override fun onLongItemClick(view: View?, position: Int) {
            }

        }))

    }

    fun drawerToggle() {
        if (drawer_layout.isDrawerVisible(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    suspend fun suspendedFunctionTest(): Long {

        val bean = WSQuestionsBean(
                null,
                "Question1",
                0,
                0
        )

        return wordsViewModel.addItems(bean)

    }
}