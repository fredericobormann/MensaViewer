package info.frederico.mensaviewer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import info.frederico.mensaviewer.helper.Essen
import info.frederico.mensaviewer.helper.Mensa
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.SocketTimeoutException


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var essensliste : List<Essen> = ArrayList<Essen>()
    private lateinit var prefListener: SharedPreferences.OnSharedPreferenceChangeListener

    private var mensa = Mensa.STUDIERENDENHAUS

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        recyclerView.visibility = View.INVISIBLE
        when (item.itemId) {
            R.id.navigation_studierendenhaus -> {
                mensa = Mensa.STUDIERENDENHAUS
                UpdateMensaPlanTask().execute()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_informatikum -> {
                mensa = Mensa.INFORMATIKUM
                UpdateMensaPlanTask().execute()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_campus -> {
               mensa = Mensa.CAMPUS
                UpdateMensaPlanTask().execute()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private val mOnNavigationItemReselectedListener = BottomNavigationView.OnNavigationItemReselectedListener {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.setOnNavigationItemReselectedListener(mOnNavigationItemReselectedListener)

        viewManager = LinearLayoutManager(this)
        viewAdapter = EssenAdapter(essensliste, this)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter}

        swipe_container.setColorSchemeColors(getColor(R.color.colorAccent), getColor(R.color.colorPrimary), getColor(R.color.secondaryLightColor))

        swipe_container.setOnRefreshListener {
            UpdateMensaPlanTask().execute()
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        prefListener = SharedPreferences.OnSharedPreferenceChangeListener{prefs, key ->
            sharedPreferencesChanged(key)
        }
        preferences.registerOnSharedPreferenceChangeListener(prefListener)

        UpdateMensaPlanTask().execute()
    }

    private fun sharedPreferencesChanged(key: String?) {
        when(key ?: ""){
            getString(R.string.pref_usergroup) -> {
                    recyclerView.invalidate()
                    viewAdapter.notifyDataSetChanged()
            }
        }
    }

    private inner class UpdateMensaPlanTask : AsyncTask<Void, Void, List<Essen>>() {
        override fun doInBackground(vararg p0: Void?): List<Essen> {
            val tagsRegex = "<[^>]+>".toRegex()
            val bracketRegex = " \\(.+?\\) ?".toRegex()
            val allergenRegex = "([^,]+) \\((.+?)\\)".toRegex()
            val starRegex = "\\*\\*\\*.*?\\*\\*\\*".toRegex()
            val preisRegex = "\\d+,\\d{2}".toRegex()
            val essenBeschreibung : MutableList<Essen> = ArrayList<Essen>()

            try {
                val doc : Document = Jsoup.connect(mensa.url).get()
                val essen : Elements = doc.select(".dish-description")
                val preis : Elements = doc.select(".price")

                for (e in essen.withIndex()){
                    var essenString = tagsRegex.replace(e.value.text(), "")

                    var allergenMap = hashMapOf<String, List<String>>()
                    for (match in allergenRegex.findAll(essenString)) {
                        var ingredient = starRegex.replace(match.groupValues[1], "").trim()
                        var allergenList = match.groupValues[2].split(", ")
                        allergenMap[ingredient] = allergenList
                    }

                    essenString = bracketRegex.replace(essenString, "").trim()

                    var studentenPreis = preisRegex.find(preis[e.index * 3].text())?.value + "\u202f€" ?: ""
                    var bedienstetePreis = preisRegex.find(preis[e.index * 3 + 1].text())?.value + "\u202f€" ?: ""
                    var gaestePreis = preisRegex.find(preis[e.index * 3 + 2].text())?.value + "\u202f€" ?: ""

                    essenBeschreibung.add(Essen(essenString, allergenMap, studentenPreis, bedienstetePreis, gaestePreis))
                }
            }
            catch (e : SocketTimeoutException){
                cancel(true)
            }
            return essenBeschreibung
        }

        override fun onPreExecute() {
            super.onPreExecute()
            swipe_container.isRefreshing = true
            val cm = this@MainActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnected == true
            if(!isConnected){
                cancel(true)
            }
            tv_error_message_internet.visibility = View.INVISIBLE
        }

        override fun onPostExecute(result: List<Essen>) {
            if(result.isEmpty())
            {
                showNoDataMessage()
            }
            else{
                if(viewAdapter is EssenAdapter){
                    val v = viewAdapter as EssenAdapter
                    v.setEssensplan(result)
                }
                swipe_container.isRefreshing = false
                my_recycler_view.visibility = View.VISIBLE
            }
        }

        override fun onCancelled() {
            showInternetConnectionMessage()
            super.onCancelled()
        }

        private fun showInternetConnectionMessage() {
            tv_error_message_internet.text = getString(R.string.error_message_internet)
            my_recycler_view.visibility = View.INVISIBLE
            swipe_container.isRefreshing = false
            tv_error_message_internet.visibility = View.VISIBLE
        }

        private fun showNoDataMessage() {
            tv_error_message_internet.text = getString(R.string.error_message_data)
            my_recycler_view.visibility = View.INVISIBLE
            swipe_container.isRefreshing = false
            tv_error_message_internet.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuinflater = menuInflater
        menuinflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item != null) {
            when (item.itemId) {
                R.id.licenses -> {
                    val intent = Intent(this, LicenseActivity::class.java)
                    startActivity(intent)
                    return true
                }
                R.id.prefs -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}
