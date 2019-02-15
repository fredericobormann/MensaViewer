package info.frederico.mensaviewer.helper

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Essensplan(val today: List<Essen>? = ArrayList(), val nextday: List<Essen>? = ArrayList()){

    fun getViewableEssensplan(): List<ViewableEssenElement>{
        val viewableEssensplan: MutableList<ViewableEssenElement> = ArrayList()
        if (today != null && !today.isEmpty()){
            val dateOfToday = SimpleDateFormat("yyyy-MM-dd").parse(today[0].date)
            viewableEssensplan.add(ViewableDateElement("Heute"))
            viewableEssensplan.addAll(today)
        }
        if (nextday != null && !nextday.isEmpty()){
            val dateOfNextDay = SimpleDateFormat("yyyy-MM-dd").parse(nextday[0].date)
            viewableEssensplan.add(ViewableDateElement(dateOfNextDay.toString()))
            viewableEssensplan.addAll(nextday)
        }
        return viewableEssensplan
    }

    fun isEmpty(): Boolean{
        return today?.isEmpty() ?: true && nextday?.isEmpty() ?: true
    }
}