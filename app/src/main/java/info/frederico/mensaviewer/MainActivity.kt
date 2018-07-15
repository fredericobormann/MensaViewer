package info.frederico.mensaviewer

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import info.frederico.mensaviewer.helper.Mensa
import kotlinx.android.synthetic.main.activity_main.*
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import info.frederico.mensaviewer.helper.Essen
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var essensliste : List<Essen> = ArrayList<Essen>()

    private var mensa = Mensa.STUDIERENDENHAUS

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        viewManager = LinearLayoutManager(this)
        viewAdapter = EssenAdapter(essensliste)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter}
        UpdateMensaPlanTask().execute()
    }

    private inner class UpdateMensaPlanTask : AsyncTask<Void, Void, List<Essen>>() {
        override fun doInBackground(vararg p0: Void?): List<Essen> {
            val tagsRegex = "<td.*description\"> |<br>|<img.+?>|</td>".toRegex()
            val bracketRegex = " \\u0028.+?\\u0029 ".toRegex()
            val nestedRegex = "&.+?\\u0029".toRegex()
            val undRegex = "&| +$".toRegex()
            val preisRegex = "\\d+,\\d{2}".toRegex()
            val doc : Document = Jsoup.connect(mensa.url).get()
            val essen : Elements = doc.select(".dish-description")
            val preis : Elements = doc.select("tr")
            val essenBeschreibung : MutableList<Essen> = ArrayList<Essen>()
            for (e in essen.withIndex()){
                var essenString = tagsRegex.replace(e.value.toString(), "")
                essenString = bracketRegex.replace(essenString, "&")
                essenString = nestedRegex.replace(essenString, "")
                essenString = undRegex.replace(essenString, "")
                var preisString = preisRegex.find(preis[e.index+1].toString())!!.value
                essenBeschreibung.add(Essen(essenString, preisString))
            }
            return essenBeschreibung
        }

        override fun onPreExecute() {
            super.onPreExecute()
            pb_mensaplan.visibility = View.VISIBLE;
        }

        override fun onPostExecute(result: List<Essen>) {
            if(viewAdapter is EssenAdapter){
                val v = viewAdapter as EssenAdapter
                v.setEssensplan(result)
            }
            pb_mensaplan.visibility = View.INVISIBLE
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
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
