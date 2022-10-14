package info.frederico.mensaviewer.helper

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.AsyncTask
import android.preference.PreferenceManager
import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.Klaxon
import info.frederico.mensaviewer.MensaViewer
import info.frederico.mensaviewer.R
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.SocketTimeoutException

class EssenViewModel : ViewModel() {
    val essen: MutableLiveData<Essensplan> by lazy {
        MutableLiveData<Essensplan>()
    }
    private val pref = PreferenceManager.getDefaultSharedPreferences(MensaViewer.mInstance)!!
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
    }

    /**
     * Asynchronous Task to load Mensa data from internet.
     * mensa LiveData will be null, if an error occurred.
     */
    @SuppressLint("StaticFieldLeak")
    private inner class UpdateMensaPlan : AsyncTask<Mensa?, Unit, Essensplan?>(){
        var loadingMensa: Mensa? = null
        override fun doInBackground(vararg param: Mensa?): Essensplan?{
            val essensplanToday: List<Essen>?
            val essensplanNextDay: List<Essen>?

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
                    return if (jsonString != null){
                        jsonString.replace(".", ",")+" €"
                    } else{
                        null
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
                return Klaxon().fieldConverter(KlaxonPrice::class, priceConverter).parseArray(it.body.string())
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
}