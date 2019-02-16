package info.frederico.mensaviewer.helper

import info.frederico.mensaviewer.MensaViewer
import info.frederico.mensaviewer.R
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class Essensplan(val today: List<Essen>? = ArrayList(), val nextday: List<Essen>? = ArrayList()){
    val jsonDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val outputDateFormat = SimpleDateFormat("EEEE, d. MMMM")
    val outputDateFormatWithoutWeekday = SimpleDateFormat("d. MMMM")

    fun getViewableEssensplan(): List<ViewableEssenElement>{
        val viewableEssensplan: MutableList<ViewableEssenElement> = ArrayList()
        if (today != null && !today.isEmpty()){
            val dateOfToday = jsonDateFormat.parse(today[0].date)
            viewableEssensplan.add(ViewableDateElement(MensaViewer.res.getString(R.string.today) + ", " + outputDateFormatWithoutWeekday.format(dateOfToday)))
            viewableEssensplan.addAll(today)
        }
        if (nextday != null && !nextday.isEmpty()){
            val dateOfNextDay = jsonDateFormat.parse(nextday[0].date)
            viewableEssensplan.add(ViewableDateElement(outputDateFormat.format(dateOfNextDay)))
            viewableEssensplan.addAll(nextday)
        }
        return viewableEssensplan
    }

    fun isEmpty(): Boolean{
        return today?.isEmpty() ?: true && nextday?.isEmpty() ?: true
    }
}