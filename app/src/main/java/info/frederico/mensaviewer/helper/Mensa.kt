package info.frederico.mensaviewer.helper

import android.support.v7.content.res.AppCompatResources.getDrawable
import android.view.View
import info.frederico.mensaviewer.R

/**
 * Created by fredd on 14.07.2018.
 */
enum class Mensa(val description : String, val icon : Int, val id : Int, val url : String) {
    STUDIERENDENHAUS("Studierendenhaus", R.drawable.ic_home_black_24dp, View.generateViewId(), "https://speiseplan.studierendenwerk-hamburg.de/de/310/2018/0/"),
    INFORMATIKUM("Informatikum", R.drawable.ic_baseline_computer_24px, View.generateViewId(), "https://speiseplan.studierendenwerk-hamburg.de/de/580/2018/0/"),
    CAMPUS("Campus", R.drawable.ic_baseline_account_balance_24px, View.generateViewId(), "https://speiseplan.studierendenwerk-hamburg.de/de/340/2018/0/"),
    HARBURG("Harburg", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), "https://speiseplan.studierendenwerk-hamburg.de/de/570/2018/0/")
}