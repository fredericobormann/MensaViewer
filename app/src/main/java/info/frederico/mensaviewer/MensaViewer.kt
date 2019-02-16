package info.frederico.mensaviewer
import android.app.Application
import android.content.res.Resources

class MensaViewer : Application(){
    companion object {
        lateinit var mInstance: Application
        lateinit var res: Resources
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        res = resources
    }
}