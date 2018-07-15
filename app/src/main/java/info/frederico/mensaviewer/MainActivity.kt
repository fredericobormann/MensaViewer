package info.frederico.mensaviewer

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import info.frederico.mensaviewer.helper.Mensa
import kotlinx.android.synthetic.main.activity_main.*
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var essensliste : List<String> = listOf("Essen1", "Essen2", "Essen3")

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

    private inner class UpdateMensaPlanTask : AsyncTask<Void, Void, List<String>>() {
        override fun doInBackground(vararg p0: Void?): List<String> {
            val tagsRegex = "<td.*description\"> |<br>|<img.+?>|</td>".toRegex()
            val bracketRegex = " \\u0028.+?\\u0029 ".toRegex()
            val nestedRegex = "&.+?\\u0029".toRegex()
            val undRegex = "&| +$".toRegex()
            val doc : Document = Jsoup.connect(mensa.url).get()
            val essen : Elements = doc.select(".dish-description")
            val essenBeschreibung : MutableList<String> = ArrayList<String>()
            for (e in essen){
                var essenString = tagsRegex.replace(e.toString(), "")
                essenString = bracketRegex.replace(essenString, "&")
                essenString = nestedRegex.replace(essenString, "")
                essenString = undRegex.replace(essenString, "")
                essenBeschreibung.add(essenString)
            }
            return essenBeschreibung
        }

        override fun onPreExecute() {
            super.onPreExecute()
            pb_mensaplan.visibility = View.VISIBLE;
        }

        override fun onPostExecute(result: List<String>) {
            if(viewAdapter is EssenAdapter){
                val v = viewAdapter as EssenAdapter
                v.setEssensplan(result)
            }
            pb_mensaplan.visibility = View.INVISIBLE
        }
    }

}
