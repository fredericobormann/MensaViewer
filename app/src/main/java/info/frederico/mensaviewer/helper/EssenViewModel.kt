package info.frederico.mensaviewer.helper

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.util.Log
import com.beust.klaxon.*
import info.frederico.mensaviewer.MensaViewer
import info.frederico.mensaviewer.R
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
            val priceConverter = object: Converter{
                override fun canConvert(cls: Class<*>): Boolean {
                    return cls == String::class.java
                }

                override fun fromJson(jv: JsonValue): Any? {
                    val jsonString: String? = jv.string
                    if (jsonString != null){
                        return jsonString.replace(".", ",")+" €"
                    }
                    else{
                        return null
                    }
                }

                override fun toJson(value: Any): String {
                    return ""
                }
            }

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

    private inner class UpdateOpeningTimes(): AsyncTask<Mensa?, Unit, String>(){
        override fun doInBackground(vararg param: Mensa?): String {
            if(param[0] == null){
                return ""
            }

            val url = param[0]!!.urlInfo
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use {
                return it.body()?.string() ?: ""
            }
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            mensa?.openingTimes = result
            Log.d("TEST!!!", result)
        }
    }
}