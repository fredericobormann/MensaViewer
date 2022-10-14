package info.frederico.mensaviewer.helper
import com.beust.klaxon.Json

@Target(AnnotationTarget.FIELD)
annotation class KlaxonPrice

data class Essen @JvmOverloads constructor(
        @Json(name = "dish")
        val bezeichnung: String,
        @Json(name = "price")@KlaxonPrice
        val studentenPreis: String,
        @Json(name = "price_staff")@KlaxonPrice
        val bedienstetePreis: String,
        @Json(name = "date")
        val date: String = "",
        @Json(name = "vegetarian")
        val vegetarian: Boolean,
        @Json(name = "vegan")
        val vegan: Boolean): ViewableEssenElement