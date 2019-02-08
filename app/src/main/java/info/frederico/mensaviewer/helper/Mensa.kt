package info.frederico.mensaviewer.helper

import android.support.v7.content.res.AppCompatResources.getDrawable
import android.view.View
import info.frederico.mensaviewer.R

/**
 * Created by fredd on 14.07.2018.
 */
enum class Mensa(val description : String, val icon : Int, val navigationViewId : Int, val number : Int) {
    ARMGARTSTRASSE("Armgartstraße", R.drawable.ic_baseline_a_24px, View.generateViewId(), 15),
    BERGEDORF("Bergedorf", R.drawable.ic_baseline_b_24px, View.generateViewId(), 1),
    BERLINERTOR("Berliner Tor", R.drawable.ic_baseline_b_24px, View.generateViewId(), 12),
    BOTANISCHERGARTEN("Botanischer Garten", R.drawable.ic_baseline_b_24px, View.generateViewId(), 16),
    BLS("Bucerius-Law-School", R.drawable.ic_baseline_b_24px, View.generateViewId(), 2),
    CAFEALEX("Café Alexanderstraße", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 17),
    CAFEBERLINER("Café Berliner Tor", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 3),
    CAFECFEL("Café CFEL", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 4),
    CAFEJUNG("Café Jungiusstraße", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 5),
    CAFEMITTEL("Café Mittelweg", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 19),
    CAMPUS("Campus", R.drawable.ic_baseline_account_balance_24px, View.generateViewId(), 6),
    DESY("DESY-Kantine", R.drawable.ic_atom, View.generateViewId(), 14),
    FINKENAU("Finkenau", R.drawable.ic_baseline_f_24px, View.generateViewId(), 18),
    GEOMATIKUM("Geomatikum", R.drawable.ic_functions_black_24dp, View.generateViewId(), 7),
    HARBURG("Harburg", R.drawable.ic_baseline_h_24px, View.generateViewId(), 8),
    HCU("HCU", R.drawable.ic_baseline_h_24px, View.generateViewId(), 20),
    INFORMATIKUM("Informatikum", R.drawable.ic_baseline_computer_24px, View.generateViewId(), 10),
    STUDIERENDENHAUS("Studierendenhaus", R.drawable.ic_home_black_24dp, View.generateViewId(), 11),
    UEBERSEE("Überseering", R.drawable.ic_baseline_ue_24px, View.generateViewId(), 13);

    val url : String
    get() = "https://mensa.mafiasi.de/api/canteens/" + number + "/today/"
}