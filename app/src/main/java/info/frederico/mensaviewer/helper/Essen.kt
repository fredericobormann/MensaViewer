package info.frederico.mensaviewer.helper
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon

data class Essen(
        @Json(name = "dish")
        val bezeichnung: String,
        @Json(name = "price")
        val studentenPreis: String,
        @Json(name = "price_staff")
        val bedienstetePreis: String){
}