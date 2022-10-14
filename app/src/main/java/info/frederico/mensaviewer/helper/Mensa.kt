package info.frederico.mensaviewer.helper

import android.view.View
import info.frederico.mensaviewer.R

/**
 * Created by fredd on 14.07.2018.
 */
enum class Mensa(val description : String, val icon : Int, val navigationViewId : Int, val number : Int) {
    ARMGARTSTRASSE("Armgartstraße", R.drawable.ic_baseline_a_24px, View.generateViewId(), 15),
    BERGEDORF("Bergedorf", R.drawable.ic_baseline_b_24px, View.generateViewId(), 1),
    BERLINERTOR("Berliner Tor", R.drawable.ic_baseline_b_24px, View.generateViewId(), 12),
    BLATTWERK("Blattwerk", R.drawable.ic_vegan, View.generateViewId(), 42),
    BOTANISCHERGARTEN("Botanischer Garten", R.drawable.ic_baseline_local_florist_24px, View.generateViewId(), 16),
    BLS("Bucerius-Law-School", R.drawable.ic_baseline_gavel_24px, View.generateViewId(), 2),
    CAFEALEX("Café Alexanderstraße", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 17),
    CAFEBERLINER("Café Berliner Tor", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 3),
    CAFECAMPUSBLICK("Café CampusBlick", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 22),
    CAFECANELA("Café Canela", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 23),
    CAFECFEL("Café CFEL", R.drawable.ic_cfel_icon, View.generateViewId(), 4),
    CAFEDELLARTE("Café dell´Arte", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 28),
    CAFEFINKENAU("Café Finkenau", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 18),
    CAFEGRINDEL("Café Grindel", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 24),
    CAFEHCU("Café HCU", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 25),
    CAFEINSGBOT("Café insgrüne Botanischer Garten", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 29),
    CAFEINSGHARBURG("Café insgrüne Harburg", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 30),
    CAFEJUNG("Café Jungiusstraße", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 5),
    CAFEMITTEL("Café Mittelweg", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 19),
    CAFESTUDAFF("Café Student Affairs", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 26),
    CAFEZESSP("Café ZessP", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 27),
    CAFEUEBERSEERING("Café Überseering", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 31),
    CAFESHOPBLUEBERRY("Café-Shop Blueberry", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 32),
    CAFESHOPCAMPUS("Café-Shop Campus", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 33),
    CAFESHOPGEOM("Café-Shop Geomatikum", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 34),
    CAFESHOPZAHNR("Café-Shop Zahnrad", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 35),
    CAMPUS("Campus", R.drawable.ic_baseline_account_balance_24px, View.generateViewId(), 6),
    CAMPUSFOODTRUCK("Campus Food Truck", R.drawable.ic_baseline_bus_24px, View.generateViewId(), 36),
    CAPUSCAFE("CampusCafé", R.drawable.ic_local_cafe_black_24dp, View.generateViewId(), 37),
    DESY("DESY-Kantine", R.drawable.ic_atom, View.generateViewId(), 14),
    FINKENAU("Finkenau", R.drawable.ic_baseline_f_24px, View.generateViewId(), 18),
    GEOMATIKUM("Geomatikum", R.drawable.ic_baseline_domain_48px, View.generateViewId(), 7),
    HARBURG("Harburg", R.drawable.ic_baseline_h_24px, View.generateViewId(), 8),
    PIZZABARHARBURG("PizzaBar Harburg", R.drawable.ic_baseline_local_pizza_24px, View.generateViewId(), 39),
    SCHLUETERSPIZZA("Schlüters (Pizza & More)", R.drawable.ic_baseline_local_pizza_24px, View.generateViewId(), 40),
    HCU("HCU", R.drawable.ic_baseline_h_24px, View.generateViewId(), 20),
    INFORMATIKUM("Informatikum", R.drawable.ic_baseline_computer_24px, View.generateViewId(), 10),
    STUDIERENDENHAUS("Studierendenhaus", R.drawable.ic_home_black_24dp, View.generateViewId(), 11),
    VERKAUFSWAGENMLK("Verkaufswagen am Martin-Luther-King-Platz", R.drawable.ic_baseline_bus_24px, View.generateViewId(), 41),
    UEBERSEE("Überseering", R.drawable.ic_baseline_ue_24px, View.generateViewId(), 13);

    val urlToday : String
    get() = "https://mensa.mafiasi.de/api/canteens/" + number + "/today/"
    val urlNextDay : String
    get() = "https://mensa.mafiasi.de/api/canteens/" + number + "/tomorrow/"
}