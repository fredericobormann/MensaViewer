package info.frederico.mensaviewer.helper.json

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue

class PriceConverter : Converter{
    override fun canConvert(cls: Class<*>): Boolean {
        return cls == String::class.java
    }

    override fun fromJson(jv: JsonValue): Any? {
        val jsonString: String? = jv.string
        if (jsonString != null){
            return jsonString.replace(".", ",")+" €"
        }
        else{
            return null
        }
    }

    override fun toJson(value: Any): String {
        return ""
    }
}