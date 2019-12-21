package info.frederico.mensaviewer.helper

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import android.os.AsyncTask
import android.preference.PreferenceManager
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import info.frederico.mensaviewer.MensaViewer
import info.frederico.mensaviewer.R
import info.frederico.mensaviewer.helper.json.OpeningTimesConverter
import info.frederico.mensaviewer.helper.json.PriceConverter
import info.frederico.mensaviewer.helper.json.WeekdayConverter
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.SocketTimeoutException

class EssenViewModel : ViewModel() {
    val essen: MutableLiveData<Essensplan> by lazy {
        MutableLiveData<Essensplan>()
    }
    val pref = PreferenceManager.getDefaultSharedPreferences(MensaViewer.mInstance)
    var mensa: Mensa?
        get(){
            val mensaName = pref.getString(MensaViewer.res.getString(R.string.pref_selected_mensa), null)
            return if(mensaName == null){
                null
            } else {
                Mensa.valueOf(mensaName)
            }
        }
    set(newMensa) {
        if(mensa != newMensa && newMensa != null){
            pref.edit().putString(MensaViewer.res.getString(R.string.pref_selected_mensa), newMensa.toString()).apply()
        }
        if(mMensaplanCache[newMensa] != null){
            essen.value = mMensaplanCache[newMensa]
        } else {
            forceReload()
        }
    }
    val mMensaplanCache = HashMap<Mensa, Essensplan?>()

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
        UpdateOpeningTimes().execute(mensa)
    }

    /**
     * Asynchronous Task to load Mensa data from internet.
     * mensa LiveData will be null, if an error occurred.
     */
    private inner class UpdateMensaPlan(): AsyncTask<Mensa?, Unit, Essensplan?>(){
        var loadingMensa: Mensa? = null
        override fun doInBackground(vararg param: Mensa?): Essensplan?{
            var essensplan: Essensplan?
            var essensplanToday: List<Essen>?
            var essensplanNextDay: List<Essen>?

            loadingMensa = param[0]

            if(loadingMensa == null){
                return null
            }

            try {
                essensplanToday = getEssenslisteByUrl(loadingMensa!!.urlToday)
                essensplanNextDay = getEssenslisteByUrl(loadingMensa!!.urlNextDay)
            } catch (e: SocketTimeoutException) {
                return null
            } catch (e: Exception) {
                return null
            }
            return Essensplan(essensplanToday, essensplanNextDay)
        }

        fun getEssenslisteByUrl(url: String): List<Essen>?{
            val client = OkHttpClient()
            val priceConverter = PriceConverter()

            val request = Request.Builder()
                    .url(url)
                    .build()
            client.newCall(request).execute().use {
                return Klaxon().fieldConverter(KlaxonPrice::class, priceConverter).parseArray<Essen>(it.body()?.string() ?: "")
            }
        }

        override fun onPostExecute(result: Essensplan?) {
            super.onPostExecute(result)
            if(mensa == loadingMensa){
                essen.value = result
            }
            mMensaplanCache[loadingMensa!!] = result
        }
    }

    private inner class UpdateOpeningTimes(): AsyncTask<Mensa?, Unit, List<OpeningTime>?>(){
        var loadingMensa: Mensa? = null
        override fun doInBackground(vararg param: Mensa?): List<OpeningTime>? {
            if(param[0] == null){
                return null
            }

            loadingMensa = param[0]
            val url = loadingMensa!!.urlInfo
            val client = OkHttpClient()
            val openingTimesConverter = OpeningTimesConverter()
            val weekdayConverter = WeekdayConverter()
            val parser = Parser.default()
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use {
                val stringBuilder = StringBuilder(it.body()?.string() ?: "")
                val jsonObject = parser.parse(stringBuilder) as JsonObject
                val jsonArray = jsonObject.array<OpeningTime>("opening_times")
                return jsonArray?.let { it1 ->
                    Klaxon()
                            .fieldConverter(KlaxonWeekday::class, weekdayConverter)
                            .fieldConverter(KlaxonOpeningTime::class, openingTimesConverter)
                            .parseFromJsonArray(it1)
                }
            }
        }

        override fun onPostExecute(result: List<OpeningTime>?) {
            super.onPostExecute(result)

            if(result != null){
                loadingMensa?.openingTimes = OpeningTimes(result)
            }
        }
    }
}