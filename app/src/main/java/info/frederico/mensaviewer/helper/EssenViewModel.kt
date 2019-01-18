package info.frederico.mensaviewer.helper

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.AsyncTask
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.SocketTimeoutException

class EssenViewModel : ViewModel() {
    val essen: MutableLiveData<List<Essen>> by lazy {
        MutableLiveData<List<Essen>>()
    }
    var mensa: Mensa? = null
    set(newMensa) {
        if(mensa != newMensa){
            field = newMensa
            if(mMensaplanCache[newMensa] != null){
                essen.value = mMensaplanCache[newMensa]
            } else {
                forceReload()
            }
        }
    }
    val mMensaplanCache = HashMap<Mensa, List<Essen>?>()

    /**
     * Notifies observers to refresh data, although dataset has not changed.
     */
    fun getData(){
        essen.value = essen.value
    }

    /**
     * Checks if cached data is available.
     */
    fun isCachedDataAvailable(m: Mensa):Boolean{
        return mMensaplanCache[m] != null
    }

    /**
     * Reload data, ignoring if Mensa has changed
     */
    fun forceReload() {
        UpdateMensaPlan().cancel(true)
        UpdateMensaPlan().execute(mensa)
    }

    /**
     * Asynchronous Task to load Mensa data from internet.
     * mensa LiveData will be null, if an error occurred.
     */
    private inner class UpdateMensaPlan(): AsyncTask<Mensa?, Unit, List<Essen>?>(){
        var loadingMensa: Mensa? = null
        override fun doInBackground(vararg param: Mensa?): List<Essen>?{
            val tagsRegex = "<[^>]+>".toRegex()
            val bracketRegex = " \\(.+?\\) ?".toRegex()
            val allergenRegex = "([^,]+) \\((.+?)\\)".toRegex()
            val starRegex = "\\*\\*\\*.*?\\*\\*\\*".toRegex()
            val preisRegex = "\\d+,\\d{2}".toRegex()
            val essenBeschreibung: MutableList<Essen> = ArrayList<Essen>()

            loadingMensa = param[0]

            if(loadingMensa == null){
                return null
            }

            try {
                val doc: Document = Jsoup.connect(loadingMensa!!.url).get()
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
                return null
            } catch (e: HttpStatusException) {
                return null
            } catch (e: Exception) {
                return null
            }
            return essenBeschreibung
        }

        override fun onPostExecute(result: List<Essen>?) {
            super.onPostExecute(result)
            if(mensa == loadingMensa){
                essen.value = result
            }
            mMensaplanCache[loadingMensa!!] = result
        }
    }
}