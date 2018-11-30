package info.frederico.mensaviewer.helper

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.SocketTimeoutException

class EssenViewModel : ViewModel() {
    private lateinit var essen: MutableLiveData<List<Essen>>
    lateinit var mensa: Mensa

    fun getEssen(): LiveData<List<Essen>> {
        if (!::essen.isInitialized) {
            essen = MutableLiveData()
            loadEssen()
        }
        return essen
    }

    private fun loadEssen() {
        val tagsRegex = "<[^>]+>".toRegex()
        val bracketRegex = " \\(.+?\\) ?".toRegex()
        val allergenRegex = "([^,]+) \\((.+?)\\)".toRegex()
        val starRegex = "\\*\\*\\*.*?\\*\\*\\*".toRegex()
        val preisRegex = "\\d+,\\d{2}".toRegex()
        val essenBeschreibung: MutableList<Essen> = ArrayList<Essen>()

        try {
            val doc: Document = Jsoup.connect(mensa.url).get()
            val essen: Elements = doc.select(".dish-description")
            val preis: Elements = doc.select(".price")

            for (e in essen.withIndex()) {
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

                essenBeschreibung?.add(Essen(essenString, allergenMap, studentenPreis, bedienstetePreis, gaestePreis))
            }
        } catch (e: SocketTimeoutException) {
            throw EssenLoadingException()
        } catch (e: HttpStatusException) {
            throw EssenLoadingException()
        }
        essen.value = essenBeschreibung
    }
}