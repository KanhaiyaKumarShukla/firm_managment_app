package com.rach.firmmanagement.navigationDrawer

import androidx.annotation.DrawableRes
import com.rach.firmmanagement.R

sealed class Screen(
   val  title: String,
   val  route: String
) {

    sealed class BottomScreen(
        val bTitle : String,
        val bRoute :String,
        @DrawableRes val icon: Int
    ) : Screen(
        bTitle ,bRoute
    ){
        object Home : BottomScreen(
            "Home",
            "Home",
            R.drawable.home
        )

        object Profile : BottomScreen(
            "Profile","Profile",
            R.drawable.about
        )

        object Another : BottomScreen(
            "Other","Other",
            R.drawable.about_us
        )

    }

    sealed class DrawerScreen(
        val dTitle: String,
        val dRoute: String,
        @DrawableRes val icon: Int

    ) : Screen(
        dTitle, dRoute
    ) {

        // Privacy Policy share Rate the App About contact Us
        object MainHome : DrawerScreen(
            "Home",
            "Home",
            R.drawable.home
        )

        object Profile : DrawerScreen(
            "Profile",
            "Profile",
            R.drawable.about

        )

        object Settings : DrawerScreen(
            "Settings",
            "Settings",
            R.drawable.settings

        )



        object About : DrawerScreen(
            "About",
            "About",
            R.drawable.about_us
        )

        object ContactUs : DrawerScreen(
            "Contact Us",
            "Contact Us",
            R.drawable.support
        )

        object Share : DrawerScreen(
            "Share",
            "Share",
            R.drawable.baseline_share_24

        )

        object RatetheApp : DrawerScreen(
            "LogOut",
            "LogOut",
            R.drawable.logout
        )
    }
}

val screensInBottom = listOf(
    Screen.BottomScreen.Home,
    Screen.BottomScreen.Profile,
    Screen.BottomScreen.Another
)

val screenDrawerItemList = listOf(
    Screen.DrawerScreen.MainHome,
    Screen.DrawerScreen.Profile,
    Screen.DrawerScreen.Settings,
    Screen.DrawerScreen.About,
    Screen.DrawerScreen.ContactUs,
    Screen.DrawerScreen.Share,
    Screen.DrawerScreen.RatetheApp,
)