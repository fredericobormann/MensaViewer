package info.frederico.mensaviewer.helper

import java.sql.Time
import com.beust.klaxon.Json


class OpeningTimes(val initialData: HashMap<Wochentag, Time> = HashMap()) {
    val data : HashMap<Wochentag, Time> = initialData;
}

enum class Wochentag {
    MONTAG,
    DIENSTAG,
    MITTWOCH,
    DONNERSTAG,
    FREITAG,
    SAMSTAG,
    SONNTAG;
}
