package info.frederico.mensaviewer.helper

import java.util.*
import kotlin.collections.ArrayList

class Essensplan(val today: List<Essen>? = ArrayList(), val nextday: List<Essen>? = ArrayList(), val dateOfNextDay: String? = ""){
    fun getViewableEssensplan(): List<ViewableEssenElement>{
        val viewableEssensplan: MutableList<ViewableEssenElement> = ArrayList()
        if (today != null && !today.isEmpty()){
            viewableEssensplan.add(ViewableDateElement("Heute"))
            viewableEssensplan.addAll(today)
        }
        if (nextday != null && !nextday.isEmpty()){
            viewableEssensplan.add(ViewableDateElement(dateOfNextDay.toString()))
            viewableEssensplan.addAll(nextday)
        }
        return viewableEssensplan
    }

    fun isEmpty(): Boolean{
        return today?.isEmpty() ?: true && nextday?.isEmpty() ?: true
    }
}