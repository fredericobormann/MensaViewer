package info.frederico.mensaviewer.helper
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon

@Target(AnnotationTarget.FIELD)
annotation class KlaxonPrice

data class Essen @JvmOverloads constructor(
        @Json(name = "dish")
        val bezeichnung: String,
        @Json(name = "price")@KlaxonPrice
        val studentenPreis: String,
        @Json(name = "price_staff")
        val bedienstetePreis: String){
}