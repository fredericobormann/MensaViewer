package info.frederico.mensaviewer

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import info.frederico.mensaviewer.helper.Mensa
import kotlinx.android.synthetic.main.activity_main.*
import android.os.AsyncTask
import android.view.View
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


class MainActivity : AppCompatActivity() {

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

        UpdateMensaPlanTask().execute()
    }

    private inner class UpdateMensaPlanTask : AsyncTask<Void, Void, List<String>>() {
        override fun doInBackground(vararg p0: Void?): List<String> {
            val tagsRegex = "<td.*description\"> |<br>|<img.+?>|</td>".toRegex()
            val bracketRegex = " \\u0028.+?\\u0029 ".toRegex()
            val nestedRegex = "&.+?\\u0029".toRegex()
            val undRegex = "&| +$".toRegex()
            val doc : Document = Jsoup.connect("https://speiseplan.studierendenwerk-hamburg.de/de/310/2018/99/").get()
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
            message.setText(result.toString())
            pb_mensaplan.visibility = View.INVISIBLE
        }
    }

}
