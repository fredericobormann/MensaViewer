package info.frederico.mensaviewer.helper

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.AsyncTask
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import okhttp3.OkHttpClient
import okhttp3.Request
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
            var essenBeschreibung: List<Essen> = ArrayList<Essen>()
            val client = OkHttpClient()

            loadingMensa = param[0]

            if(loadingMensa == null){
                return null
            }

            try {
                val request = Request.Builder()
                        .url(loadingMensa!!.url)
                        .build()
                client.newCall(request).execute().use {
                    essenBeschreibung = Klaxon().parseArray<Essen>(it.body()!!.string())!!
                }
            } catch (e: SocketTimeoutException) {
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