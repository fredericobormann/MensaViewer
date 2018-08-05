package info.frederico.mensaviewer.helper

import android.support.v7.content.res.AppCompatResources.getDrawable
import android.view.View
import info.frederico.mensaviewer.R

/**
 * Created by fredd on 14.07.2018.
 */
enum class Mensa(val description : String, val icon : Int, val navigationViewId : Int, val number : Int) {
    STUDIERENDENHAUS("Studierendenhaus", R.drawable.ic_home_black_24dp, View.generateViewId(), 310),
    INFORMATIKUM("Informatikum", R.drawable.ic_baseline_computer_24px, View.generateViewId(), 580),
    CAMPUS("Campus", R.drawable.ic_baseline_account_balance_24px, View.generateViewId(), 340),
    HARBURG("Harburg", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 570),
    ARMGARTSTRASSE("Armgartstraße", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 590),
    BERGEDORF("Bergedorf", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 520),
    BERLINERTOR("Berliner Tor", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 530),
    BOTANISCHERGARTEN("Botanischer Garten", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 560),
    BLS("Bucerius-Law-School", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 410),
    CAFEALEX("Café Alexanderstraße", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 660),
    CAFEMITTEL("Café Mittelweg", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 690),
    CAFEBERLINER("Café Berliner Tor", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 531),
    CAFECFEL("Café CFEL", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 680),
    CAFEJUNG("Café Jungiusstraße", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 610),
    FINKENAU("Finkenau", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 420),
    GEOMATIKUM("Geomatikum", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 540),
    HCU("HCU", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 430),
    UEBERSEE("überseering", R.drawable.ic_dashboard_black_24dp, View.generateViewId(), 380);

    val url : String
    get() = "https://speiseplan.studierendenwerk-hamburg.de/de/" + number + "/2018/99/"
}