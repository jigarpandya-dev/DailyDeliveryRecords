package com.app.dailydeliveryrecords.ui.bottomnav

import com.app.dailydeliveryrecords.R

sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){

    object Home : BottomNavItem("Home", R.drawable.ic_home,"home")
    object Monthly: BottomNavItem("Monthly",R.drawable.ic_month_wise,"monthly")
    object Receipts: BottomNavItem("Receipts",R.drawable.ic_receipt,"receipts")
    object Setting: BottomNavItem("Setting",R.drawable.ic_setting,"setting")

}