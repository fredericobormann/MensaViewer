package info.frederico.mensaviewer.helper

import android.os.Build
import android.text.Html

class HtmlHelper {
    companion object {
        @Suppress("DEPRECATION")
        fun fromHtmltoString(html : String) : String{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
            } else {
                return Html.fromHtml(html).toString();
            }
        }
    }
}