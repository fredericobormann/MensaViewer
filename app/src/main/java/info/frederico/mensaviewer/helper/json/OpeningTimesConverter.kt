package info.frederico.mensaviewer.helper.json

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import info.frederico.mensaviewer.helper.Wochentag
import java.text.SimpleDateFormat
import java.util.*

class OpeningTimesConverter : Converter{
    override fun fromJson(jv: JsonValue): Date? {
        val jsonString : String? = jv.string
        val dateTimeFormatter = SimpleDateFormat("HH:mm:ss", Locale.GERMAN)
        return dateTimeFormatter.parse(jsonString)
    }

    override fun toJson(value: Any): String {
        return value.toString()
    }

    override fun canConvert(cls: Class<*>): Boolean {
        return cls == Date::class
    }

}

class WeekdayConverter : Converter{
    override fun canConvert(cls: Class<*>): Boolean {
        return cls == Wochentag::class
    }

    override fun fromJson(jv: JsonValue): Any? {
        val jsonInt : Int? = jv.int
        if (jsonInt != null) {
            return Wochentag.values()[jsonInt-1]
        }
        return null
    }

    override fun toJson(value: Any): String {
        if(value is Wochentag){
            return (value.ordinal+1).toString()
        }
        return ""
    }

}