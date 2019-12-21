package info.frederico.mensaviewer.helper
import com.beust.klaxon.Json
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


data class OpeningTimes(val data: List<OpeningTime> = ArrayList()){
    override fun toString(): String {
        val timeFormatter = SimpleDateFormat("HH:mm")
        return data.map {
            "${it.weekday}: ${timeFormatter.format(it.start)} - ${timeFormatter.format(it.end)} Uhr"
        }.joinToString(separator="\n")
    }
}

@Target(AnnotationTarget.FIELD)
annotation class KlaxonOpeningTime
@Target(AnnotationTarget.FIELD)
annotation class KlaxonWeekday

data class OpeningTime @JvmOverloads constructor(
        @Json(name = "weekday")@KlaxonWeekday
        val weekday : Wochentag,
        @Json(name = "start")@KlaxonOpeningTime
        val start : Date,
        @Json(name = "end")@KlaxonOpeningTime
        val end : Date
)

enum class Wochentag {
    MONTAG,
    DIENSTAG,
    MITTWOCH,
    DONNERSTAG,
    FREITAG,
    SAMSTAG,
    SONNTAG;

    override fun toString(): String {
        return this.name.toLowerCase().capitalize()
    }
}
